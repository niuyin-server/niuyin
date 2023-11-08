package com.niuyin.service.member.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.niuyin.feign.member.fallback")
public class InitConfig {
}
