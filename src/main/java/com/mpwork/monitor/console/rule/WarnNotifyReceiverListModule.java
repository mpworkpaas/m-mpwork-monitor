package com.mpwork.monitor.console.rule;

import org.mframework.admin.modulebase.ListModuleBase;
import org.mframework.annotation.MModuleMeta;
import org.mframework.utils.MPageUtils;
import org.springframework.stereotype.Component;

import com.mpwork.monitor.bean.WarnNotifyReceiver;

/**
 * 通知渠道管理
 * @author dbb
 *
 */
@MModuleMeta(modulename="通知渠道管理", modulepath="mpwork/monitor/receiver/list")
@Component
public class WarnNotifyReceiverListModule extends ListModuleBase {

	public WarnNotifyReceiverListModule() {
		super(WarnNotifyReceiver.class, MPageUtils.buildFrameURL("mpwork/monitor/receiver/edit"));
		this.editTarget = "_pop";
		
		// this.navtabList = new NotifyNavTab().buildNavTab("receiver");
//		additionalParameters = new ArrayList<String>();
//		additionalParameters.add("tenantid");
	}
	
}