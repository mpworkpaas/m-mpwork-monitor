package com.mpwork.monitor.console.device;

import org.mframework.admin.modulebase.ListModuleBase;
import org.mframework.annotation.MModuleMeta;
import org.mframework.common.BeanInfo;
import org.mframework.utils.MPageUtils;
import org.springframework.stereotype.Component;

import com.mpwork.monitor.bean.DeviceCategory;

/**
 * 设备分类
 * @author dbb
 *
 */
@MModuleMeta(modulename="设备分类", modulepath="mpwork/monitor/category/list")
@Component
public class DeviceCategoryListModule extends ListModuleBase {

	private static BeanInfo beanInfo = BeanInfo.getBeanInfo(DeviceCategory.class);

	public DeviceCategoryListModule() {
		super(beanInfo, MPageUtils.buildFrameURL("mpwork/monitor/category/edit"));
		this.editTarget = "_pop";
		
//		ListCustomButton.build("categoryid", "徘徊人员规则设置", MPageUtils.buildFrameURL("mpwork/monitor/devicenode/list"))
//			.addButtonTo(this);
	}

}
