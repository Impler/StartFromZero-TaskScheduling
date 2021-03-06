package com.study.taskscheduling.quartz._6schedulerlistener;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.study.taskscheduling.quartz._1helloworld.HelloJob;
import com.study.taskscheduling.quartz._4joblistener.MyJobListener;
import com.study.taskscheduling.quartz._7triggerlistener.MyTriggerListener;

public class SchedulerListener {

	public static void main(String[] args) throws SchedulerException {
		SchedulerFactory factory = new StdSchedulerFactory();

		Scheduler scheduler = factory.getScheduler();
		
		scheduler.getListenerManager().addSchedulerListener(new MySchedulerListener());;
		
		JobDetail jobDetail = JobBuilder.newJob(HelloJob.class).withIdentity("helloJob", "defaultGroup").build();

		Trigger trigger = TriggerBuilder.newTrigger().startNow().build();

		scheduler.scheduleJob(jobDetail, trigger);

		scheduler.getListenerManager().addJobListener(new MyJobListener("myJobListener"));
		
		scheduler.getListenerManager().addTriggerListener(new MyTriggerListener("myTriggerListener"));
		
		scheduler.start();
	}

}
