package com.niuyin.model.search.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * UserSearchKeywordDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2024/10/11
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserSearchKeywordDTO extends PageDTO {
    /**
     * 关键词
     */
    @NotBlank(message = "搜索关键词不能为空")
    private String keyword;
}
