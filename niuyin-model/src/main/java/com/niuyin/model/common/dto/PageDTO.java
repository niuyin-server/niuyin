package com.niuyin.model.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * PageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/1
 **/
@Data
public class PageDTO {
    @NotNull(message = "页码不能为空")
    private Integer pageNum = 1;
    @NotNull(message = "页大小不能为空")
    private Integer pageSize = 10;
}
