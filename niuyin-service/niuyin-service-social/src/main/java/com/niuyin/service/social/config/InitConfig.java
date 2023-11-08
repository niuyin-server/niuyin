package com.niuyin.service.social.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.niuyin.feign.user.fallback")
public class InitConfig {
}
