package com.study.taskscheduling.quartz._3job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

@SuppressWarnings("deprecation")
public class MyStatefulJob implements StatefulJob {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		int num = (Integer) dataMap.get("num");
		System.out.println("num in MyStatefulJob: " + num);
		num ++;
		//修改dataMap
		dataMap.put("num", num);
	}

}
