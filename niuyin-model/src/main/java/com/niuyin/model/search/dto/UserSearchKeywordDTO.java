package com.niuyin.model.search.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

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
