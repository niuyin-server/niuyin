package com.niuyin.service.social;

import com.niuyin.common.cache.annotations.EnableCacheConfig;
import com.niuyin.common.core.annotations.EnableUserTokenInterceptor;
import com.niuyin.common.core.config.MybatisPlusConfig;
import com.niuyin.common.core.swagger.Swagger2Configuration;
import com.niuyin.feign.config.FeignConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * SocialApplication
 * 社交服务
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/2
 **/
@SpringBootApplication(scanBasePackages = {"com.niuyin.service.social", "com.niuyin.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.niuyin.feign", defaultConfiguration = {FeignConfig.class})
@EnableUserTokenInterceptor
@EnableCacheConfig
@EnableAsync
@EnableDubbo
@Import({MybatisPlusConfig.class})
public class SocialApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(SocialApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("                                                                                                                                                            \n" +
                "                                                                                                                                                            \n" +
                "                 ___                                 ___                                                                                                    \n" +
                "               ,--.'|_                             ,--.'|_                                                                                                  \n" +
                "               |  | :,'                  __  ,-.   |  | :,'                                ,--,                                                             \n" +
                "  .--.--.      :  : ' :                ,' ,'/ /|   :  : ' :            .--.--.           ,'_ /|                                     .--.--.      .--.--.    \n" +
                " /  /    '   .;__,'  /      ,--.--.    '  | |' | .;__,'  /            /  /    '     .--. |  | :     ,---.      ,---.      ,---.    /  /    '    /  /    '   \n" +
                "|  :  /`./   |  |   |      /       \\   |  |   ,' |  |   |            |  :  /`./   ,'_ /| :  . |    /     \\    /     \\    /     \\  |  :  /`./   |  :  /`./   \n" +
                "|  :  ;_     :__,'| :     .--.  .-. |  '  :  /   :__,'| :            |  :  ;_     |  ' | |  . .   /    / '   /    / '   /    /  | |  :  ;_     |  :  ;_     \n" +
                " \\  \\    `.    '  : |__    \\__\\/: . .  |  | '      '  : |__           \\  \\    `.  |  | ' |  | |  .    ' /   .    ' /   .    ' / |  \\  \\    `.   \\  \\    `.  \n" +
                "  `----.   \\   |  | '.'|   ,\" .--.; |  ;  : |      |  | '.'|           `----.   \\ :  | : ;  ; |  '   ; :__  '   ; :__  '   ;   /|   `----.   \\   `----.   \\ \n" +
                " /  /`--'  /   ;  :    ;  /  /  ,.  |  |  , ;      ;  :    ;          /  /`--'  / '  :  `--'   \\ '   | '.'| '   | '.'| '   |  / |  /  /`--'  /  /  /`--'  / \n" +
                "'--'.     /    |  ,   /  ;  :   .'   \\  ---'       |  ,   /          '--'.     /  :  ,      .-./ |   :    : |   :    : |   :    | '--'.     /  '--'.     /  \n" +
                "  `--'---'      ---`-'   |  ,     .-./              ---`-'             `--'---'    `--`----'      \\   \\  /   \\   \\  /   \\   \\  /    `--'---'     `--'---'   \n" +
                "                          `--`---'                                                                 `----'     `----'     `----'                             \n" +
                "                                                                                                                                                            ");
    }
}
