package com.niuyin.model.behave.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * UserFavoriteVideoVO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/20
 **/
@Data
public class UserFavoriteVideoVO {
    private String videoId;
    private String userId;
    private String userNickName;
    private String videoTitle;
    private String videoDesc;
    private String coverImage;
    private String videoUrl;
    private Long likeNum;
    private String publishType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime favoriteTime;
}
