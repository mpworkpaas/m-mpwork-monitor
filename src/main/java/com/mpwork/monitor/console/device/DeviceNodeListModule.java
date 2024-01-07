package com.mpwork.monitor.console.device;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.mframework.admin.common.ListCustomButton;
import org.mframework.admin.common.TipMessage;
import org.mframework.admin.modulebase.ListModuleBase;
import org.mframework.annotation.MModuleMeta;
import org.mframework.common.BeanInfo;
import org.mframework.common.MBeanBase;
import org.mframework.utils.MPageUtils;
import org.mframework.utils.MResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mpwork.monitor.bean.DeviceNode;
import com.mpwork.monitor.service.JobScheduleService;

/**
 * 设备节点
 * @author dbb
 *
 */
@MModuleMeta(modulename="设备节点", modulepath="mpwork/monitor/devicenode/list")
@Component
public class DeviceNodeListModule extends ListModuleBase {

	private static BeanInfo beanInfo = BeanInfo.getBeanInfo(DeviceNode.class);
	@Autowired
	private JobScheduleService jobScheduleService;

	public DeviceNodeListModule() {
		super(beanInfo, MPageUtils.buildFrameURL("mpwork/monitor/devicenode/edit"));
		this.can_modify = false;
		this.show_refreshbtn = true;
		this.maxListBtnCount = 2;
		
		ListCustomButton.build("deviceid", "播测", MPageUtils.buildFrameURL("mpwork/monitor/devicenode/testing"))
			.setTarget("_pop")
			.addButtonTo(this);
		
		ListCustomButton.build("deviceid", "告警记录", MPageUtils.buildFrameURL("mpwork/monitor/warnrecord/list"))
			.setTarget("_pop")
			.addButtonTo(this);
		
		ListCustomButton.build("id", "设置", MPageUtils.buildFrameURL("mpwork/monitor/devicenode/edit"))
			.setTarget("_pop")
			.addButtonTo(this);

		this.transparentParameters = new ArrayList<>();
		this.transparentParameters.add("categoryid");
		this.additionalParameters = new ArrayList<>();
		this.additionalParameters.add("categoryid");
	}

	@Override
	protected MResult onswitchupdated(HttpServletRequest request, MBeanBase bean, String field) {
		DeviceNode deviceNode = (DeviceNode)bean;
		
		if("status".equals(field)) {
			if(deviceNode.getStatus()==1) {
				MResult mres = jobScheduleService.deployJob(deviceNode);
				if(!mres.isOk()) {
					deviceNode.setStatus(0);
					mframeworkservice.update(bean);
					
					TipMessage.postErrorTip(request, "任务部署时发生错误！");
					return mres;
				}
			} else {
				jobScheduleService.removeJob(deviceNode);
			}
		}
		
		return MResult.buildOK();
	}
}
