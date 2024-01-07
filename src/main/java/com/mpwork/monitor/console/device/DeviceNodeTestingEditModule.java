package com.mpwork.monitor.console.device;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.mframework.admin.LoginInfo;
import org.mframework.admin.modulebase.EditModuleBase;
import org.mframework.annotation.MModuleMeta;
import org.mframework.common.BeanInfo;
import org.mframework.common.MBeanBase;
import org.mframework.utils.MPageUtils;
import org.mframework.utils.MResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mpwork.apputils.service.CommonAppConfigServiceV2;
import com.mpwork.monitor.bean.DeviceNode;
import com.mpwork.monitor.common.DeviceTestingVo;
import com.mpwork.monitor.service.TestingService;

/**
 * 播测
 * @author dbb
 *
 */
@MModuleMeta(modulename = "播测", modulepath = "mpwork/monitor/devicenode/testing")
@Component
public class DeviceNodeTestingEditModule extends EditModuleBase {

	private static BeanInfo beanInfo = BeanInfo.getBeanInfo(DeviceTestingVo.class);
	@Autowired
	private TestingService testingService;
	@Autowired
	private CommonAppConfigServiceV2 commonAppconfigService;

	public DeviceNodeTestingEditModule() {
		super(beanInfo, "");
		this.notfoundbean_create = false;
		this.readonly = true;
	}
	
	@Override
	protected MBeanBase loadBean(HttpServletRequest request, String beanid, LoginInfo loginInfo, BeanInfo beanInfo) {
		String deviceid = MPageUtils.getParameter(request, "deviceid");
		if(StringUtils.isEmpty(deviceid)) {
			return null;
		}
		
		DeviceNode deviceNode = mframeworkservice.get(DeviceNode.class, deviceid);
		if(deviceNode==null) {
			return null;
		}
		
		long time1 = System.currentTimeMillis();
		DeviceTestingVo testingVo = new DeviceTestingVo();
		testingVo.setDeviceid(deviceNode.getDeviceid());
		testingVo.setServerinfo(deviceNode.toSummary());
		
		// 执行播测
		MResult mres = testingService.doTesting(deviceNode);
		if(mres.isOk()) {
			String warnmsg = mres.getParamT("warnmsg");
			testingVo.setResult(1);
			testingVo.setOutdata(warnmsg);
		} else {
			String warnmsg = mres.getParamT("warnmsg");
			if(StringUtils.isEmpty(warnmsg))
				warnmsg = mres.getErrmsg();
			testingVo.setResult(0);
			testingVo.setOutdata(warnmsg);
		}
		
		/*
		String mtype = deviceNode.getMonitortype();
		if("PING".equalsIgnoreCase(mtype)) {
			PingResult result = TestingUtils.ping(deviceNode.getIp());
			testingVo.setResult(result.isSuccess() ? 1 : 0);
			testingVo.setOutdata(result.getOutput());
		} else if("CURL".equalsIgnoreCase(mtype)) {
			MResult mres = testingService.doUrlTesting(appconfig, deviceNode);
			if(mres.isOk()) {
				String warnmsg = mres.getParamT("warnmsg");
				testingVo.setResult(1);
				testingVo.setOutdata(warnmsg);
			} else {
				testingVo.setResult(0);
				testingVo.setOutdata(mres.getErrmsg());
			}
		} else if("TCP".equalsIgnoreCase(mtype)) {
			
		} else {
			return null;
		}
		*/

		testingVo.setUsetime((System.currentTimeMillis() - time1) + "ms");
		return testingVo;
	}
	
}
