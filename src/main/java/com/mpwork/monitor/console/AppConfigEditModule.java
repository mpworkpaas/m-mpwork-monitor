package com.mpwork.monitor.console;

import org.mframework.annotation.MModuleMeta;
import org.mframework.common.BeanInfo;
import org.springframework.stereotype.Component;

import com.mpwork.apputils.modulebase.ConfigEditModuleBase;
import com.mpwork.monitor.bean.AppConfig;

/**
 * 应用参数设置
 * @author dbb
 *
 */
@MModuleMeta(modulename = "应用参数设置", modulepath = "mpwork/monitor/config")
@Component
public class AppConfigEditModule extends ConfigEditModuleBase {

	private static BeanInfo beanInfo = BeanInfo.getBeanInfo(AppConfig.class);
	
	public AppConfigEditModule() {
		super(beanInfo, "");
//		this.navtabAdapter = new ConfigNavTab("BASE");
	}

//	// 页签
//	public static class ConfigNavTab extends NavTabBase {
//		public ConfigNavTab(String currTabid) {
//			this.currTabid = currTabid;
//		}
//		
//		public List<MNavTab> buildNavTab(){
//			List<MNavTab> navtabList = new ArrayList<MNavTab>();
//			navtabList.add(MNavTab.build("BASE", "通用设置", "mpwork/kbase/config"));
//			navtabList.add(MNavTab.build("STOPWORD", "停用词管理", "mpwork/kbase/config/stopword")); 
//			return navtabList;
//		}
//	}
}
