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

/**
 * quartz等多线程程序无法使用junit测试，只能通过main方法
 */
public class TestJobDataMap {

	public static void main(String[] args) throws SchedulerException {
		
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		Scheduler scheduler = schedulerFactory.getScheduler();
		
		SimpleScheduleBuilder scheBuilder = SimpleScheduleBuilder
				.simpleSchedule()
				.withIntervalInSeconds(1)
				.repeatForever();

		JobDataMap dataMap = new JobDataMap();
		dataMap.put("data", "data-value");
		
		//简单无状态job
		JobDetail simpleJob = JobBuilder.newJob(SimpleJob.class)
					.withIdentity("simpleJob", "default")
					.setJobData(dataMap)						//传递JobDataMap对象
					.usingJobData("data1", "data1-value")		//或单个添加data
					.usingJobData("data2", "data2-value")	
					.build();
		
		
		Trigger simpleTrigger = TriggerBuilder.newTrigger()
					.startNow()
					.withIdentity("simpleTrigger", "default")
					//trigger中也可以包含JobDataMap，在Job的execute方法中可以得到JobDetail中的JobDataMap和Trigger中的JobDataMap的并集，如果重复，后者覆盖前者
					.usingJobData("data2", "trigger-data2-value")		
					.usingJobData("data3", "trigger-data3-value")		
					.withSchedule(scheBuilder)
					.build();
		
		scheduler.scheduleJob(simpleJob, simpleTrigger);
		
		scheduler.start();
	}

}
