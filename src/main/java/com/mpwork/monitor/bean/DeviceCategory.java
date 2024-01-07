package com.mpwork.monitor.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mframework.annotation.BeanMeta;
import org.mframework.annotation.FieldMeta;
import org.mframework.annotation.FormMeta;
import org.mframework.annotation.FormMeta.OPTIONTYPE;
import org.mframework.annotation.MSearchMeta;
import org.mframework.annotation.MSearchMeta.MSEARCHTYPE;
import org.mframework.common.MBeanBase;

import lombok.Getter;
import lombok.Setter;

/**
 * 设备分类
 * @author dbb
 *
 */
@SuppressWarnings("serial")
@BeanMeta(name="设备分类", fakedelete=true)
@Entity
@Table(name="device_category")
@Getter
@Setter
public class DeviceCategory extends MBeanBase {

	@Id
	private String categoryid;
	
	@FormMeta(hide=true)
	private String tenantid;
	
	@MSearchMeta(searchtype=MSEARCHTYPE.COMPLEXLIKE)
	@FieldMeta(name="分类名称", db_index=true, summary=true)
	private String name;

	@FieldMeta(name="创建时间", createtime=true, editable=false)
	private Date createtime;

	@FormMeta(defvalue="1", optiontype=OPTIONTYPE.RADIO, options={ "0:禁用:default", "1:可用:success" })
	@FieldMeta(name="状态", summary=true)
	private int status;

	@Override
	public String toSummary(){
		return name;
	}

}
