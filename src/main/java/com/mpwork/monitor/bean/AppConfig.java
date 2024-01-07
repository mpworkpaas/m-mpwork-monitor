package com.mpwork.monitor.bean;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.mframework.annotation.BeanMeta;
import org.mframework.annotation.FieldMeta;
import org.mframework.annotation.FormMeta;
import org.mframework.annotation.FormMeta.OPTIONTYPE;

import com.mpwork.apputils.common.ConfigBeanBase;

import lombok.Getter;
import lombok.Setter;

/**
 * 应用模块配置
 * @author zjs
 *
 */
@SuppressWarnings("serial")
@BeanMeta(name="配置")
@Entity
@Table(name="appconfig")
@Setter
@Getter
public class AppConfig extends ConfigBeanBase {
	
	@FormMeta(defvalue="www.baidu.com", formtip="系统将在播测业务前进行服务端网络可用性自检。此处填写自检ping的目标主机")
	@FieldMeta(name="播测服务自检目标主机")
	private String testing_selftest_pinghost;
	
	@FormMeta(defvalue="3", formtip="PING监控：指超过指定失败次数后触发")
	@FieldMeta(name="ping默认告警阈值", summary=true)
	private int default_ping_threshold;
	
	@FormMeta(defvalue="80", formtip="即使用空间超过指定百分比后触发，填写值如80")
	@FieldMeta(name="磁盘空间默认告警阈值", summary=true)
	private int default_diskusage_threshold;

	@FormMeta(defvalue="1", optiontype=OPTIONTYPE.RADIO, options={ "0:关闭日志:default", "1:日志开启:success" })
	@FieldMeta(name="播测控制台日志")
	private int testing_consolelog;
	
	@FormMeta(defvalue="30", formtip="如填入30，即表示距离证书到期不足30天时，开始触发提醒")
	@FieldMeta(name="SSL到期前提醒天数")
	private int ssl_expire_leftday;
	
}
