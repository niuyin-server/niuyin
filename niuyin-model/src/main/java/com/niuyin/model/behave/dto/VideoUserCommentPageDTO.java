package com.niuyin.model.behave.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * VideoUserCommentPageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/30
 **/
@Data
public class VideoUserCommentPageDTO {
    @NotNull
    private String videoId;
    /**
     * 视频查询排序（0发布时间，1点赞数）
     *
     * @see com.niuyin.model.common.enums.VideoCommentPageOrderEnum
     */
    private String orderBy;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
