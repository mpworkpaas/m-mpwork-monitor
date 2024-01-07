package com.mpwork.monitor.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mframework.boot.configure.MCoreConfig;
import org.mframework.cache.MCacheUtils;
import org.mframework.service.MFrameworkService;
import org.mframework.utils.CommonUtils;
import org.mframework.utils.MClusterUtils;
import org.mframework.utils.MResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.mpwork.apputils.service.CommonAppConfigServiceV2;
import com.mpwork.apputils.service.MpworkNotifyClient;
import com.mpwork.monitor.bean.AppConfig;
import com.mpwork.monitor.bean.DeviceNode;
import com.mpwork.monitor.bean.WarnRecord;
import com.mpwork.monitor.utils.TestingUtils.PingResult;

/**
 * 告警与通知服务
 * @author dbb
 *
 */
@Service
public class WarnService {

	private static final Log logger = LogFactory.getLog(WarnService.class);
	@Autowired
	private MFrameworkService mframeworkservice;
	@Autowired
	private CommonAppConfigServiceV2 commonAppconfigService;
	@Autowired
	private MpworkNotifyClient mpworkNotifyClient;


	// 提醒频率控制
	private MResult checkRateControl(DeviceNode deviceNode, String warnmsgHash, int repeatcount, int inhour) {
		try {
			String key = "MPWORK-MONITOR-WARNREPEAT:" + warnmsgHash + ":" + inhour;
			long value = MCacheUtils.incr(key, 1, 1, 5000, 3600*inhour);
			
			String msghashkey_key = KEY_DEVICENODE_WARNMSGHASHKEY + deviceNode.getDeviceid();
			MCacheUtils.set(msghashkey_key, 3600*24, key);
			
			if(value>repeatcount) {
				// 已超过提醒频率，不再触发提醒
				// logger.info("triggerWarnByCURL, over frequency limit. IGNORE. deviceNode=" + deviceNode + " warnmsg=" + warnmsg);
				return MResult.build_A00005("INLIMIT");
			}
		} catch(Exception e) {
			e.printStackTrace();
			return MResult.build_A00005("INLIMIT");
		}
		
		return MResult.buildOK();
	}
	
	@Async
	public void triggerWarnByPing(DeviceNode deviceNode, PingResult pingResult, int repeatcount, int inhour) {
		String warncontent = "播测异常(PING)："
			+ "<br/>- 节点名: " + deviceNode.getName()
			+ "<br/>- 监控目标: " + deviceNode.toSummary()
			+ "<br/>- PingTime:" + pingResult.getRoundTripTime()
			+ "<br/><br/>" + pingResult.getOutput();
		
		boolean reachNotifyCount = checkNotifyTryCount(deviceNode);
		if(!reachNotifyCount) {
			// 尚未达到需要触发通知的连续异常阈值
			return;
		}
		
		// 提醒频率控制
		String warnmsgHash = CommonUtils.MD5(deviceNode.getDeviceid() + "PING");
		MResult limitResult = checkRateControl(deviceNode, warnmsgHash, repeatcount, inhour);
		if(!limitResult.isOk())
			return;	// 已超过提醒频率，不再触发提醒
		
		// 生成告警记录
		WarnRecord warnRecord = WarnRecord.newInstance(WarnRecord.class);
		warnRecord.setTenantid(deviceNode.getTenantid());
		warnRecord.setDeviceid(deviceNode.getDeviceid());
		warnRecord.setWarncontent(warncontent);
		warnRecord.setHasnotify(0);
		
		// 设备节点允许告警推送
		if(deviceNode.getWarn_pushnotify()!=0) {
			// 检测通知规则; 推送通知
			checkRuleAndPushNotify(deviceNode, warnRecord);

			warnRecord.setHasnotify(1);
		}
		mframeworkservice.update(warnRecord);
		
		// 标记【已告警】
		deviceNode.setWarnstatus(1);	// 1已告警
		mframeworkservice.update(deviceNode);
	}

    
	// 通用
	// repeatcount与inhour为每指定的时间内，可重复提醒的次数
	// - 如SSL证书到期提醒，可设为repeatcount=1，inmin=24(即24小时)  1天内只提示1次
	// - 如PING异常告警提醒，可设为repeatcount=3，inmin=1（即1小时）  1小时内可重复3次提醒
	@Async
	public void triggerWarn(DeviceNode deviceNode, String warnmsg, int repeatcount, int inhour) {
		String warncontent = "播测异常(" + deviceNode.getMonitortype() + ")："
			+ "<br/> 节点名: " + deviceNode.getName()
			+ "<br/> 监控目标: " + deviceNode.toSummary()
			+ "<br/> 告警信息: " + warnmsg;
		
		boolean reachNotifyCount = checkNotifyTryCount(deviceNode);
		if(!reachNotifyCount) {
			// 尚未达到需要触发通知的连续异常阈值
			return;
		}
		
		// 提醒频率控制
		String warnmsgHash = CommonUtils.MD5(deviceNode.getDeviceid() + warncontent);
		MResult limitResult = checkRateControl(deviceNode, warnmsgHash, repeatcount, inhour);
		if(!limitResult.isOk())
			return;	// 已超过提醒频率，不再触发提醒
		
		// 生成告警记录
		WarnRecord warnRecord = WarnRecord.newInstance(WarnRecord.class);
		warnRecord.setTenantid(deviceNode.getTenantid());
		warnRecord.setDeviceid(deviceNode.getDeviceid());
		warnRecord.setWarncontent(warncontent);
		warnRecord.setHasnotify(0);
		
		if(deviceNode.getWarn_pushnotify()!=0) {
			// 检测通知规则; 推送通知
			checkRuleAndPushNotify(deviceNode, warnRecord);
			
			warnRecord.setHasnotify(1);
		}
		mframeworkservice.update(warnRecord);
		
		// 标记【已告警】
		deviceNode.setWarnstatus(1);	// 1已告警
		mframeworkservice.update(deviceNode);
	}
	
