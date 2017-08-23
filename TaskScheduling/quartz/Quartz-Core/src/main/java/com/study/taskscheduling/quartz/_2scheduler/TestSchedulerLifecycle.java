package com.study.taskscheduling.quartz._2scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class TestSchedulerLifecycle {

	public static void main(String[] args) throws SchedulerException {

		SchedulerFactory factory = new StdSchedulerFactory();
		/*
		 * Scheduler由SchedulerFactory工厂创建
		 * scheduler的创建过程包括：
		 * 1. 读取配置文件, 配置文件中需要配置scheduler、线程池、jobStore、jobListener、triggerListenner、插件等。配置文件的读取过程如下：
		 * - 读取参数系统参数System中配置的org.quartz.properties指定的文件
		 * - 如果找不到则读取项目Classpath目录下的quartz.properties配置文件
		 * - 如果找不到则读取jar包中默认的配置文件quartz.properties
		 * 2. 从SchedulerRepository中根据名称读取已经创建的scheduler，
		 * 3. 如果没有则重新创建一个，并保存在SchedulerRepository中。
		 */
		Scheduler scheduler = factory.getScheduler();
		
		//register job/trigger/calendar/listener
		
		/*
		 * 启动scheduler 
		 */
		scheduler.start();
		
		/*
		 * 暂停scheduler，trigger将不会被触发。可随时调用start()方法唤醒
		 */
		scheduler.standby();
		
		scheduler.start();
		
		/*
		 * 停止scheduler
		 */
		scheduler.shutdown();
		
	}

}
