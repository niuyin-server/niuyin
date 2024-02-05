package com.niuyin.model.search.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 视频搜索筛选：根据发布时间筛选
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/30
 **/
@Getter
@AllArgsConstructor
public enum VideoSearchScreenPublishTime {

    NO_LIMIT("0", 0),
    ONE_DAY("1", 1),
    ONE_WEEK("2", 7),
    ONE_MONTH("3", 31),
    ;

    private final String code;
    private final int limit;

    public static VideoSearchScreenPublishTime findByCode(String code) {
        for (VideoSearchScreenPublishTime value : VideoSearchScreenPublishTime.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

}
