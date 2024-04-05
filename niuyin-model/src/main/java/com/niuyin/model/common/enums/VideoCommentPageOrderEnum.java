package com.niuyin.model.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 视频评论分页排序枚举
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/4
 **/
@Getter
@AllArgsConstructor
public enum VideoCommentPageOrderEnum {

    CREATE_TIME("0", "create_time"),
    LIKE_NUM("1", "like_num"),
    ;

    private final String code;
    private final String info;

    public static VideoCommentPageOrderEnum findByCode(String code) {
        for (VideoCommentPageOrderEnum value : VideoCommentPageOrderEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

}
