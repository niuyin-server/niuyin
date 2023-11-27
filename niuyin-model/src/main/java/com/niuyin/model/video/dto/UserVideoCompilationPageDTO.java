package com.niuyin.model.video.dto;

import com.niuyin.model.video.domain.UserVideoCompilation;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * UserVideoCompilationPageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/27
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserVideoCompilationPageDTO extends UserVideoCompilation {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
