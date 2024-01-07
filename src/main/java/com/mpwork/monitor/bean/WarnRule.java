package com.mpwork.monitor.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mframework.annotation.BeanMeta;
import org.mframework.annotation.FieldMeta;
import org.mframework.annotation.FormMeta;
import org.mframework.annotation.FormMeta.MFIELDFEATURE;
import org.mframework.annotation.FormMeta.OPTIONTYPE;
import org.mframework.common.MBeanBase;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 告警规则
 * @author dbb
 *
 */
@SuppressWarnings("serial")
@BeanMeta(name="告警规则", fakedelete=true)
@Entity
@Table(name="warn_rule")
@Getter
@Setter
@ToString
public class WarnRule extends MBeanBase {

	@Id
	private String ruleid;

	@FormMeta(required=false, optiontype=OPTIONTYPE.SELECTDB,
		optionsHQL="select deviceid, concat('[', monitortype, '] ', name) from DeviceNode",
		optionTextHQL="select concat('[', monitortype, '] ', name) from DeviceNode where deviceid='?'")
	@FieldMeta(name="指定设备", summary=true)
	private String deviceid;
	
	@FormMeta(formtip="PING监控：指超过指定失败次数后触发；<br/>CURL/TCP时，此值无意义；<br/>磁盘用量监控时，取值为0-100百分比")
	@FieldMeta(name="阈值", summary=true)
	private int threshold;

	@FormMeta(defvalue="WARN", optiontype=OPTIONTYPE.RADIO, options={ "INFO:信息:info", "WARN:警告:warning", "ERROR:错误:danger" })
	@FieldMeta(name="告警级别", summary=true)
	private String warnlevel;
	
	@FieldMeta(name="创建时间", createtime=true, editable=false)
	private Date createtime;

	@FormMeta(defvalue="1", optiontype=OPTIONTYPE.RADIO, options={ "0:停用:default", "1:启用:primary" }, features = MFIELDFEATURE.OUT_LIST_SWITCH)
	@FieldMeta(name="状态", summary=true)
	private int status;

}