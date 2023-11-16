package com.niuyin.service.behave.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserFavoriteStatus {
    NORMAL("0", "存在"),

    DELETED("1", "删除"),
    ;

    private final String code;
    private final String info;

    public static VideoCommentStatus findByCode(String code) {
        for (VideoCommentStatus value : VideoCommentStatus.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
