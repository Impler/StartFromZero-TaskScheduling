# QuartzSchedulerThread详解

QuartzSchedulerThread是一个线程类，负责查询并触发Triggers。  

```java
public class QuartzSchedulerThread extends Thread {
    QuartzSchedulerThread(QuartzScheduler qs, QuartzSchedulerResources qsRsrcs, boolean setDaemon, int threadPrio) {
        super(qs.getSchedulerThreadGroup(), qsRsrcs.getThreadName());
        ........
        paused = true;
        halted = new AtomicBoolean(false);
    }
}
```

该线程类的主要工作分为以下几个步骤：  
- 等待QuartzScheduler启动
- 查询待触发的Trigger
- 等待Trigger触发时间到来
- 触发Trigger
- 循环

```java
/*-----------------run()方法有删减----------------------*/
public void run() {
    while (!halted.get()) {
        // -------------------------------
        // 1 等待QuartzScheduler启动
        // -------------------------------
        synchronized (sigLock) {
            while (paused && !halted.get()) {
                // wait until togglePause(false) is called...
                sigLock.wait(1000L);
            }
        }

        // -------------------------------
        // 2 查询待触发的Trigger
        // -------------------------------
        int availThreadCount = qsRsrcs.getThreadPool().blockForAvailableThreads();
        if(availThreadCount > 0) { // will always be true, due to semantics of blockForAvailableThreads...
            // 查询未来（now + idletime）时间内待触发的Triggers
            // triggers是按触发时间由近及远排序的集合
            List<OperableTrigger> triggers = qsRsrcs.getJobStore().acquireNextTriggers(
                    now + idleWaitTime, Math.min(availThreadCount, qsRsrcs.getMaxBatchSize()), qsRsrcs.getBatchTimeWindow());
            if (triggers != null && !triggers.isEmpty()) {
                now = System.currentTimeMillis();
                long triggerTime = triggers.get(0).getNextFireTime().getTime();
                long timeUntilTrigger = triggerTime - now;
                // 通过循环阻塞，等待第一个Trigger触发时间
                while(timeUntilTrigger > 2) {
                    synchronized (sigLock) {
                        if (halted.get()) {
                            break;
                        }
                    }
                    now = System.currentTimeMillis();
                    timeUntilTrigger = triggerTime - now;
                }
            // 通知JobStore，这些Triggers将要被触发
            List<TriggerFiredResult> res = qsRsrcs.getJobStore().triggersFired(triggers);
            if(res != null)
                bndles = res;
            }
            // -------------------------------
            // 3 触发Triggers
            // -------------------------------
            for (int i = 0; i < bndles.size(); i++) {
                TriggerFiredResult result =  bndles.get(i);
                TriggerFiredBundle bndle =  result.getTriggerFiredBundle();
                JobRunShell shell = qsRsrcs.getJobRunShellFactory().createJobRunShell(bndle);
                shell.initialize(qs);
                qsRsrcs.getThreadPool().runInThread(shell);
            }
            continue; // while (!halted)
        } else { // if(availThreadCount > 0)
            // should never happen, if threadPool.blockForAvailableThreads() follows contract
            continue; // while (!halted)
        }
    } // while (!halted)
}
```

## 1 等待QuartzScheduler启动
```java
synchronized (sigLock) {
    while (paused && !halted.get()) {
        // wait until togglePause(false) is called...
        sigLock.wait(1000L);
    }
}
```
循环检查`paused && !halted.get()`条件是否满足，否则释放sigLock对象的锁，并等待，一秒后重试。  
当`QuartzScheduler`对象启动，即调用`start()`方法时，将唤醒QuartzSchedulerThread线程，即可跳出阻塞块，继续执行。  
```java
/*QuartzScheduler*/
public void start() throws SchedulerException {
    ....
    schedThread.togglePause(false);
    ....
}

/*QuartzSchedulerThread*/
void togglePause(boolean pause) {
    synchronized (sigLock) {
        // 更改暂停状态
        paused = pause;
        if (paused) {
            signalSchedulingChange(0);
        } else {
            // 唤醒在sigLock上等待的所有线程
            sigLock.notifyAll();
        }
    }
}
```

## 2 查询待触发的Trigger
Quartz未雨绸缪，从JobStore中获取当前时间后移一段时间内（idle time + time window）将要触发的Triggers，以及在当前时间前移一段时间内（misfireThreshold）错过触发的Triggers。结果集是以触发时间升序、优先级降序的集合。  
```java
public List<TriggerKey> selectTriggerToAcquire(Connection conn, long noLaterThan, long noEarlierThan, int maxCount)
        throws SQLException {
}
```

```sql
SELECT
	TRIGGER_NAME,
	TRIGGER_GROUP,
	NEXT_FIRE_TIME,
	PRIORITY
FROM
	QRTZ_TRIGGERS
WHERE
	SCHED_NAME = 'TestScheduler'
AND TRIGGER_STATE = ?
AND NEXT_FIRE_TIME <= ?
AND (
	MISFIRE_INSTR = - 1
	OR (
		MISFIRE_INSTR != - 1
		AND NEXT_FIRE_TIME >= ?
	)
)
ORDER BY
	NEXT_FIRE_TIME ASC,
	PRIORITY DESC
```

## 3 等待Trigger触发时间到来

## 4 触发Trigger