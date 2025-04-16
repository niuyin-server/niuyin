package com.niuyin.common.core.swagger;

import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableSwagger2
//@EnableKnife4j
//@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger2Configuration {

//    @Bean(value = "defaultApi2")
//    public Docket defaultApi2() {
//        Docket docket = new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                //分组名称
//                .groupName("1.0")
//                .select()
//                //这里指定Controller扫描包路径
//                .apis(RequestHandlerSelectors.basePackage("com.niuyin"))
//                .paths(PathSelectors.any())
//                .build();
//        return docket;
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("API文档")
//                .description("API文档")
//                .version("1.0")
//                .build();
//    }
}
