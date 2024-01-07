package com.mpwork.monitor.console.device;

import org.mframework.admin.modulebase.EditModuleBase;
import org.mframework.annotation.MModuleMeta;
import org.mframework.common.BeanInfo;
import org.springframework.stereotype.Component;

import com.mpwork.monitor.bean.DeviceCategory;

/**
 * 设备分类
 * @author dbb
 *
 */
@MModuleMeta(modulename = "设备分类", modulepath = "mpwork/monitor/category/edit")
@Component
public class DeviceCategoryEditModule extends EditModuleBase {

	private static BeanInfo beanInfo = BeanInfo.getBeanInfo(DeviceCategory.class);

	public DeviceCategoryEditModule() {
		super(beanInfo, "");
	}
	
}
