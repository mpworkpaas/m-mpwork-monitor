package com.mpwork.monitor.console.device;

import javax.servlet.http.HttpServletRequest;

import org.mframework.admin.common.TipMessage;
import org.mframework.admin.modulebase.EditModuleBase;
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
@MModuleMeta(modulename = "设备节点", modulepath = "mpwork/monitor/devicenode/edit")
@Component
public class DeviceNodeEditModule extends EditModuleBase {

	private static BeanInfo beanInfo = BeanInfo.getBeanInfo(DeviceNode.class);
	@Autowired
	private JobScheduleService jobScheduleService;

	public DeviceNodeEditModule() {
		super(beanInfo, MPageUtils.buildFrameURL("mpwork/monitor/devicenode/list"));
	}
	
	@Override
	protected boolean onsaved(HttpServletRequest request, BeanInfo beanInfo, MBeanBase bean) {
		DeviceNode deviceNode = (DeviceNode)bean;
		
		if(deviceNode.getStatus()==1) {
			// 先移除
			jobScheduleService.removeJob(deviceNode);
			
			// 再重新部署
			MResult mres = jobScheduleService.deployJob(deviceNode);
			if(!mres.isOk()) {
				deviceNode.setStatus(0);
				mframeworkservice.update(bean);
				
				TipMessage.postErrorTip(request, "任务部署时发生错误！");
				return false;
			}
		}
		
		return true;
	}
	
}
