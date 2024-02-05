package com.niuyin.model.common.dto;

import lombok.Data;

/**
 * PageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/1
 **/
@Data
public class PageDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
