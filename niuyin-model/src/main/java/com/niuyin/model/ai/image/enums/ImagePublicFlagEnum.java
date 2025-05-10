package com.niuyin.model.ai.image.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 图片公开状态
 * 0: 私密
 * 1: 公开
 *
 * @AUTHOR: roydon
 * @DATE: 2025/5/10
 **/
@Getter
@AllArgsConstructor
public enum ImagePublicFlagEnum {
    PRIVATE("0", "私密"),
    PUBLIC("1", "公开");

    private final String code;
    private final String message;

    public static ImagePublicFlagEnum getByCode(String code) {
        for (ImagePublicFlagEnum value : ImagePublicFlagEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
