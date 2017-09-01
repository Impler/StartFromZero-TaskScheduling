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
- 循环上述步骤

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
当`QuartzScheduler`对象创建并调用`start()`方法时，将唤醒QuartzSchedulerThread线程，即可跳出阻塞块，继续执行。  
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
Quartz未雨绸缪，从JobStore中获取当前时间后移一段时间内（idle time + time window）将要触发的Triggers，以及在当前时间前移一段时间内（misfireThreshold）错过触发的Triggers(这里仅查询Trigger的主要信息)。被查询到的Trggers状态变化：STATE_WAITING-->STATE_ACQUIRED。结果集是以触发时间升序、优先级降序的集合。  
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
因为上一步取得的Triggers是按时间排序的集合，所以取集合中的第一个，即触发时间最早的Trigger，等待其触发时间的到来。老套路while循环+wait实现。  
不过需要注意的是，这此期间，可能有一些新的情况发生，比如说，新增了一个Trigger，并且该新增的Trigger笔前面获取的触发时间都早，那么就需要将上面获取的过期的Trigger释放掉(状态变化:STATE_ACQUIRED-->STATE_WAITING)，然后重新查询Trggers
```java
now = System.currentTimeMillis();
long triggerTime = triggers.get(0).getNextFireTime().getTime();
long timeUntilTrigger = triggerTime - now;
// 当触发时间距当前时间<=2 ms时，结束循环
while(timeUntilTrigger > 2) {
    synchronized (sigLock) {
        if (halted.get()) {
            break;
        }
        // 判断在此过程中是否有新增的并且触发时间更早的Trigger
        // 但是此处有个权衡，为了一个新增的的Trigger而丢弃当前已获取的是否值得？
        // 丢弃当前获取的Trigger并重新获取需要花费一定的时间，时间的长短与JobStore的实现有关。
        // 所以此处做了主观判断，如果使用的是数据库存储，查询时间假定为70ms，内存存储假定为7ms
        // 如果当前时间距已获得的第一个Trigger触发时间小于查询时间，则认为丢弃是不合算的。
        if (!isCandidateNewTimeEarlierWithinReason(triggerTime, false)) {
            try {
                // we could have blocked a long while
                // on 'synchronize', so we must recompute
                now = System.currentTimeMillis();
                timeUntilTrigger = triggerTime - now;
                // 距触发时间太早，先休息会吧
                if(timeUntilTrigger >= 1)
                    sigLock.wait(timeUntilTrigger);
            } catch (InterruptedException ignore) {
            }
        }
    }
    // 如果有新增的且触发时间更早的Trigger过来搅局，则释放上面已获取的Trigger，等待下一波查询
    if(releaseIfScheduleChangedSignificantly(triggers, triggerTime)) {
        break;
    }
    now = System.currentTimeMillis();
    timeUntilTrigger = triggerTime - now;
}
```

## 4 触发Trigger
前面提到过，先前只是获取Trigger的主要信息，其关联的Job、Calendar等信息是在触发前获取的。待Trigger所需信息验证、关联完成后，先行将Trigger的状态改为STATE_ACQUIRED-->STATE_COMPLETE。而后将Trigger封装后的TriggerFiredResult对象交由JobRunShell执行。  
```java
List<TriggerFiredResult> res = qsRsrcs.getJobStore().triggersFired(triggers);
for (int i = 0; i < bndles.size(); i++) {
    TriggerFiredResult result =  bndles.get(i);
    TriggerFiredBundle bndle =  result.getTriggerFiredBundle();
    JobRunShell shell = qsRsrcs.getJobRunShellFactory().createJobRunShell(bndle);
    shell.initialize(qs);
    qsRsrcs.getThreadPool().runInThread(shell);
}
```