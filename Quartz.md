# Quartz

## Quartz 主要API
- Scheduler 
- Job 一个接口，通过实现该接口定义需要执行的任务
- JobDetail 用于定义Job实例
- Trigger 描述Job执行的时间触发规则
- JobBuilder 用于定义或创建JobDetail实例
- TriggerBuilder 用于定义或创建Trigger实例

-----------------Draft
Scheduler由SchedulerFactory创建，生命周期结束于shutdown()方法.任何计划任务开始于start()方法。
当Trigger被触发时，Scheduler的某个执行线程将会调用Job的execute(JobExecutionContext)方法。JobExecutionContext 包含一些运行时环境信息：包括调用该Job的Scheduler、Trigger、JobDetail以及其他信息。
JobDetail在Job被添加到Schedler时被创建出来，用于保存Job的状态信息
Trigger用来触发调用Job执行。常见的两种Trigger为SimpleTrigge和CronTrigger. 
SimpleTrigger一般为在特定点一次性执行或延迟执行N次
CronTrigger支持Cron表达式
Job可以独立于Trigger存储于Scheduler中，一个Job可以关联多个Trigger.Job和Trigger相互对立，互相解耦。可以重新配置Job的Trigger当之前的Trigger过期或失效。可以修改一个Trigger而不用重新定义相关的Job
Job和Trigger在Scheduler都有唯一的标识符，在同一个group下唯一

