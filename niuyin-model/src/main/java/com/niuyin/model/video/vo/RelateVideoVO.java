package com.niuyin.model.video.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 相关视频推荐返回体
 *
 * @AUTHOR: roydon
 * @DATE: 2024/1/14
 **/
@Data
public class RelateVideoVO {

    private String videoId;
    private Long userId;
    private String videoTitle;
    /**
     * 视频封面地址
     */
    private String coverImage;

    /**
     * 发布类型（0视频，1图文）
     */
    private String publishType;
    /**
     * 视频详情
     */
    private String videoInfo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    /**
     * 用户
     */
    private VideoAuthor videoAuthor;
    private VideoBehave videoBehave;
    private VideoSocial videoSocial;

    @Data
    @AllArgsConstructor
    public static class VideoAuthor {
        private Long userId;
        private String nickName;
        private String avatar;
    }

    @Data
    @AllArgsConstructor
    public static class VideoBehave {
        private Long viewNum;
        private Long likeNum;
        private Long favoriteNum;
        private Long commentNum;
    }

    @Data
    @AllArgsConstructor
    public static class VideoSocial {

        private Boolean weatherFollow;

    }

}
