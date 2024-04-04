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
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
