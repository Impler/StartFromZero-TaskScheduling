package com.study.taskscheduling.quartz._2scheduler;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.study.taskscheduling.quartz._1helloworld.HelloJob;
import com.study.taskscheduling.quartz._6schedulerlistener.MySchedulerListener;

/**
 * 了解完job、jobdetail、trigger等再来看
 */
public class TestSchedulerOperateJobAndTrigger {

	public static void main(String[] args) throws SchedulerException {
		
		SchedulerFactory factory = new StdSchedulerFactory();

		Scheduler scheduler = factory.getScheduler();
		
		JobKey jobKey = new JobKey("job1", "default");
		
		JobDetail job = JobBuilder.newJob(HelloJob.class)
					.withIdentity(jobKey)
					.build();
		
		TriggerKey triggerKey = new TriggerKey("trigger1", "default");
		
		SimpleScheduleBuilder schBuilder = SimpleScheduleBuilder.simpleSchedule()
					.withIntervalInSeconds(1)
					.repeatForever();
		
		Trigger trigger = TriggerBuilder.newTrigger()
					.forJob(job)
					.withIdentity(triggerKey)
					.withSchedule(schBuilder)
					.build();
		
		scheduler.scheduleJob(job, trigger);
		// 为了方便观察
		scheduler.getListenerManager().addSchedulerListener(new MySchedulerListener());
		
		scheduler.start();
		
		// JOB pause and resume
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		scheduler.pauseJob(jobKey);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		scheduler.resumeJob(jobKey);
		
		// Trigger pause and resume
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		scheduler.pauseTrigger(triggerKey);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		scheduler.resumeTrigger(triggerKey);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		scheduler.deleteJob(jobKey);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		scheduler.shutdown();
	}

}
