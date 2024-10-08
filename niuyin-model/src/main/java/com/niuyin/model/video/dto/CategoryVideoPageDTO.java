package com.niuyin.model.video.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 分类视频分页dto
 *
 * @AUTHOR: roydon
 * @DATE: 2024/2/5
 **/
@Data
public class CategoryVideoPageDTO {
    @NotNull
    private Long id; // 分类id
    @NotNull
    private Integer pageNum; // 页码
    @NotNull
    private Integer pageSize; // 每页数量
}
