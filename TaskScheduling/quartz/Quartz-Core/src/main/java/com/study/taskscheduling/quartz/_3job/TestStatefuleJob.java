package com.study.taskscheduling.quartz._3job;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class TestStatefuleJob {

	public static void main(String[] args) throws SchedulerException{
		
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		
		Scheduler scheduler = schedulerFactory.getScheduler();
		
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("data", "data-value");
		dataMap.put("num", 1);
		
		//有状态job
		JobDetail stateJob = JobBuilder.newJob(MyStatefulJob.class)
					.withIdentity("stateJob", "default")
					.setJobData(dataMap)
					.build();
		
		SimpleScheduleBuilder scheBuilder = SimpleScheduleBuilder
					.simpleSchedule()
					.withIntervalInSeconds(3)
					.repeatForever();
		
		Trigger stateTrigger = TriggerBuilder.newTrigger()
				.startNow()
				.withIdentity("stateTrigger", "default")
				.withSchedule(scheBuilder)
				.build();
		
		
		scheduler.scheduleJob(stateJob, stateTrigger);
		
		scheduler.start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("stateful job num：" + dataMap.get("num"));
	}
}
