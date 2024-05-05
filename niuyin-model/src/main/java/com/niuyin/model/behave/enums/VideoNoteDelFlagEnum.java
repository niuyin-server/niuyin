package com.niuyin.model.behave.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * VideoNoteDelFlagEnum
 *
 * @AUTHOR: roydon
 * @DATE: 2024/5/5
 **/
@Getter
@AllArgsConstructor
public enum VideoNoteDelFlagEnum {

    NORMAL("0", "正常"),
    DISABLE("1", "禁用"),
    DELETE("2", "删除"),
    ;

    private final String code;
    private final String desc;

    public static VideoNoteDelFlagEnum findByCode(String code) {
        for (VideoNoteDelFlagEnum value : VideoNoteDelFlagEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

}
