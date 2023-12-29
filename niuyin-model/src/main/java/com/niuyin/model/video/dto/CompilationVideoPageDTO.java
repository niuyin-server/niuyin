package com.niuyin.model.video.dto;

import lombok.Data;

/**
 * CompilationVideoPageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/28
 **/
@Data
public class CompilationVideoPageDTO {

    private Long compilationId;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
