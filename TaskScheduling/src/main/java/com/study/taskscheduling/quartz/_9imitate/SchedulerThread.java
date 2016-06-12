package com.study.taskscheduling.quartz._9imitate;

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

	public void togglePause(boolean pause){
		this.pause = pause;
	}
	@Override
	public void run() {

		while (!halt) {
			synchronized (sigLock) {
				//外部控制循环结束
				while (pause) {
					try {
						System.out.println("scheduler thread sigLock wait 1000ms");
						sigLock.wait(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(halt){
						break;
					}
				}
				
				System.out.println("do business");
				// 模拟业务耗时
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 检索待执行的triggers trigger.nextFireTime 时间范围 <= (当前时间  + idleTime)
				
				// 遍历trigger，触发规则时间
				
			}
		}
	}
}
