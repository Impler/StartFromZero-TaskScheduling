package com.study.taskscheduling.quartz._9imitate;

import java.util.ArrayList;
import java.util.List;

public class SchedulerThread extends Thread {

	private Object sigLock = new Object();

	// 该线程是否暂停
	private boolean pause;

	// 该线程是否终止
	private boolean halt;

	public SchedulerThread() {
		super();
		this.pause = true;
		this.halt = false;
	}

	public void togglePause(boolean pause) {
		this.pause = pause;
	}

	@Override
	public void run() {

		while (!halt) {
			synchronized (sigLock) {
				// 外部控制循环结束
				while (pause) {
					try {
						System.out
								.println("scheduler thread sigLock wait 1000ms");
						sigLock.wait(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (halt) {
						break;
					}
				}
			}
			System.out.println("start do business");
			// 检索待执行的triggers (当前时间-60s(miss fired)) <= trigger.nextFireTime <=
			// (当前时间 + 30s(wait fired))
			List<Trigger> triggers = getNextFireTriggers();
			// 遍历trigger
			long now = System.currentTimeMillis();
			long nextFireTime = triggers.get(0).getNextFireTime();
			long timeUntilTrigger = nextFireTime - now;
			System.out.println("1距离任务执行还有：" + timeUntilTrigger + "毫秒");
			while (timeUntilTrigger > 2) {
				synchronized (sigLock) {
					if (halt) {
						break;
					}
					// 重新计算时间，以求精确
					now = System.currentTimeMillis();
					timeUntilTrigger = nextFireTime - now;
					System.out.println("2距离任务执行还有：" + timeUntilTrigger + "毫秒");
					if (timeUntilTrigger >= 1) {
						try {
							System.out.println("3距离任务执行还有：" + timeUntilTrigger
									+ "毫秒， 等待。。。.");
							sigLock.wait(timeUntilTrigger);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				now = System.currentTimeMillis();
				timeUntilTrigger = nextFireTime - now;

			}
			System.out.println("4距离任务执行还有：" + timeUntilTrigger + "毫秒");
			System.out.println("执行任务。。。");
			break;
		}
	}

	/**
	 * 满足要求的触发器列表，按时间升序排列
	 * 
	 * @return
	 */
	public List<Trigger> getNextFireTriggers() {
		List<Trigger> triggers = new ArrayList<Trigger>();
		// 当前时间前20秒
		//triggers.add(new Trigger(System.currentTimeMillis() - 20 * 1000));
		// 当前时间后10秒
		triggers.add(new Trigger(System.currentTimeMillis() + 10 * 1000));
		return triggers;
	}

	class Trigger {
		private long nextFireTime;

		public Trigger(long nextFireTime) {
			super();
			this.nextFireTime = nextFireTime;
		}

		public long getNextFireTime() {
			return nextFireTime;
		}

		public void setNextFireTime(long nextFireTime) {
			this.nextFireTime = nextFireTime;
		}
	}
}
