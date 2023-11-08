package com.qiniu.service.user.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.qiniu.feign.user.fallback")
public class InitConfig {
}
