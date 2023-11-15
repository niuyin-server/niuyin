package com.niuyin.tools.es;

import com.niuyin.common.annotations.EnableRedisConfig;
import com.niuyin.common.config.MybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * EsApplication
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/14
 **/
@SpringBootApplication
@EnableRedisConfig
@Import({MybatisPlusConfig.class})
@MapperScan("com.niuyin.tools.es.mapper")
public class EsApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsApplication.class, args);
    }
}
