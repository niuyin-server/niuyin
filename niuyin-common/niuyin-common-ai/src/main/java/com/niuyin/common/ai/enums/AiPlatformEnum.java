package com.niuyin.common.ai.enums;

import com.niuyin.common.core.domain.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 模型平台
 */
@Getter
@AllArgsConstructor
public enum AiPlatformEnum implements ArrayValuable<String> {

    // ========== 国内平台 ==========

    TONG_YI("TongYi", "通义千问", "icon-TongYi"), // 阿里
    YI_YAN("YiYan", "文心一言", "icon-YiYan"), // 百度
    DEEP_SEEK("DeepSeek", "DeepSeek", "icon-DeepSeek"), // DeepSeek
    ZHI_PU("ZhiPu", "智谱", "icon-ZhiPu"), // 智谱 AI
    XING_HUO("XingHuo", "星火", "icon-XingHuo"), // 讯飞
    DOU_BAO("DouBao", "豆包", "icon-DouBao"), // 字节
    HUN_YUAN("HunYuan", "混元", "icon-HunYuan"), // 腾讯
    SILICON_FLOW("SiliconFlow", "硅基流动", "icon-SiliconFlow"), // 硅基流动
    MINI_MAX("MiniMax", "MiniMax", "icon-MiniMax"), // 稀宇科技
    MOONSHOT("Moonshot", "月之暗面", "icon-Moonshot"), // KIMI
    BAI_CHUAN("BaiChuan", "百川智能", "icon-BaiChuan"), // 百川智能

    // ========== 国外平台 ==========

    OPENAI("OpenAI", "OpenAI", "icon-OpenAI"), // OpenAI 官方
    AZURE_OPENAI("AzureOpenAI", "AzureOpenAI", "icon-AzureOpenAI"), // OpenAI 微软
    OLLAMA("Ollama", "Ollama", "icon-Ollama"),

    STABLE_DIFFUSION("StableDiffusion", "StableDiffusion", "icon-StableDiffusion"), // Stability AI
    MIDJOURNEY("Midjourney", "Midjourney", "icon-Midjourney"), // Midjourney
    SUNO("Suno", "Suno", "icon-Suno"), // Suno AI

    ;

    /**
     * 平台
     */
    private final String platform;
    /**
     * 平台名
     */
    private final String name;
    /**
     * 平台图标
     */
    private final String icon;

    public static final String[] ARRAYS = Arrays.stream(values()).map(AiPlatformEnum::getPlatform).toArray(String[]::new);

    public static AiPlatformEnum validatePlatform(String platform) {
        for (AiPlatformEnum platformEnum : AiPlatformEnum.values()) {
            if (platformEnum.getPlatform().equals(platform)) {
                return platformEnum;
            }
        }
        throw new IllegalArgumentException("非法平台： " + platform);
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }

    public static AiPlatformEnum getByPlatform(String platform) {
        for (AiPlatformEnum value : values()) {
            if (value.getPlatform().equals(platform)) {
                return value;
            }
        }
        return null;
    }

}
