package com.mpwork.monitor.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mframework.annotation.BeanMeta;
import org.mframework.annotation.FieldMeta;
import org.mframework.annotation.FormMeta;
import org.mframework.annotation.FormMeta.MFIELDFEATURE;
import org.mframework.annotation.FormMeta.OPTIONTYPE;
import org.mframework.annotation.MSearchMeta;
import org.mframework.annotation.MSearchMeta.MSEARCHTYPE;
import org.mframework.common.MBeanBase;
import org.mframework.common.SortableField;
import org.mframework.common.mfield.FormGroupControl;
import org.mframework.utils.MStringUtils;
import org.quartz.JobKey;

import com.mpwork.monitor.common.jobexec.JobBean;

import lombok.Getter;
import lombok.Setter;

/**
 * 节点设备
 * @author dbb
 *
 */
@SuppressWarnings("serial")
@BeanMeta(name="节点设备")
@Entity
@Table(name="device_node")
@Getter
@Setter
public class DeviceNode extends MBeanBase implements JobBean {

	@Id
	private String deviceid;
	
	@FormMeta(hide=true)
	private String tenantid;

	@FormMeta(hide=true, required=false)
	@FieldMeta(name="设备类型")
	private String devicetype;	// CAM/...
	
	@MSearchMeta(searchtype=MSEARCHTYPE.INDEX)
	@FormMeta(optiontype=OPTIONTYPE.SELECTDB,
		optionsHQL="select categoryid, name from DeviceCategory where status<>9",
		optionTextHQL="select name from DeviceCategory where categoryid='?'")
	@FieldMeta(name="设备分类", summary=true)
	private String categoryid;

	@MSearchMeta(searchtype=MSEARCHTYPE.COMPLEXLIKE)
	@FieldMeta(name="设备编号/名称", summary=true)
	private String name;

	@MSearchMeta(searchtype=MSEARCHTYPE.COMPLEXLIKE)
	@FormMeta(required=false, formtip="如: H08入口等")
	@FieldMeta(name="设备位置")
	private String location;

	@FormMeta(defvalue="PING", maxlength=50, mcontrol=FormGroupControl.class, options={
			"PING:PING:info", "CURL:WEB服务:primary", "TCP:TCP服务", "ISKUSAGE:磁盘用量:warning" })
	@FieldMeta(name="监控方式", summary=true)
	private String monitortype;

	@FormMeta(groupby="monitortype", groupid="PING")
	@FieldMeta(name="网络IP")
	private String ip;

	@FormMeta(groupby="monitortype", groupid="CURL")
	@FieldMeta(name="WEB地址")
	private String url;

	@FormMeta(groupby="monitortype", groupid="TCP", formtip="值如：10.11.8.116:6379")
	@FieldMeta(name="TCP地址")
	private String tcphost;
	
	@Transient
	@FormMeta(required=false)
	@FieldMeta(name="监控目标", summary=true)
	private String _serverinfo;

	@FormMeta(required=false, formtip="格式：xx-xx-xx-xx-xx-xx")
	@FieldMeta(name="管理网络MAC地址")
	private String mac;		// 绑定mac（远程开关机）
	
	@FormMeta(maxlength=0, required=false)
	@FieldMeta(name="配置参数")
	private String params;
	
	@FormMeta(required=false, defvalue="0 * * * * ?", pagegroup="cron",
			formtip="cron表达式(秒 分 时 日 月 星期 年)，配置示例：0 36 14 * * ?（表示每日14:36执行）<br/>0 * * * * ?（每分钟执行）<br/>0/10 * * * * ?（每10秒执行）")
	@FieldMeta(name="播测周期")
	private String cron;

	@FormMeta(defvalue="1", optiontype=OPTIONTYPE.RADIO, options={ "0:禁止推送:default", "1:允许推送:primary" })
	@FieldMeta(name="推送通知", summary=true)
	private int warn_pushnotify;

	@FormMeta(defvalue="1", formtip="填0或1表示每次异常都触发告警；填3表示连续3次异常才推送告警")
	@FieldMeta(name="连续异常推送通知", summary=true)
	private int warn_notify_trycount;

	@FormMeta(defvalue="0", formtip="填0表示按系统默认3次，即1小时最大重复通知3次。")
	@FieldMeta(name="小时最大通知次数", subname="推送熔断参数")
	private int warn_notify_countinhour;

	@FieldMeta(name="创建时间", createtime=true, editable=false)
	private Date createtime;

	@FormMeta(defvalue="1", optiontype=OPTIONTYPE.RADIO, options={ "0:监控关闭:default", "1:监控开启:success" }, features = MFIELDFEATURE.OUT_LIST_SWITCH)
	@FieldMeta(name="监控状态", summary=true)
	private int status;

	@FormMeta(hide=true, defvalue="1", optiontype=OPTIONTYPE.RADIO, options={ "0:未告警:success", "1:已告警:danger" })
	@FieldMeta(name="告警状态", summary=true, editable=false)
	private int warnstatus;
	
	@Override
	public String toSummary() {
		String summary = "[" + monitortype + "]";
		if("PING".equalsIgnoreCase(monitortype)) {
			summary += ip;
		} else if("CURL".equalsIgnoreCase(monitortype)) {
			summary += MStringUtils.ellipsis(url, 100);
		} else if("TCP".equalsIgnoreCase(monitortype)) {
			summary += tcphost;
		}
		return summary;
	}

	// 自定义值输出格式处理器
	public static String outFormat(SortableField mfield, DeviceNode deviceNode, Object value, String standardOut) {
		if("_serverinfo".equals(mfield.getName())) {
			if("PING".equalsIgnoreCase(deviceNode.monitortype)) {
				return deviceNode.ip;
			} else if("CURL".equalsIgnoreCase(deviceNode.monitortype)) {
				return MStringUtils.ellipsis(deviceNode.url, 100);
			} else if("TCP".equalsIgnoreCase(deviceNode.monitortype)) {
				return deviceNode.tcphost;
			}
		}
		
		return null;
	}

	
	@Override
	public String toString() {
		return name + "(" + toSummary() + ")";
	}
	
	@Override
	public JobKey getJobKey() {
		return new JobKey("job-" + deviceid, "group1");
	}

	@Override
	public String getJobPkid() {
		return deviceid;
	}
	
}