package com.niuyin.model.behave.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * (UserFavoriteVideo)实体类
 *
 * @author lzq
 * @since 2023-11-17 10:16:03
 */
@Data
@TableName("user_favorite_video")
public class UserFavoriteVideo implements Serializable {
    private static final long serialVersionUID = -25561243481195565L;
    /**
     * 收藏夹id
     */
    private Long favoriteId;
    /**
     * 视频id
     */
    private String videoId;

    /**
     * 用户id
     */
    private Long userId;


}

