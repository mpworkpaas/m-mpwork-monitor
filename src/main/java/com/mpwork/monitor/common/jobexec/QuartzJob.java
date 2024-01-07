package com.mpwork.monitor.common.jobexec;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mframework.base.MEnvironment;
import org.mframework.utils.MModuleSpringUtils;
import org.mframework.utils.MSyncLock;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mpwork.apputils.service.CommonAppConfigServiceV2;
import com.mpwork.monitor.bean.AppConfig;
import com.mpwork.monitor.service.TestingService;
import com.mpwork.monitor.utils.TestingUtils;
import com.mpwork.monitor.utils.TestingUtils.PingResult;

/**
 * 定时任务
 * @author dbb
 * 
 */
public class QuartzJob implements Job{

	private static final Log logger = LogFactory.getLog(QuartzJob.class);
	
	// private static Map<String, Object> lockMap = new HashMap<>();

	public void execute(JobExecutionContext jobContext) throws JobExecutionException {
		long intime = System.currentTimeMillis();
		JobDataMap jobDataMap = jobContext.getMergedJobDataMap();
		String deviceid = (String) jobDataMap.get("deviceid");
//		Object lockobj = lockMap.get(datajobid);
//		if(lockobj==null) {
//			lockobj = new Object();
//			lockMap.put(datajobid, lockobj);
//		}
		
		
		// 通过对像锁实现不同的datajobid，在同时，不会多重执行
		String syncKey = "QUARTZ:" + deviceid;
		Object lockobj = MSyncLock.getLockObject(syncKey);
	    synchronized (lockobj) {
			long holdtime = System.currentTimeMillis() - intime;
			if(holdtime>1000) {
				// 根据锁hold时间，判定是否为被阻塞的执行触发，如是将取消执行，实现触发时如有未执行的将跳过执行。
				logger.info("cancel by hold exec! deviceid=" + deviceid + " holdtime=" + holdtime);
				return;
			}
			
			// 20231220
			// 播测前监控系统网络可用性自检（避免服务端网络波动导致误报）
			CommonAppConfigServiceV2 commonAppconfigService = MModuleSpringUtils.getBean(CommonAppConfigServiceV2.class);
			AppConfig appconfig = commonAppconfigService.getUniConfig(AppConfig.class);
			String selftest_pinghost = appconfig.getTesting_selftest_pinghost();
			if(!StringUtils.isEmpty(selftest_pinghost)) {
				PingResult pingResult = TestingUtils.ping(selftest_pinghost);
				if(!pingResult.isSuccess()) {
					logger.warn("IGNORE BY MONITOR-SERVICE NETWORK UNSTABITILY! selftest_pinghost=" + selftest_pinghost + " deviceid=" + deviceid);
					return;
				}
			}
			
			// 触发任务执行
			TestingService testingService = MModuleSpringUtils.getBean(TestingService.class);
			testingService.doTesting(deviceid);
		}
	}
	
}