	// 校验推送规则；执行平台推送
	private void checkRuleAndPushNotify(DeviceNode deviceNode, WarnRecord warnRecord) {
		AppConfig appconfig = commonAppconfigService.getUniConfig(AppConfig.class);
		
		int threshold_ping = 3;
		int threshold_diskusage = 80;
		if(appconfig!=null) {
			threshold_diskusage = appconfig.getDefault_diskusage_threshold();
			threshold_ping = appconfig.getDefault_ping_threshold();
		}
		
		// DBB@20231006
		// TODO 待规则考虑全面，再实现
		/*
		// 校验是否达到阈值
		if("PING".equals(deviceNode.getMonitortype())) {
			// 告警频率
		} else if("CURL".equals(deviceNode.getMonitortype())) {
			// ??
		} else {
			// ??
		}
		*/
		
		String eventKey = "MPWORK-MONITOR-WARN";
		String msgcontent = warnRecord.getWarncontent();
		String bundleid = MCoreConfig.getMModuleBundleid();
		String notifyMessage = "<table><tr><td>Event</td><td>" + eventKey + "</td></tr>"
				+ "<tr><td width='80'>NodeAddr</td><td>" + MClusterUtils.getNodeAddr() + "</td></tr>"
				+ "<tr><td>Time</td><td>" + CommonUtils.getDate() + "</td></tr>"
				+ (StringUtils.isEmpty(bundleid) ? "" : "<tr><td>Bundleid</td><td>" + bundleid + "</td></tr>")
				+ "</table><br/>" + CommonUtils.replace(msgcontent, "\n", "<br/>");
		
		// 触发Mpwork平台事件通知
		mpworkNotifyClient.sendNotifyMessage(warnRecord.getTenantid(), eventKey, notifyMessage);
	}

	
    // 标记和记录告警状态恢复为正常
    public void restoreWarnStatus(DeviceNode deviceNode) {
    	deviceNode.setWarnstatus(0);	// 解除告警状态
		mframeworkservice.update(deviceNode);
		logger.info("restoreWarnStatus, WARNSTATUS RESTORE GREEN! deviceNode=" + deviceNode);

		// 记录告警恢复
		WarnRecord warnRecord = WarnRecord.newInstance(WarnRecord.class);
		warnRecord.setTenantid(deviceNode.getTenantid());
		warnRecord.setDeviceid(deviceNode.getDeviceid());
		warnRecord.setWarncontent("播测成功，告警解除");
		warnRecord.setHasnotify(0);
		mframeworkservice.update(warnRecord);
		
		// 清理连续告警次数统计
		String key = KEY_CONTINU_WARN_NOTIFY + deviceNode.getDeviceid();
		MCacheUtils.delete(key);
		// 清理提醒频率控制
		String warnmsgRateControlKey = MCacheUtils.getString(KEY_DEVICENODE_WARNMSGHASHKEY + deviceNode.getDeviceid());
		if(!StringUtils.isEmpty(warnmsgRateControlKey))
			MCacheUtils.delete(warnmsgRateControlKey);
    }


	private static String KEY_DEVICENODE_WARNMSGHASHKEY = "DEVICENODE_WARNMSGHASH_KEY:";	// 记录最后一次告警消息通知频率控制key（用于恢复时清理）
	private static String KEY_CONTINU_WARN_NOTIFY = "CONTINU_WARN_NOTIFY:";					// 记录设备连续异常次数（用于检测是否达到通知阈值）
	
	// 校验是否已达到触发通知的连续播测异常数
	private boolean checkNotifyTryCount(DeviceNode deviceNode) {
		try {
			String key = KEY_CONTINU_WARN_NOTIFY + deviceNode.getDeviceid();
			long value = MCacheUtils.incr(key, 1, 1, 5000, 12*3600);
			
			return value>=deviceNode.getWarn_notify_trycount();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
