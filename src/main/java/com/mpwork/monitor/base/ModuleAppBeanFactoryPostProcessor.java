package com.mpwork.monitor.base;

import org.mframework.admin.AdminModuleService;
import org.mframework.admin.MPostProcessorBase;
import org.mframework.admin.MenuItem;
import org.mframework.base.MApplicationEvent;
import org.mframework.install.InstallService;
import org.mframework.utils.MPageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mpwork.apputils.support.MpworkControlPanelSupport;
import com.mpwork.monitor.service.JobScheduleService;
import com.weapi.wclient.common.WeapiBeanAclAdapter;

/**
 * SpringBean初始化前（钩子）：用于动态修改类映射
 * 兼容Tomcat/SpringBoot方式启动
 * 20190609
 * @author dbb
 *
 */
@Component
public class ModuleAppBeanFactoryPostProcessor extends MPostProcessorBase {

	@Autowired
	private AdminModuleService adminModuleService;
	@Autowired
	private ConsoleModuleHandler consoleModuleHandler;
	@Autowired
	private InstallService installservice;
	@Autowired
	private JobScheduleService jobScheduleService;
	
	// 自定义事件处理
	@Override
	protected void onMApplicationEvent(MApplicationEvent event) {
		// do nothing, 不可删除
	}

	@Override
	protected void onContentStop() {
		System.out.println("onContentStop");
	}

	@Override
	protected void onContextStarting() {
		adminModuleService.setCustomModuleHandler(consoleModuleHandler);
		adminModuleService.addDefaultBeanAcl(new WeapiBeanAclAdapter());
		
        // 库表同步扫描
		installservice.mbeanTableSync();
		
		// 向Mpwork[控制面板-插件]中注册应用入口
		MenuItem menuItem = MenuItem.buildByMenuKey("MPWORK.MENU.MMODULE.MONITOR", "运行监控", "SYS", MPageUtils.buildFrameURL("mframework/admin/index"), "fa-solid fa-earth-asia", 200);
		MpworkControlPanelSupport.registerControlPanelMenu(menuItem);

		// 自动启动激活状态下的任务计划
		jobScheduleService.startSchedulerAndJobs();	// 15s后
	}

}
