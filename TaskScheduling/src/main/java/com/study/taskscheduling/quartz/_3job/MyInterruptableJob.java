package com.study.taskscheduling.quartz._3job;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyInterruptableJob implements InterruptableJob {
	
	final Logger log = LoggerFactory.getLogger(MyInterruptableJob.class);
	// 标记当前Job
	private String flag;
	// 标记是否收到中断请求
	private boolean isInterrupted = false;
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		flag = String.valueOf(System.currentTimeMillis()).substring(6);
		log.info(Thread.currentThread().getName() + " " + flag + " start to execute....");
		
		if(isInterrupted){
			log.info(Thread.currentThread().getName() + " " + flag + " is interrepted, return....");
			return;
		}else{
			//doBusinessWork
			try {
				//模拟现实场景
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		log.info(Thread.currentThread().getName() + " " + flag + " stop execute....");
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		isInterrupted = true;
		log.info(Thread.currentThread().getName() + " " + flag + " is interrupted");
	}

}
