package com.niuyin.model.ai.domain.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.niuyin.model.common.BaseDO;
import lombok.Data;

import java.io.Serial;

/**
 * AI API 密钥表(AiApiKey)实体类
 *
 * @author roydon
 * @since 2025-05-31 23:44:52
 */
@Data
@TableName("ai_api_key")
public class ApiKeyDO extends BaseDO {
    @Serial
    private static final long serialVersionUID = 637950607395494840L;
    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 密钥
     */
    private String apiKey;
    /**
     * 平台
     */
    private String platform;
    /**
     * 自定义 API 地址
     */
    private String url;
    /**
     * 状态[0正常1禁用]
     */
    private String stateFlag;


}

