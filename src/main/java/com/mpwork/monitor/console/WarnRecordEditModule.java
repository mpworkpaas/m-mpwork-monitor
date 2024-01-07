package com.mpwork.monitor.console;

import org.mframework.admin.modulebase.EditModuleBase;
import org.mframework.annotation.MModuleMeta;
import org.mframework.common.BeanInfo;
import org.springframework.stereotype.Component;

import com.mpwork.monitor.bean.WarnRecord;

/**
 * 告警记录
 * @author dbb
 *
 */
@MModuleMeta(modulename = "告警记录", modulepath = "mpwork/monitor/warnrecord/detail")
@Component
public class WarnRecordEditModule extends EditModuleBase {

	private static BeanInfo beanInfo = BeanInfo.getBeanInfo(WarnRecord.class);

	public WarnRecordEditModule() {
		super(beanInfo, "");
		this.readonly = true;
	}
	
}
