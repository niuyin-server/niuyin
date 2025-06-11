package com.niuyin.model.ai.dto.model;

import com.niuyin.model.common.dto.PageDTO;
import lombok.Data;

/**
 * AiModelPageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2025/6/2
 **/
@Data
public class AiModelPageDTO extends PageDTO {
    /**
     * 模型名字
     */
    private String name;
    /**
     * 模型标识
     */
    private String model;
    private String keyId;
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
    /**
     * 状态[0正常1禁用]
     */
    private String stateFlag;
}

