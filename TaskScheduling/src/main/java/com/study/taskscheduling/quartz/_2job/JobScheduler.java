package com.study.taskscheduling.quartz._2job;

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

public class JobScheduler {

	public static void main(String[] args) throws SchedulerException{
		
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		
		Scheduler scheduler = schedulerFactory.getScheduler();
		
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("data", "data-value");
		
		JobDetail job = JobBuilder.newJob(SimpleJob.class)
					.withIdentity("simpleJob", "default")
					.setJobData(dataMap)						//传递JobDataMap对象
					.usingJobData("data1", "data1-value")		//或单个添加data
					.usingJobData("data2", "data2-value")	
					.build();
		
		SimpleScheduleBuilder scheBuilder = SimpleScheduleBuilder
					.simpleSchedule()
					.withIntervalInSeconds(3)
					.repeatForever();
		
		Trigger trigger = TriggerBuilder.newTrigger()
					.startNow()
					.withIdentity("simpleTrigger", "default")
					//trigger中也可以包含JobDataMap，在Job的execute方法中可以得到JobDetail中的JobDataMap和Trigger中的JobDataMap的并集，如果重复，后者覆盖前者
					.usingJobData("data2", "trigger-data2-value")		
					.usingJobData("data3", "trigger-data3-value")		
					.withSchedule(scheBuilder)
					.build();
		
		scheduler.scheduleJob(job, trigger);
		
		scheduler.start();
		
		System.out.println("比较JobDetail创建时绑定的JobDataMap与Job中得到的是否为同一个对象：" + job.getJobDataMap() + "====显然不是");
	}
}
