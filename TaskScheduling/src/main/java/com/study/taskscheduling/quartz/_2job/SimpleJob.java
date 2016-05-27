package com.study.taskscheduling.quartz._2job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SimpleJob implements Job{
	
	private String data;

	// 如果JobDataMap中包含相对应的key值，该setter方法将会在该对象创建后调用
	public void setData(String data) {
		this.data = data;
	}


	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// 获取JobDetail中的JobDataMap
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		
		// 获取JobDetail中的JobDataMap与Trigger中的JobDataMap的并集，如果key重复，后者覆盖前者
		JobDataMap mergeDataMap = context.getMergedJobDataMap();
		
		// 只在当前方法中起效
		dataMap.put("data1", "data1-value-modify");
		
		System.out.println(dataMap + ":" + this.data + "," 
				+ dataMap.getString("data1") + "," 
				+ dataMap.getString("data2"));
		
		System.out.println(mergeDataMap + ":" + this.data + "," 
				+ mergeDataMap.getString("data1") + "," 
				+ mergeDataMap.getString("data2") + "," 
				+ mergeDataMap.getString("data3"));
		
		System.out.println();
		
		//在Job中修改JobDataMap并没有什么乱用
		dataMap.put("data3", "data3-value-modify");
	}

}
