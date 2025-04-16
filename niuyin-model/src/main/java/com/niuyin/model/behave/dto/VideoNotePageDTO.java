package com.niuyin.model.behave.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * VideoNotePageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2023/11/1
 **/
@Data
public class VideoNotePageDTO {
    @NotEmpty
    @NotBlank
    private String videoId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
