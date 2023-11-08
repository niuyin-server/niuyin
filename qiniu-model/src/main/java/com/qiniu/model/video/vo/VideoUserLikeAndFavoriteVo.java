package com.qiniu.model.video.vo;

import lombok.Data;

/**
 * 功能：
 * 作者：lzq
 * 日期：2023/10/31 19:20
 */
@Data
public class VideoUserLikeAndFavoriteVo {

    private String videoTitle;
    private String videoDesc;
    /**
     * 视频地址
     */
    private String videoUrl;
    private Long viewNum;
    private Long likeNum;
    private Long favoritesNum;

}
