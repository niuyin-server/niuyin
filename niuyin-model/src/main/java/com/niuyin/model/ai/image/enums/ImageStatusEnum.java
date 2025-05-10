package com.niuyin.model.ai.image.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 绘画状态
 * 0：未完成
 * 1：完成
 * 2：失败
 *
 * @AUTHOR: roydon
 * @DATE: 2025/5/10
 **/
@Getter
@AllArgsConstructor
public enum ImageStatusEnum {
    UNFINISHED("0", "进行中"),
    FINISHED("1", "完成"),
    FAILED("2", "失败");

    private final String code;
    private final String message;

    public static ImageStatusEnum getByCode(String code) {
        for (ImageStatusEnum value : ImageStatusEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
