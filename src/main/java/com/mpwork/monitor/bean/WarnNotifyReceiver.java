package com.mpwork.monitor.bean;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mframework.annotation.BeanMeta;
import org.mframework.annotation.FieldMeta;
import org.mframework.annotation.FormMeta;
import org.mframework.annotation.FormMeta.OPTIONTYPE;
import org.mframework.common.MBeanBase;

import lombok.Getter;
import lombok.Setter;

/**
 * 通知渠道
 * @author dbb
 *
 */
@SuppressWarnings("serial")
@BeanMeta(name="通知渠道", fakedelete=true)
@Entity
@Table(name="notify_receiver")
@Setter
@Getter
public class WarnNotifyReceiver extends MBeanBase {
	
	@Id
	private String notify_receiver_id;

	@FormMeta(formtip="如接收人姓名、系统名等")
	@FieldMeta(name="通知目标名称", summary=true)
	private String receive_name;

	@FormMeta(optiontype=OPTIONTYPE.CHECKBOX, options={"EMAIL:邮件:primary", "SMS:短信:warning", "WECHAT:微信:success", "API:API:info"})
	@FieldMeta(name="渠道", summary=true)
	private String channels;

	@FormMeta(required=false)
	@FieldMeta(name="通知接收邮件")
	private String receive_email;

	@FormMeta(required=false)
	@FieldMeta(name="通知接收电话")
	private String receive_telnum;

	@FormMeta(required=false)
	@FieldMeta(name="通知接收微信")
	private String receive_wxopenid;

	@FormMeta(required=false, maxlength=500, formtip="post/json")
	@FieldMeta(name="API地址")
	private String receive_apiurl;

	@FormMeta(defvalue="1", optiontype=OPTIONTYPE.RADIO, options={"0:禁用:default", "1:开启:success"})
	@FieldMeta(name="消息状态", summary=true)
	private String status;

	@Override
	public String toString() {
		return receive_name;
	}
}
