package com.niuyin.model.behave.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户视频行为枚举
 * 1 view 观看（非点击，前端埋点若观看视频时长的1/5则收集此视频观看行为）、2 like 点赞、3 comment 评论、4 favorite 收藏
 * 限制某用户对单个视频评分最高阈值（为1 view weight+ 2 like weight + 3 comment weight + 4 favorite weight = 10 weight）
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/19
 **/
@Getter
@AllArgsConstructor
public enum UserVideoBehaveEnum {

    NOT_BEHAVE("0", "无行为", 0),
    VIEW("1", "观看", 1),
    LIKE("2", "点赞", 2),
    COMMENT("3", "评论", 3),
    FAVORITE("4", "收藏", 4),
    ;

    private final String code;
    private final String desc;
    private final Integer score;

    public static UserVideoBehaveEnum findByCode(String code) {
        for (UserVideoBehaveEnum value : UserVideoBehaveEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

}
