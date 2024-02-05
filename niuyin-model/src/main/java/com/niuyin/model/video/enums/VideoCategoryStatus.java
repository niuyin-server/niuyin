package com.niuyin.model.video.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分类状态0-正常，1-禁用
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/5
 **/
@Getter
@AllArgsConstructor
public enum VideoCategoryStatus {

    NORMAL("0", "正常"),
    DISABLE("1", "禁用"),
    ;

    private final String code;
    private final String info;

    public static VideoCategoryStatus findByCode(String code) {
        for (VideoCategoryStatus value : VideoCategoryStatus.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
