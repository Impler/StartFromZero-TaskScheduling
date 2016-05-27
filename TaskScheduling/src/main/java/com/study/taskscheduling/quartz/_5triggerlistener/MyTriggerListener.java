package com.study.taskscheduling.quartz._5triggerlistener;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;

public class MyTriggerListener implements TriggerListener {

	private String name;
	
	public MyTriggerListener(String name) {
		super();
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	/**
	 * 将会作为key存放在Scheduler中的TriggerListener中
	 */
	public String getName() {
		return this.name;
	}

	@Override
	/**
	 * trigger被触发
	 */
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		System.out.println("trigger listener do triggerFired");
	}

	@Override
	/**
	 * trigger被禁止触发
	 */
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		return false;
	}

	@Override
	/**
	 * trigger没来得及触发
	 */
	public void triggerMisfired(Trigger trigger) {
		System.out.println("trigger listener do triggerMisfired");
	}

	@Override
	/**
	 * trigger执行完成
	 */
	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode) {
		System.out.println("trigger listener do triggerComplete");
	}

}
