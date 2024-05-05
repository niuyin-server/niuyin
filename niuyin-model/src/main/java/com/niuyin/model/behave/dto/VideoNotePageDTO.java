package com.niuyin.model.behave.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

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
