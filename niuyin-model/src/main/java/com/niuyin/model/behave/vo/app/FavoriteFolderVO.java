package com.niuyin.model.behave.vo.app;

import com.niuyin.model.behave.domain.UserFavorite;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏夹vo
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/24
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class FavoriteFolderVO extends UserFavorite {
    // 收藏视频数量
    private Long videoCount;
    // 该视频是否在此收藏夹中
    private Boolean weatherFavorite;
}
