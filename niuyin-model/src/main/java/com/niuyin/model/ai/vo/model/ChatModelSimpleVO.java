package com.niuyin.model.ai.vo.model;

import lombok.Data;

/**
 * ChatModelSimpleVO
 *
 * @AUTHOR: roydon
 * @DATE: 2025/6/2
 **/
@Data
public class ChatModelSimpleVO {
    /**
     * 编号
     */
    private Long id;
    /**
     * 模型名字
     */
    private String name;
    /**
     * 模型标识
     */
    private String model;
    /**
     * 模型平台
     * 枚举 {@link AiPlatformEnum}
     */
    private String platform;
    /**
     * 模型类型
     * 枚举 {@link AiModelTypeEnum}
     */
    private String type;
}
