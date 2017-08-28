package com.study.taskscheduling.quartz._1helloworld;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class QuartzHelloWorld {

	public static void main(String[] args) throws SchedulerException {

		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory("_quartz.properties");

		Scheduler sched = schedFact.getScheduler();

		// define the job and tie it to our HelloJob class
		JobDetail job = JobBuilder.newJob(HelloJob.class).withIdentity("myJob", "group1").build();

		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		// Trigger the job to run now, and then every 40 seconds
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("myTrigger", "group1")
				.startNow()
				.withSchedule(
						SimpleScheduleBuilder.simpleSchedule()
								.withIntervalInSeconds(2).repeatForever())
				.build();

		// Tell quartz to schedule the job using our trigger
		sched.scheduleJob(job, trigger);
		sched.start();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sched.deleteJob(new JobKey("myJob", "group1"));
		System.out.println("myJob has been deleted");
		sched.shutdown();
	}
}
