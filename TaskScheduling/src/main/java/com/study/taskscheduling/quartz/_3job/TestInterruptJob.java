package com.study.taskscheduling.quartz._3job;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class TestInterruptJob {

	public static void main(String[] args) throws SchedulerException{
		
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		
		Scheduler scheduler = schedulerFactory.getScheduler();
		
		JobKey jobKey = new JobKey("interruptableJob", "default");
		
		JobDetail job = JobBuilder.newJob(MyInterruptableJob.class)
					.withIdentity(jobKey)
					.build();
		
		SimpleScheduleBuilder scheBuilder = SimpleScheduleBuilder
					.simpleSchedule()
					.withIntervalInSeconds(3)
					.repeatForever();
		
		Trigger trigger = TriggerBuilder.newTrigger()
				.startNow()
				.withIdentity("trigger", "default")
				.withSchedule(scheBuilder)
				.build();
		
		
		scheduler.scheduleJob(job, trigger);
		
		scheduler.start();
		
		for(int i=0; i<20; i++){
			try {
				Thread.sleep(3000);
				scheduler.interrupt(jobKey);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
