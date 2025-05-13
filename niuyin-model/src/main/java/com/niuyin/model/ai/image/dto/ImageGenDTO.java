package com.niuyin.model.ai.image.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ImageGenDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2025/5/10
 **/
@Data
public class ImageGenDTO {
    @NotBlank(message = "请输入提示词")
    private String message;
    // 图片比例，默认1:1
    private String radio = "1:1";
    // 图片数量，默认1，最高4
    @Min(value = 1, message = "图片数量不能小于1")
    @Max(value = 4, message = "最多生成4张图片")
    private Integer batchSize = 1;
}
