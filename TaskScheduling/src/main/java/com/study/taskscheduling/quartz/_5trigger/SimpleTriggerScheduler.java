package com.study.taskscheduling.quartz._5trigger;

import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
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

public class SimpleTriggerScheduler {

	public static void main(String[] args) throws SchedulerException{
		
		SchedulerFactory factory = new StdSchedulerFactory();
		
		Scheduler scheduler = factory.getScheduler();
		
//		scheduler.scheduleJob(getJobDetail(), getTrigger1("立即执行一次"));
//		scheduler.scheduleJob(getJobDetail(), getTrigger2("2秒后执行一次"));
//		scheduler.scheduleJob(getJobDetail(), getTrigger3("每隔3秒，重复5次立即执行"));
		scheduler.scheduleJob(getJobDetail(), getTrigger4("每秒一次，立即执行，执行到下一分钟"));
		
		scheduler.start();
	}
	
	
	public static JobDetail getJobDetail(){
		JobKey jobKey = new JobKey("simpleJob" + (int)(Math.random()*100), "defaultGroup");
		JobDetail job = JobBuilder.newJob(SimpleJob.class)
				.withIdentity(jobKey)
				.build();
		return job;
	}
	
	public static TriggerKey getTriggerKey(String triggerName){
		return new TriggerKey(triggerName, "defaultGroup");
	}
	/**
	 * 立即执行一次
	 * @return
	 */
	public static Trigger getTrigger1(String triggerName) {
		return TriggerBuilder.newTrigger()
				.withIdentity(getTriggerKey(triggerName))
				.build();
	}
	
	/**
	 * 2秒后执行一次
	 * @return
	 */
	public static Trigger getTrigger2(String triggerName) {
		
		return TriggerBuilder.newTrigger()
				.startAt(DateBuilder.futureDate(2, IntervalUnit.SECOND))
				.withIdentity(getTriggerKey(triggerName))
				.build();
	}
	/**
	 * 每隔3秒，重复5次立即执行
	 * @return
	 */
	public static Trigger getTrigger3(String triggerName) {
		
		SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule()
				//间隔3秒
				.withIntervalInSeconds(3)
				//执行10次
				.withRepeatCount(5);
		
		return TriggerBuilder.newTrigger().startNow()
				.withIdentity(getTriggerKey(triggerName))
				.withSchedule(builder).build();

	}
	
	/**
	 * 每秒一次，立即执行，执行到下一分钟
	 * @return
	 */
	public static Trigger getTrigger4(String triggerName) {
		
		SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule()
				//间隔1秒
				.withIntervalInSeconds(1)
				.repeatForever();
		
		return TriggerBuilder.newTrigger().startNow()
				.withSchedule(builder)
				//执行到下一分钟
				.endAt(DateBuilder.evenMinuteDateAfterNow())
				.withIdentity(getTriggerKey(triggerName))
				.build();
	}
}
