package com.qiniu.service.behave.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 视频评论状态:0默认1已删除
 **/
@Getter
@AllArgsConstructor
public enum VideoCommentStatus {

    NORMAL("0", "默认"),

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
