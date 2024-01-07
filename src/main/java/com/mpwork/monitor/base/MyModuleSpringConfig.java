package com.mpwork.monitor.base;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({
	"classpath:META-INF/spring/mmodule-common.xml",
	"classpath:META-INF/spring/mmodule.xml",
})
public class MyModuleSpringConfig {
    
    
    
}