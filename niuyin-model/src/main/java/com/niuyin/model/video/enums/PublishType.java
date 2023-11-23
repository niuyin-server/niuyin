package com.niuyin.model.video.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PublishType
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/21
 * 视频布类型（0视频，1图文）
 **/
@Getter
@AllArgsConstructor
public enum PublishType {

    VIDEO("0", "视频"),
    IMAGE("1", "图文"),
    ;

    private final String code;
    private final String info;

    public static PublishType findByCode(String code) {
        for (PublishType value : PublishType.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
