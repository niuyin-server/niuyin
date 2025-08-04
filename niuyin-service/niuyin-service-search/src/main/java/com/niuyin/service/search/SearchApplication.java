package com.niuyin.service.search;

import com.niuyin.common.cache.annotations.EnableCacheConfig;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.niuyin.service.search", "com.niuyin.common"})
@EnableDiscoveryClient
@EnableAsync
@EnableScheduling
@EnableCaching
@EnableCacheConfig
@EnableFeignClients(basePackages = "com.niuyin.feign")
public class SearchApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }

    /**
     * mq的消息转换器防止乱码
     */
    @Bean
    public MessageConverter jacksonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setCreateMessageIds(true);
        return converter;
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
