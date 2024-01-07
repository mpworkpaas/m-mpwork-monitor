package com.mpwork.monitor.service;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mframework.service.MFrameworkService;
import org.mframework.utils.CommonUtils;
import org.mframework.utils.MResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mpwork.apputils.service.CommonAppConfigServiceV2;
import com.mpwork.monitor.bean.AppConfig;
import com.mpwork.monitor.bean.DeviceNode;
import com.mpwork.monitor.utils.TcpPortChecker;
import com.mpwork.monitor.utils.TestingUtils;
import com.mpwork.monitor.utils.TestingUtils.PingResult;
import com.mpwork.monitor.utils.UrlValidator;

/**
 * 播测服务
 * @author dbb
 *
 */
@Service
public class TestingService {

	private static final Log logger = LogFactory.getLog(TestingService.class);
	@Autowired
	private MFrameworkService mframeworkservice;
	@Autowired
	private WarnService warnservice;
	@Autowired
	private CommonAppConfigServiceV2 commonAppconfigService;
	
	// 执行播测
    public void doTesting(String deviceid) {
    	DeviceNode deviceNode = mframeworkservice.get(DeviceNode.class, deviceid);
    	doTesting(deviceNode);
    }
	
	// 执行播测
    public MResult doTesting(DeviceNode deviceNode) {
    	if(deviceNode==null) {
    		logger.error("doTesting, invalid deviceNode!");
    		return MResult.build_A00003("invalid deviceNode");
    	}

    	AppConfig appconfig = commonAppconfigService.getUniConfig(AppConfig.class);
    	
		// 执行播测
		String mtype = deviceNode.getMonitortype();
		if("PING".equalsIgnoreCase(mtype)) {
			return doPingTesting(appconfig, deviceNode);
		} else if("CURL".equalsIgnoreCase(mtype)) {
			return doUrlTesting(appconfig, deviceNode);
		} else if("TCP".equalsIgnoreCase(mtype)) {
			return doTcpTesting(appconfig, deviceNode);
		} else {
			return MResult.build_A00007("UNKNOW MTYPE:" + mtype);
		}
    }
    
    
    public MResult doTcpTesting(AppConfig appconfig, DeviceNode deviceNode) {
    	int consolelog = appconfig.getTesting_consolelog();
    	
    	String[] tcpHostData = deviceNode.getTcphost().trim().split(":");
    	if(tcpHostData.length!=2)
    		return MResult.build_A00003("tcphost格式错误");
    	String tcpHostname = tcpHostData[0];
    	int tcpPort = Integer.parseInt(tcpHostData[1]);
    	
    	
    	MResult tcpResult = TcpPortChecker.checkTcp(tcpHostname, tcpPort);
		if(!tcpResult.isOk()) {
			if(consolelog==1)
				logger.warn("doTcpTesting, TCPCHECK FAIL! deviceNode=" + deviceNode + " tcpHost=" + deviceNode.getTcphost());
			
			// 记录并触发告警
			String warnmsg = "TCP端口不通";
			warnservice.triggerWarn(deviceNode, warnmsg, 3, 1);		// 1小时可重复提醒3次
			return MResult.build_A00005("TCP测试异常");
		}
		
		// 播测成功
		if(deviceNode.getWarnstatus()==1) {
			warnservice.restoreWarnStatus(deviceNode);
		}
		return MResult.buildOK();
    }
    
    
    public MResult doPingTesting(AppConfig appconfig, DeviceNode deviceNode) {
    	int consolelog = appconfig.getTesting_consolelog();
    	
    	PingResult pingResult = TestingUtils.ping(deviceNode.getIp());
		if(!pingResult.isSuccess()) {
			if(consolelog==1)
				logger.warn("doTesting, PING FAIL! deviceNode=" + deviceNode + " pingResult-roundTime=" + pingResult.getRoundTripTime());
			
			// 记录并触发告警
			int countinhour = deviceNode.getWarn_notify_countinhour();
			if(countinhour<=0)
				countinhour = 3;
			warnservice.triggerWarnByPing(deviceNode, pingResult, countinhour, 1);		// 1小时可重复提醒3次
			MResult mres = MResult.build_A00005("PING测试异常");
			mres.putParam("warnmsg", pingResult.getOutput());
			return mres;
		}
		
		// 播测成功
		if(deviceNode.getWarnstatus()==1) {
			warnservice.restoreWarnStatus(deviceNode);
		}
		MResult mres = MResult.buildOK();
		mres.putParam("warnmsg", pingResult.getOutput());
		return mres;
    }
    
    public MResult doUrlTesting(AppConfig appconfig, DeviceNode deviceNode) {
    	int consolelog = appconfig.getTesting_consolelog();
    	int SSL_EXPIRE_LEFTDAY = appconfig.getSsl_expire_leftday();
    	if(SSL_EXPIRE_LEFTDAY<=0)
    		SSL_EXPIRE_LEFTDAY = 30;
    	
		// URL可访问性检测
		String url = deviceNode.getUrl().trim();
		MResult accessMResult = UrlValidator.checkUrlAccessible(url);
		if(accessMResult.isOk()) {
			int httpcode = accessMResult.getParamT("httpcode");
			
			if(httpcode==200) {
				// TODO 后期可加入针200/404/502 等状态检测识别
				// TODO
			}
		} else {
			if(consolelog==1)
				logger.warn("doTesting, UrlValidator FAIL! deviceNode=" + deviceNode + " url=" + url);
			
			// 记录并触发告警
			String warnmsg = "URL无法被访问";
			warnservice.triggerWarn(deviceNode, warnmsg, 3, 1);	// 1小时可重复提醒3次
			return MResult.build_A00005(warnmsg);
		}
		
		MResult mres = MResult.buildOK();
		if(url.toLowerCase().startsWith("https")) {
			// SSL证书检测
			mres = UrlValidator.checkSSLCertificate(deviceNode.getUrl());
			if(!mres.isOk()) {
				// SSL证书已失效
				if(consolelog==1)
					logger.warn("doTesting, checkSSLCertificate FAIL! deviceNode=" + deviceNode + " url=" + url);
				
				// 记录并触发告警
				String warnmsg = "SSL证书已失效";
				warnservice.triggerWarn(deviceNode, warnmsg, 3, 24);	// 24小时可重复提醒3次
				return MResult.build_A00005(warnmsg);
			} else {
				// SSL证书有效
				Date notafter = mres.getParamT("notafter");

				long now = System.currentTimeMillis();
				long lefttime = notafter.getTime() - now;
				int leftDay = (int)(lefttime/(1000L*24*3600));	// 距离过期的天数
	            if(leftDay<=SSL_EXPIRE_LEFTDAY) {
	            	// SSL证书有效期已达告警阈值
		            if(consolelog==1)
		            	logger.warn("doTesting, SSL CERTIFICATE EXPIRES IN " + leftDay + " DAYS!! deviceNode=" + deviceNode + " url=" + url);
					
					// 记录并触发告警
					String warnmsg = "SSL证书将在" + leftDay + "天后过期，到期日期：" + CommonUtils.getDate(notafter, "yyyy-MM-dd");
					warnservice.triggerWarn(deviceNode, warnmsg, 1, 24);	// 24小时可重复提醒1次
					
					// SSL证书目前状态正常，但已临近到期时间
					mres.putParam("warnmsg", warnmsg);
					return mres;
	            }
			}
		}
		
		// 播测成功
		if(deviceNode.getWarnstatus()==1) {
			warnservice.restoreWarnStatus(deviceNode);
		}
		
		return mres;
    }
	
}
