package com.niuyin.model.video.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ik分词器分词模式枚举
 *
 * @AUTHOR: roydon
 * @DATE: 2024/9/25
 **/
@Getter
@AllArgsConstructor
public enum IkAnalyzeTypeEnum {

    IK_SMART("ik_smart", "最少切分分词"),
    IK_MAX_WORD("ik_max_word", "最细切分分词"),
    ;

    private final String code;
    private final String info;

    public static IkAnalyzeTypeEnum findByCode(String code) {
        for (IkAnalyzeTypeEnum value : IkAnalyzeTypeEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
