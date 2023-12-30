package com.niuyin.service.search.domain.vo;

import com.niuyin.model.video.domain.VideoPosition;
import com.niuyin.service.search.domain.VideoSearchVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * VideoSearchUserVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/3
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoSearchUserVO extends VideoSearchVO {

    private Long likeNum = 0L;
    private Long favoriteNum = 0L;
    private Long commentNum = 0L;

    private String userNickName;
    private String userAvatar;
    // 是否点赞
    private boolean weatherLike;
    // 是否收藏
    private boolean weatherFavorite;
    // 是否关注
    private boolean weatherFollow;
    // 标签数组
    private String[] tags;
    // 图片集合
    private String[] imageList;
    // 位置信息
    private VideoPosition position;

    // 热力值
    private Double hotScore;
}
