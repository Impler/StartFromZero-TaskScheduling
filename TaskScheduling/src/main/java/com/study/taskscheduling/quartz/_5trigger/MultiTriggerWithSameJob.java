package com.study.taskscheduling.quartz._5trigger;

import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
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
import org.quartz.spi.OperableTrigger;

public class MultiTriggerWithSameJob {

	public static void main(String[] args) throws SchedulerException {
	
		SchedulerFactory factory = new StdSchedulerFactory();

		Scheduler scheduler = factory.getScheduler();
		
		JobDetail jobDetail = getJobDetail();
		JobKey jobKey = jobDetail.getKey();
		
		OperableTrigger tr1 = (OperableTrigger)getCronTrigger("0s,30s执行", "0,30 * * * * ?");
		OperableTrigger tr2 = (OperableTrigger)getSimpleTrigger("每秒一次，立即执行，执行到下一分钟");
		OperableTrigger tr3 = (OperableTrigger)getCronTrigger("从第三秒开始，每5秒一次", "30/5 * * * * ?");
	
		tr1.setJobKey(jobKey);
		tr2.setJobKey(jobKey);
		tr3.setJobKey(jobKey);
		
		scheduler.addJob(jobDetail , true);
		scheduler.scheduleJob(tr1);
		scheduler.scheduleJob(tr2);
		scheduler.scheduleJob(tr3);
		
		scheduler.start();
	}

	
	public static JobDetail getJobDetail() {
		JobKey jobKey = new JobKey("simpleJob" + (int) (Math.random() * 100),
				"defaultGroup");
		JobDetail job = JobBuilder
				.newJob(SimpleJob.class)
				.withIdentity(jobKey)
				//一定要设置
				.storeDurably()
				.build();
		return job;
	}
	
	
	/**
	 * 创建CronTrigger根据Cron表达式
	 * @param cronExpression
	 * @return
	 */
	public static Trigger getCronTrigger(String triggerName, String cronExpression) {
		return TriggerBuilder.newTrigger()
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.withIdentity(getTriggerKey(triggerName))
				.build();

	}


	public static TriggerKey getTriggerKey(String triggerName){
		return new TriggerKey(triggerName, "defaultGroup");
	}

	
	public static Trigger getSimpleTrigger(String triggerName) {

		SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule()
		// 间隔1秒
				.withIntervalInSeconds(1).repeatForever();

		return TriggerBuilder
				.newTrigger()
				.startNow()
				.withSchedule(builder)
				// 执行到下一分钟
				.endAt(DateBuilder.evenMinuteDateAfterNow())
				.withIdentity(getTriggerKey(triggerName))
				.build();
	}
}
