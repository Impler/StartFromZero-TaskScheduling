package com.study.taskscheduling.quartz._6schedulerlistener;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

public class MySchedulerListener implements SchedulerListener {

	@Override
	public void jobScheduled(Trigger trigger) {
		System.out.println("scheduler listener do jobScheduled: " + trigger.getKey());

	}

	@Override
	public void jobUnscheduled(TriggerKey triggerKey) {
		System.out.println("scheduler listener do jobUnscheduled: " + triggerKey);
	}

	@Override
	public void triggerFinalized(Trigger trigger) {
		System.out.println("scheduler listener do triggerFinalized: " + trigger.getKey());
	}

	@Override
	public void triggerPaused(TriggerKey triggerKey) {
		System.out.println("scheduler listener do triggerPaused: " + triggerKey);
	}

	@Override
	public void triggersPaused(String triggerGroup) {
		System.out.println("scheduler listener do triggersPaused: " + triggerGroup);
	}

	@Override
	public void triggerResumed(TriggerKey triggerKey) {
		System.out.println("scheduler listener do triggerResumed: " + triggerKey);
	}

	@Override
	public void triggersResumed(String triggerGroup) {
		System.out.println("scheduler listener do triggersResumed: " + triggerGroup);
	}

	@Override
	public void jobAdded(JobDetail jobDetail) {
		System.out.println("scheduler listener do jobAdded: " + jobDetail.getKey());
	}

	@Override
	public void jobDeleted(JobKey jobKey) {
		System.out.println("scheduler listener do jobDeleted: " + jobKey);
	}

	@Override
	public void jobPaused(JobKey jobKey) {
		System.out.println("scheduler listener do jobPaused: " + jobKey);
	}

	@Override
	public void jobsPaused(String jobGroup) {
		System.out.println("scheduler listener do jobsPaused: " + jobGroup);
	}

	@Override
	public void jobResumed(JobKey jobKey) {
		System.out.println("scheduler listener do jobResumed: " + jobKey);
	}

	@Override
	public void jobsResumed(String jobGroup) {
		System.out.println("scheduler listener do jobsResumed: " + jobGroup);
	}

	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		System.out.println("scheduler listener do schedulerError: " + msg);
	}

	@Override
	public void schedulerInStandbyMode() {
		System.out.println("scheduler listener do schedulerInStandbyMode");
	}

	@Override
	public void schedulerStarted() {
		System.out.println("scheduler listener do schedulerStarted");
	}

	@Override
	public void schedulerStarting() {
		System.out.println("scheduler listener do schedulerStarting");
	}

	@Override
	public void schedulerShutdown() {
		System.out.println("scheduler listener do schedulerShutdown");
	}

	@Override
	public void schedulerShuttingdown() {
		System.out.println("scheduler listener do schedulerShuttingdown");
	}

	@Override
	public void schedulingDataCleared() {
		System.out.println("scheduler listener do schedulingDataCleared");
	}

}
