package com.niuyin.model.ai.image.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 图片比例枚举
 * 1:1 : 1024x1024
 * 1:2 : 1024x2048
 * 3:4 : 1536x2048
 * 4:3 : 2048x1536
 * 16:9 : 2048x1152
 * 9:16 : 1152x2048
 *
 * @AUTHOR: roydon
 * @DATE: 2025/5/10
 **/
@Getter
@AllArgsConstructor
public enum ImageRadioEnum {
    ONE_ONE("1:1", 1024, 1024),
    ONE_TWO("1:2", 1024, 2048),
    THREE_FOUR("3:4", 1536, 2048),
    FOUR_THREE("4:3", 2048, 1536),
    SIXTEEN_NINE("16:9", 2048, 1152),
    NINE_SIXTEEN("9:16", 1152, 2048);

    private final String code;
    private final Integer width;
    private final Integer height;

    public static ImageRadioEnum getByCode(String code) {
        for (ImageRadioEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
