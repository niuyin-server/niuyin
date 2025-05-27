package com.niuyin.common.core.config;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.filter.UserTokenInterceptor;
import com.niuyin.common.core.utils.json.JsonUtils;
import com.niuyin.common.core.utils.json.databind.NumberSerializer;
import com.niuyin.common.core.utils.json.databind.TimestampLocalDateTimeDeserializer;
import com.niuyin.common.core.utils.json.databind.TimestampLocalDateTimeSerializer;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final UserTokenInterceptor userTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTokenInterceptor).addPathPatterns("/**");
    }

    // todo 2-3 启用尾斜线匹配
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
        configurePathMatch(configurer, webProperties.getAdminApi());
        configurePathMatch(configurer, webProperties.getWebApi());
        configurePathMatch(configurer, webProperties.getAppApi());
    }

    /**
     * 设置 API 前缀，仅仅匹配 controller 包下的
     *
     * @param configurer 配置
     * @param api        API 配置
     */
    private void configurePathMatch(PathMatchConfigurer configurer, WebProperties.Api api) {
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
        configurer.addPathPrefix(api.getPrefix(), clazz -> clazz.isAnnotationPresent(RestController.class)
                && antPathMatcher.match(api.getController(), clazz.getPackage().getName())); // 仅仅匹配 controller 包
    }

    @Bean
    public SnowFlake snowFlake() {
        return new SnowFlake();
    }

//    @Bean
//    @SuppressWarnings("InstantiationOfUtilityClass")
//    public JsonUtils jsonUtils(List<ObjectMapper> objectMappers) {
//        // 1.1 创建 SimpleModule 对象
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule
//                // 新增 Long 类型序列化规则，数值超过 2^53-1，在 JS 会出现精度丢失问题，因此 Long 自动序列化为字符串类型
//                .addSerializer(Long.class, NumberSerializer.INSTANCE)
//                .addSerializer(Long.TYPE, NumberSerializer.INSTANCE)
//                .addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE)
//                .addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE)
//                .addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE)
//                .addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE)
//                // 新增 LocalDateTime 序列化、反序列化规则，使用 Long 时间戳
//                .addSerializer(LocalDateTime.class, TimestampLocalDateTimeSerializer.INSTANCE)
//                .addDeserializer(LocalDateTime.class, TimestampLocalDateTimeDeserializer.INSTANCE);
//        // 1.2 注册到 objectMapper
//        objectMappers.forEach(objectMapper -> objectMapper.registerModule(simpleModule));
//
//        // 2. 设置 objectMapper 到 JsonUtils
//        JsonUtils.init(CollUtil.getFirst(objectMappers));
//        return new JsonUtils();
//    }
}
