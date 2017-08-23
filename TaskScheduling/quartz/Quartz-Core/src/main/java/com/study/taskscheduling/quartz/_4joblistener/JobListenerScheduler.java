package com.study.taskscheduling.quartz._4joblistener;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.study.taskscheduling.quartz._1helloworld.HelloJob;

public class JobListenerScheduler {

	public static void main(String[] args) throws SchedulerException{
		
		SchedulerFactory factory = new StdSchedulerFactory();
		
		Scheduler scheduler = factory.getScheduler();
		
		JobDetail jobDetail = JobBuilder.newJob(HelloJob.class).withIdentity("helloJob", "defaultGroup").build();
		
		Trigger trigger = TriggerBuilder.newTrigger().startNow().build();
		
		scheduler.scheduleJob(jobDetail, trigger);
		
		scheduler.getListenerManager().addJobListener(new MyJobListener("myJobListener"));
		
		scheduler.start();
	}
}
