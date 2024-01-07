package com.mpwork.monitor.service;

import java.util.List;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mframework.common.MParam;
import org.mframework.service.MFrameworkService;
import org.mframework.utils.MResult;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mpwork.monitor.bean.DeviceNode;
import com.mpwork.monitor.common.jobexec.JobBean;
import com.mpwork.monitor.common.jobexec.QuartzJob;

import lombok.Getter;

/**
 * 播测任务定时触发服务
 * @author dbb
 *
 */
@Service
@Getter
public class JobScheduleService {

	private static final Log logger = LogFactory.getLog(JobScheduleService.class);
	@Autowired
	private MFrameworkService mframeworkservice;
	
	private SchedulerFactory schedulerFactory;
	
	public JobScheduleService() {
		try {
			schedulerFactory = new StdSchedulerFactory("mpwork-quartz.properties");
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	// PostProcessor 中启动
	@Async
	public void startSchedulerAndJobs() {
		// 延迟10秒，等应用、数据通道启动完成
		try {
			Thread.sleep(15000);
		}catch(Exception e) {}
		
		try {
			// 调度器开始调度任务
			Scheduler scheduler = schedulerFactory.getScheduler();
	        scheduler.start();
	        
	        MParam mparam = new MParam("status", 1);	// 激活状态
	        List<JobBean> jobBeanList = mframeworkservice.listAll(DeviceNode.class, mparam);
	        for(JobBean jobBean : jobBeanList) {
	        	this.deployJob(jobBean);
	        }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
    public void cleanUp() throws Exception {
		Scheduler scheduler = schedulerFactory.getScheduler();
		scheduler.shutdown();
		System.out.println("cleanUp...");
    }

	public MResult deployJob(JobBean jobBean) {
    	String cron = jobBean.getCron();	// "0/10 * * * * ? *"
    	if(StringUtils.isEmpty(cron)) {
    		return MResult.build_A00003("未设定cron参数");
    	}
    	
        // 将任务及其触发器放入调度器
        try {
	        Scheduler scheduler = schedulerFactory.getScheduler();
	        
	        JobKey jobKey = jobBean.getJobKey();
	        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
	        if(jobDetail!=null) {
	        	// 已存在，运行中
	        	return MResult.buildOK();
	        }
	        
        	jobDetail = JobBuilder.newJob(QuartzJob.class)
        			.usingJobData("deviceid", jobBean.getJobPkid())
        			.withIdentity(jobKey)
            		.build();
	        	
            
            //建立触发器 每3秒钟执行一次
            Trigger trigger = TriggerBuilder.newTrigger()
            	// .withIdentity("trigger1", "group3")
        		.withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing())
        		.build();
	        
	        scheduler.scheduleJob(jobDetail, trigger);
	        return MResult.buildOK();
        } catch(Exception e) {
        	e.printStackTrace();
        	return MResult.build_A00005(e.getMessage());
        }
	}

	public void removeJob(JobBean jobBean) {
		try {
			Scheduler scheduler = schedulerFactory.getScheduler();
			
			boolean res = scheduler.deleteJob(jobBean.getJobKey());
			System.out.println("removeJob, res=" + res);
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
}
