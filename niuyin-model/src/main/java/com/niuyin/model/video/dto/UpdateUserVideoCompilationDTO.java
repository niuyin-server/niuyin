package com.niuyin.model.video.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * UpdateUserVideoCompilationDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/12/28
 **/
@Data
public class UpdateUserVideoCompilationDTO {
    /**
     * compilation_id
     */
    private Long compilationId;
    /**
     * 合集标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 20, message = "标题需在20字符以内")
    private String title;
    /**
     * 描述
     */
    @Size(min = 1, max = 200, message = "描述需在200字符以内")
    private String description;
    /**
     * 合集封面(5M)
     */
    private String coverImage;
}
