package com.niuyin.service.ai;

import com.niuyin.common.cache.annotations.EnableCacheConfig;
import com.niuyin.common.core.annotations.EnableUserTokenInterceptor;
import com.niuyin.common.core.config.MybatisPlusConfig;
import com.niuyin.common.core.swagger.Swagger2Configuration;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * AiApplication
 *
 * @AUTHOR: roydon
 * @DATE: 2025/3/15
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.niuyin.feign")
@EnableUserTokenInterceptor
@EnableCacheConfig
@EnableAsync
@EnableDubbo
@Import({MybatisPlusConfig.class, Swagger2Configuration.class})
public class AiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiApplication.class, args);
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
