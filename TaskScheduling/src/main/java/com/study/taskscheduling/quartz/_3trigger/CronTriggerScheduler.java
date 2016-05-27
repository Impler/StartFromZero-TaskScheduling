package com.study.taskscheduling.quartz._3trigger;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

public class CronTriggerScheduler {
	public static void main(String[] args) throws SchedulerException {

		SchedulerFactory factory = new StdSchedulerFactory();

		Scheduler scheduler = factory.getScheduler();

		//每分钟执行一次
//		scheduler.scheduleJob(getJobDetail(), getTrigger("每分钟执行一次", "0 * * * * ?"));
		//0s,30s执行
//		scheduler.scheduleJob(getJobDetail(), getTrigger("0s,30s执行", "0,30 * * * * ?"));
		//从第三秒开始，每5秒一次
//		scheduler.scheduleJob(getJobDetail(), getTrigger("从第三秒开始，每5秒一次", "30/5 * * * * ?"));
		
		//每天16点50分执行
		scheduler.scheduleJob(getJobDetail(), getTrigger("每天16点50分执行一次"));
		
		scheduler.start();
	}


	public static JobDetail getJobDetail() {
		JobKey jobKey = new JobKey("simpleJob" + (int) (Math.random() * 100),
				"defaultGroup");
		JobDetail job = JobBuilder.newJob(SimpleJob.class).withIdentity(jobKey)
				.build();
		return job;
	}

	public static TriggerKey getTriggerKey(String triggerName){
		return new TriggerKey(triggerName, "defaultGroup");
	}
	
	/**
	 * 创建CronTrigger根据Cron表达式
	 * @param cronExpression
	 * @return
	 */
	public static Trigger getTrigger(String triggerName, String cronExpression) {
		return TriggerBuilder.newTrigger()
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.withIdentity(getTriggerKey(triggerName))
				.build();

	}

	public static Trigger getTrigger(String triggerName) {
		return TriggerBuilder.newTrigger()
				//每天16点50分执行一次，同 0 50 16 * * ?
				.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(16, 50))
				.withIdentity(getTriggerKey(triggerName))
				.build();

	}
}
