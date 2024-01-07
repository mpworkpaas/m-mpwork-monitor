package com.mpwork.monitor.base;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.mframework.admin.CustomModuleHandler;
import org.mframework.admin.FrameInitResult;
import org.mframework.admin.LoginInfo;
import org.mframework.admin.MenuItem;
import org.mframework.admin.ModuleInterface;
import org.mframework.admin.common.Breadcrumb;
import org.mframework.bean.MAdminModule;
import org.mframework.utils.ModulePathUtils;
import org.springframework.stereotype.Component;

import com.weapi.wclient.common.vo.WClientModule;
import com.weapi.wclient.utils.WClientUtils;

/**
 * 根据用户帐号所开通的功能生成菜单列表
 * @author dbb
 *
 */
@Component
public class ConsoleModuleHandler extends CustomModuleHandler {

	// 初始化菜单列表
	@Override
	protected void initMenus(){	
		allMenuList = new ArrayList<MenuItem>();
		
		MenuItem viewItem = MenuItem.build("", "监控概览", "#", "fa-area-chart", 1000);
		viewItem.appendSubMenuItem(MenuItem.build("", "概览",  ModulePathUtils.buildUrl(""), "fa-television", 1000));
		// viewItem.appendSubMenuItem(MenuItem.build("", "设备拓扑", ModulePathUtils.buildUrl(""), "fa-th-large", 1000));
		viewItem.appendSubMenuItem(MenuItem.build("", "告警记录", ModulePathUtils.buildUrl("mpwork/monitor/warnrecord/list"), "fa-th-large", 1000));
		allMenuList.add(viewItem);
		
		MenuItem queryItem = MenuItem.build("", "设备管理", "#", "fa-circle-o-notch", 1000);
		queryItem.appendSubMenuItem(MenuItem.build("", "设备列表", ModulePathUtils.buildUrl("mpwork/monitor/devicenode/list"), "fa-circle-o-notch", 1000));
		queryItem.appendSubMenuItem(MenuItem.build("", "设备分类", ModulePathUtils.buildUrl("mpwork/monitor/category/list"), "fa-list-alt", 1000));
		allMenuList.add(queryItem);
		
		MenuItem recordItem = MenuItem.build("", "告警通知设置", "#", "fa-circle-o-notch", 1000);
		recordItem.appendSubMenuItem(MenuItem.build("", "参数配置", ModulePathUtils.buildUrl("mpwork/monitor/config"), "fa-list-alt", 1000));
		recordItem.appendSubMenuItem(MenuItem.build("", "告警规则管理", ModulePathUtils.buildUrl("mpwork/monitor/warnrule/list"), "fa-list-alt", 1000));
		recordItem.appendSubMenuItem(MenuItem.build("", "通知渠道管理", ModulePathUtils.buildUrl("mpwork/monitor/receiver/list"), "fa-list-alt", 1000));
		recordItem.appendSubMenuItem(MenuItem.build("", "通知记录", ModulePathUtils.buildUrl("mpwork/monitor/warnnotifyrecord/list"), "fa-list-alt", 1000));
		allMenuList.add(recordItem);
	}

	/**
	 * MFramework Frame页面预处理器
	 */
	@Override
	public FrameInitResult beforeFrameInit(MAdminModule madminmodule, ModuleInterface moduleInstance, HttpServletRequest request, LoginInfo loginInfo) {
		// 初始化默认的面包屑导航
		WClientModule wclientmodule = WClientModule.getFromRequest(request);
		if (wclientmodule != null) {
			Breadcrumb breadcrumb = Breadcrumb.buildBreadcrumb(wclientmodule.getModulename(), WClientUtils.buildFrameURL("mframework/admin/index", wclientmodule));
			request.setAttribute("breadcrumb", breadcrumb);
		}
		
		return null;
	}

}
