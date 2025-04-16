package com.niuyin.service.video.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * springdoc接口文档
 * 访问链接：http://server:port/swagger-ui/index.html
 */
@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("r")
                        .description("r")
                        .version("v0.0.1")
                        .license(new License().name("ri 1.0")));
    }

}
