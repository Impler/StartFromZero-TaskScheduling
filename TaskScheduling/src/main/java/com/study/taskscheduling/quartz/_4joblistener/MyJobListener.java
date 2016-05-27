package com.study.taskscheduling.quartz._4joblistener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class MyJobListener implements JobListener{

	private String name;
	
	public MyJobListener(String name) {
		super();
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	/**
	 * 将会作为key存放在Scheduler的JobListener中
	 */
	public String getName() {
		return this.name;
	}

	@Override
	/**
	 * job被执行前
	 */
	public void jobToBeExecuted(JobExecutionContext context) {
		System.out.println("job listener do jobToBeExecuted");
	}

	@Override
	/**
	 * job被禁止
	 */
	public void jobExecutionVetoed(JobExecutionContext context) {
		System.out.println("job listener do jobExecutionVetoed");
	}

	@Override
	/**
	 * job执行后
	 */
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		System.out.println("job listener do jobWasExecuted");
	}

}
