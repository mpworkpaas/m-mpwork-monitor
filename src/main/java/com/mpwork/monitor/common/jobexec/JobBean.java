package com.mpwork.monitor.common.jobexec;

import org.quartz.JobKey;

/**
 * 任务对像接口
 * @author dbb
 *
 */
public interface JobBean {

	public JobKey getJobKey();

	public String getJobPkid();

	public String getCron();
	
}
