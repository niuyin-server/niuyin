package com.niuyin.model.ai.image.dto;

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
    private String radio = "0";
}
