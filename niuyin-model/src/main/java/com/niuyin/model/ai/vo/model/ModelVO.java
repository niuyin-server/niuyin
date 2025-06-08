package com.niuyin.model.ai.vo.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * ModelVO
 *
 * @AUTHOR: roydon
 * @DATE: 2025/6/7
 **/
@Data
public class ModelVO {

    private Long id;
    private String name;
    private String model;
    private String platform;
    private String type;
    /**
     * 温度参数
     */
    private BigDecimal temperature;
    /**
     * 单条回复的最大 Token 数量
     */
    private Integer maxTokens;
    /**
     * 上下文的最大 Message 数量
     */
    private Integer maxContexts;

    // 额外参数
    private String icon;

}
