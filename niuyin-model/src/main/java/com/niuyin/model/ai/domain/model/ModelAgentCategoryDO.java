package com.niuyin.model.ai.domain.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.niuyin.model.common.BaseDO;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * AI 智能体分类表(AiModelAgentCategory)实体类
 *
 * @author roydon
 * @since 2025-06-13 10:21:49
 */
@Data
@TableName("ai_model_agent_category")
public class ModelAgentCategoryDO extends BaseDO {
    @Serial
    private static final long serialVersionUID = -26433566383406754L;
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
     * 图标
     */
    private String icon;
    /**
     * 角色排序
     */
    private Integer sort;

}

