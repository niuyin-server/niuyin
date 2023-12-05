package com.niuyin.model.creator.dto;

import lombok.Data;

/**
 * 视频合集分页dto
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/5
 **/
@Data
public class videoCompilationPageDTO {
    private String title;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
