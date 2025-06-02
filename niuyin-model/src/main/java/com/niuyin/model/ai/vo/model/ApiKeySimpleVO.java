package com.niuyin.model.ai.vo.model;

import lombok.Data;

/**
 * ApiKeySimpleVO
 *
 * @AUTHOR: roydon
 * @DATE: 2025/6/2
 **/
@Data
public class ApiKeySimpleVO {
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 平台
     */
    private String platform;
}
