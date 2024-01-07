package com.mpwork.monitor.console;

import org.mframework.admin.modulebase.EditModuleBase;
import org.mframework.annotation.MModuleMeta;
import org.mframework.common.BeanInfo;
import org.springframework.stereotype.Component;

import com.mpwork.monitor.bean.WarnNotifyRecord;

/**
 * 告警通知记录
 * @author dbb
 *
 */
@MModuleMeta(modulename = "告警通知记录", modulepath = "mpwork/monitor/warnnotifyrecord/detail")
@Component
public class WarnNotifyRecordEditModule extends EditModuleBase {

	private static BeanInfo beanInfo = BeanInfo.getBeanInfo(WarnNotifyRecord.class);

	public WarnNotifyRecordEditModule() {
		super(beanInfo, "");
		this.readonly = true;
	}
	
}
