package com.study.taskscheduling.quartz._9imitate;

public class SchedulerController {

	public static void main(String[] args) {
		
		SchedulerThread thread = new SchedulerThread();
		
		thread.start();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("结束暂停");
		thread.togglePause(false);
		
		/*// n秒后，暂定任务
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		thread.togglePause(true);*/
	}

}
