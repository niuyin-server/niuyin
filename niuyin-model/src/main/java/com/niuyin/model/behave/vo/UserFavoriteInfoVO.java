package com.niuyin.model.behave.vo;

import com.niuyin.model.behave.domain.UserFavorite;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * UserFavoriteInfoVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/19
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserFavoriteInfoVO extends UserFavorite {

    // 收藏视频数量
    private Long videoCount;

    // 最近收藏视频封面集合
    private String[] videoCoverList;

}
