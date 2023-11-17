package com.niuyin.model.behave.dto;

import lombok.Data;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/11/17 11:38
 */
@Data
public class UserFavoriteVideoDTO {

    /**
     * 视频id
     */
    private String videoId;

    /**
     * 收藏夹id集合
     */
    private Long[] favorites;
}
