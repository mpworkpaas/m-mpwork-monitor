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
import org.mframework.annotation.MSearchMeta;
import org.mframework.annotation.MSearchMeta.MSEARCHTYPE;
import org.mframework.common.MBeanBase;
import org.mframework.common.MFormVisible;

import lombok.Getter;
import lombok.Setter;

/**
 * 告警记录
 * @author dbb
 *
 */
@SuppressWarnings("serial")
@BeanMeta(name="告警记录")
@Entity
@Table(name="warn_record")
@Getter
@Setter
public class WarnRecord extends MBeanBase {

	@Id
	private String warnid;
	
	@FormMeta(hide=true)
	private String tenantid;

	@MSearchMeta(searchtype=MSEARCHTYPE.INDEX)
	@FormMeta(optiontype=OPTIONTYPE.SELECTDB,
		optionsHQL="select deviceid, name from DeviceNode",
		optionTextHQL="select name from DeviceNode where deviceid='?'")
	@FieldMeta(name="告警设备", summary=true)
	private String deviceid;
	
	@MSearchMeta(searchtype=MSEARCHTYPE.COMPLEXLIKE)
	@FormMeta(maxlength=0, features = MFIELDFEATURE.OUT_CODE)
	@FieldMeta(name="告警内容", summary=true)
	private String warncontent;

	@MSearchMeta(searchtype=MSEARCHTYPE.DATETIMERANGE, searchdefvalue="DAY")
	@FormMeta(visible=MFormVisible.SHOW_FOCUS)
	@FieldMeta(name="告警时间", createtime=true, summary=true)
	private Date createtime;

	@FormMeta(optiontype=OPTIONTYPE.RADIO, options={ "0:未通知", "1:已通知:info" })
	@FieldMeta(name="通知状态", summary=true)
	private int hasnotify;

}
