package com.mpwork.monitor.console.rule;

import org.mframework.admin.modulebase.ListModuleBase;
import org.mframework.annotation.MModuleMeta;
import org.mframework.common.BeanInfo;
import org.mframework.utils.MPageUtils;
import org.springframework.stereotype.Component;

import com.mpwork.monitor.bean.WarnRule;

/**
 * 告警规则
 * @author dbb
 *
 */
@MModuleMeta(modulename="告警规则", modulepath="mpwork/monitor/warnrule/list")
@Component
public class WarnRuleListModule extends ListModuleBase {

	private static BeanInfo beanInfo = BeanInfo.getBeanInfo(WarnRule.class);

	public WarnRuleListModule() {
		super(beanInfo, MPageUtils.buildFrameURL("mpwork/monitor/warnrule/edit"));
		this.editTarget = "_pop";
	}

}
