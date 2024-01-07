package com.mpwork.monitor.common;

import javax.persistence.Id;

import org.mframework.annotation.BeanMeta;
import org.mframework.annotation.FieldMeta;
import org.mframework.annotation.FormMeta;
import org.mframework.annotation.FormMeta.MFIELDFEATURE;
import org.mframework.annotation.FormMeta.OPTIONTYPE;
import org.mframework.common.MBeanBase;

import lombok.Getter;
import lombok.Setter;

/**
 * 播测Vo
 * @author dbb
 *
 */
@SuppressWarnings("serial")
@BeanMeta(name="播测")
@Getter
@Setter
public class DeviceTestingVo extends MBeanBase {

	@Id
	private String id;

	@FieldMeta(name="设备节点", editable=false)
	private String deviceid;

	@FieldMeta(name="监控目标", editable=false)
	private String serverinfo;
	
	@FormMeta(optiontype=OPTIONTYPE.RADIO, options={"0:播测失败:danger", "1:播测成功:success"})
	@FieldMeta(name="结果", editable=false)
	private int result;

	@FieldMeta(name="执行用时", editable=false)
	private String usetime;
	
	@FormMeta(required=false, maxlength=0, features = MFIELDFEATURE.OUT_CODE)
	@FieldMeta(name="返回信息", editable=false)
	private String outdata;

}
