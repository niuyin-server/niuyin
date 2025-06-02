package com.niuyin.common.ai.enums;

import com.niuyin.common.core.domain.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * AI 模型类型的枚举
 */
@Getter
@RequiredArgsConstructor
public enum AiModelTypeEnum implements ArrayValuable<String> {

    CHAT("1", "对话"),
    IMAGE("2", "绘图"),
    VOICE("3", "语音"),
    VIDEO("4", "视频"),
    EMBEDDING("5", "向量"),
    RERANK("6", "重排序");

    /**
     * 类型
     */
    private final String type;
    /**
     * 类型名
     */
    private final String name;

    public static final String[] ARRAYS = Arrays.stream(values()).map(AiModelTypeEnum::getType).toArray(String[]::new);

    @Override
    public String[] array() {
        return ARRAYS;
    }

}
