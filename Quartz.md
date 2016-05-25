# Quartz

## Quartz 主要API
- Scheduler 
- Job 一个接口，通过实现该接口定义需要执行的任务
- JobDetail 用于定义Job实例
- Trigger 描述Job执行的时间触发规则
- JobBuilder 用于定义或创建JobDetail实例
- TriggerBuilder 用于定义或创建Trigger实例