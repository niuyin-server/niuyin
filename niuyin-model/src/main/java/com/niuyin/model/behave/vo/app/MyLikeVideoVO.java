package com.niuyin.model.behave.vo.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * MyLikeVideoVO
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/2
 **/
@Data
public class MyLikeVideoVO {

    private String videoId;
    private String videoTitle; // 标题
    private String coverImage; // 封面
    private String publishType; // 发布类型（0视频，1图文）
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; // 发布时间
    private Long likeNum;  // 点赞量

}
