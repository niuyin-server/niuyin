package com.niuyin.model.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短视频平台枚举
 * 目前存在3个端：0web；1app；2后台
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/3
 **/
@Getter
@AllArgsConstructor
public enum VideoPlatformEnum {

    WEB("0", "web"),
    APP("1", "app"),
    ;

    private final String code;
    private final String info;

    public static VideoPlatformEnum findByCode(String code) {
        for (VideoPlatformEnum value : VideoPlatformEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

}
