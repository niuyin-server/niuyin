package com.niuyin.model.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知类型(0：点赞，1：收藏，2：关注、3：回复评论、4：赞了评论)
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/8
 **/
@Getter
@AllArgsConstructor
public enum NoticeTypeEnum {

    LIKE("0", "点赞"),
    FAVORITE("1", "收藏"),
    FOLLOW("2", "关注"),
    REPLAY("3", "回复评论"),
    LIKE_COMMENT("4", "赞了评论"),
    ;

    private final String code;
    private final String info;

    public static NoticeTypeEnum findByCode(String code) {
        for (NoticeTypeEnum value : NoticeTypeEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

}
