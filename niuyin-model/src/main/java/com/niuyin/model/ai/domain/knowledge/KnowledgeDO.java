package com.niuyin.model.ai.domain.knowledge;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.niuyin.model.common.BaseDO;
import lombok.Data;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * AI 知识库表(Knowledge)实体类
 *
 * @author roydon
 * @since 2025-06-03 22:03:25
 */
@Data
@TableName("ai_knowledge")
public class KnowledgeDO extends BaseDO {
    @Serial
    private static final long serialVersionUID = 341998039512368993L;
    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 知识库名称
     */
    private String name;
    /**
     * 知识库描述
     */
    private String description;
    /**
     * 向量模型编号
     */
    private Long embeddingModelId;
    /**
     * 向量模型标识
     */
    private String embeddingModel;
    /**
     * topK
     */
    private Integer topK;
    /**
     * 相似度阈值
     */
    private BigDecimal similarityThreshold;
    /**
     * 是否启用
     */
    private String stateFlag;


}

