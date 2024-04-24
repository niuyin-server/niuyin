package com.niuyin.model.behave.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户收藏夹对外展示状态
 * 0 ： 公开
 * 1 ： 私密
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/24
 **/
@Getter
@AllArgsConstructor
public enum FavoriteFolderShowStatusEnum {

    PUBLIC("0", "公开"),
    PRIVATE("1", "私密"),
    ;

    private final String code;
    private final String desc;

    public static FavoriteFolderShowStatusEnum findByCode(String code) {
        for (FavoriteFolderShowStatusEnum value : FavoriteFolderShowStatusEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

}
