package com.mpwork.monitor.console.rule;

import org.mframework.admin.modulebase.EditModuleBase;
import org.mframework.annotation.MModuleMeta;
import org.mframework.common.BeanInfo;
import org.springframework.stereotype.Component;

import com.mpwork.monitor.bean.WarnRule;

/**
 * 告警规则
 * @author dbb
 *
 */
@MModuleMeta(modulename = "告警规则", modulepath = "mpwork/monitor/warnrule/edit")
@Component
public class WarnRuleEditModule extends EditModuleBase {

	private static BeanInfo beanInfo = BeanInfo.getBeanInfo(WarnRule.class);

	public WarnRuleEditModule() {
		super(beanInfo, "");
	}
	
}
