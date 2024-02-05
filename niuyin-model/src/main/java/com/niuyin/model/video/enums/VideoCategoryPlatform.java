package com.niuyin.model.video.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 视频分类平台枚举
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/5
 **/
@Getter
@AllArgsConstructor
public enum VideoCategoryPlatform {

    DEFAULT("0", "默认"),
    WEB("1", "web"),
    APP("2", "app"),
    ;

    private final String code;
    private final String info;

    public static VideoCategoryPlatform findByCode(String code) {
        for (VideoCategoryPlatform value : VideoCategoryPlatform.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
