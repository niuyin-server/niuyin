package com.niuyin.tools.es;

import com.niuyin.common.cache.annotations.EnableCacheConfig;
import com.niuyin.common.core.config.MybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * EsApplication
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/14
 **/
@SpringBootApplication
@EnableCacheConfig
@Import({MybatisPlusConfig.class})
@MapperScan("com.niuyin.tools.es.mapper")
public class EsApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsApplication.class, args);
    }
}
