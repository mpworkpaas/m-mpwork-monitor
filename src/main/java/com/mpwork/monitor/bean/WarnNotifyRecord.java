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
 * 告警通知记录
 * @author dbb
 *
 */
@SuppressWarnings("serial")
@BeanMeta(name="告警通知记录")
@Entity
@Table(name="warn_notify_record")
@Getter
@Setter
public class WarnNotifyRecord extends MBeanBase {

	@Id
	private String warnnotifyid;

	@MSearchMeta(searchtype=MSEARCHTYPE.INDEX)
	@FormMeta(optiontype=OPTIONTYPE.SELECTDB,
		optionsHQL="select deviceid, name from DeviceNode",
		optionTextHQL="select name from DeviceNode where deviceid='?'")
	@FieldMeta(name="告警设备", summary=true)
	private String deviceid;

	@FormMeta(hide=true)
	@FieldMeta(name="告警信息id")
	private String warnid;
	
	@MSearchMeta(searchtype=MSEARCHTYPE.COMPLEXLIKE)
	@FormMeta(maxlength=0, features = MFIELDFEATURE.OUT_CODE)
	@FieldMeta(name="通知内容", summary=true)
	private String notifydesc;

	@FormMeta(visible=MFormVisible.SHOW_FOCUS)
	@FieldMeta(name="通知时间", createtime=true, summary=true)
	private Date createtime;

}
