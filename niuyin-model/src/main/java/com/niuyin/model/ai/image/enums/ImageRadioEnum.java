package com.niuyin.model.ai.image.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 图片比例枚举
 * 0:  1:1 : 1024x1024
 * 1:  3:4 : 1536x2048
 * 2:  4:3 : 2048x1536
 * 3:  16:9 : 2048x1152
 * 4:  9:16 : 1152x2048
 *
 * @AUTHOR: roydon
 * @DATE: 2025/5/10
 **/
@Getter
@AllArgsConstructor
public enum ImageRadioEnum {
    ONE_ONE("0", "1:1", 1024, 1024),
    THREE_FOUR("1", "3:4", 1536, 2048),
    FOUR_THREE("2", "4:3", 2048, 1536),
    SIXTEEN_NINE("3", "16:9", 2048, 1152),
    NINE_SIXTEEN("4", "9:16", 1152, 2048);

    private final String code;
    private final String message;
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
