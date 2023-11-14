package com.niuyin.model.behave.dto;

import com.niuyin.model.behave.domain.VideoUserComment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * VideoUserCommentPageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/30
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoUserCommentPageDTO extends VideoUserComment {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
