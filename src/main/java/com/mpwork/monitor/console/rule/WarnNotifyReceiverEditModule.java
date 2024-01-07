package com.mpwork.monitor.console.rule;

import org.mframework.admin.modulebase.EditModuleBase;
import org.mframework.annotation.MModuleMeta;
import org.springframework.stereotype.Component;

import com.mpwork.monitor.bean.WarnNotifyReceiver;

@MModuleMeta(modulename="通知渠道管理", modulepath="mpwork/monitor/receiver/edit")
@Component
public class WarnNotifyReceiverEditModule extends EditModuleBase {

	public WarnNotifyReceiverEditModule() {
		super(WarnNotifyReceiver.class, "");
	}
	
}
