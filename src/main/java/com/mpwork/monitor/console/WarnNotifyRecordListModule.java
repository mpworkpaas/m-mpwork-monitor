package com.mpwork.monitor.console;

import org.mframework.admin.modulebase.ListModuleBase;
import org.mframework.annotation.MModuleMeta;
import org.mframework.common.BeanInfo;
import org.mframework.utils.MPageUtils;
import org.springframework.stereotype.Component;

import com.mpwork.monitor.bean.WarnNotifyRecord;

/**
 * 告警通知记录
 * @author dbb
 *
 */
@MModuleMeta(modulename="告警通知记录", modulepath="mpwork/monitor/warnnotifyrecord/list")
@Component
public class WarnNotifyRecordListModule extends ListModuleBase {

	private static BeanInfo beanInfo = BeanInfo.getBeanInfo(WarnNotifyRecord.class);

	public WarnNotifyRecordListModule() {
		super(beanInfo, MPageUtils.buildFrameURL("mpwork/monitor/warnnotifyrecord/detail"));
		this.editTarget = "_pop";
		this.text_modifybtn = "详情";
		this.can_delete = false;
		this.can_create = false;
	}

}
