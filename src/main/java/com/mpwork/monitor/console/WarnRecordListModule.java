package com.mpwork.monitor.console;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.mframework.admin.LoginInfo;
import org.mframework.admin.modulebase.ListModuleBase;
import org.mframework.annotation.MModuleMeta;
import org.mframework.common.BeanInfo;
import org.mframework.utils.MPageUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.mpwork.monitor.bean.WarnRecord;

/**
 * 告警记录
 * @author dbb
 *
 */
@MModuleMeta(modulename="告警记录", modulepath="mpwork/monitor/warnrecord/list")
@Component
public class WarnRecordListModule extends ListModuleBase {

	private static BeanInfo beanInfo = BeanInfo.getBeanInfo(WarnRecord.class);

	public WarnRecordListModule() {
		super(beanInfo, MPageUtils.buildFrameURL("mpwork/monitor/warnrecord/detail"));
		this.editTarget = "_pop";
		this.text_modifybtn = "详情";
		this.can_delete = false;
		this.can_create = false;
		
		this.additionalParameters = new ArrayList<>();
		this.additionalParameters.add("deviceid");
	}
	
	@Override
	protected String preExecute(HttpServletRequest request, LoginInfo loginInfo, ModelMap modelMap) {
		String noframe = MPageUtils.getParameter(request, "noframe");
		if(!StringUtils.isEmpty(noframe)) {
			BeanInfo beanInfo = BeanInfo.getFromRequest(request);
			beanInfo.msearchList = null;
		}
		
		return null;
	}

}
