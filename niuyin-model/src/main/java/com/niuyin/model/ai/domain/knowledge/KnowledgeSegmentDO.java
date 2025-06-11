package com.niuyin.model.ai.domain.knowledge;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.niuyin.model.common.BaseDO;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * AI 知识库分段表(KnowledgeSegment)实体类
 *
 * @author roydon
 * @since 2025-06-03 22:03:58
 */
@Data
@TableName("ai_knowledge_segment")
public class KnowledgeSegmentDO extends BaseDO {
    @Serial
    private static final long serialVersionUID = -80429395507510721L;

    /**
     * 向量库的编号 - 空值
     */
    public static final String VECTOR_ID_EMPTY = "";

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 知识库编号
     */
    private Long knowledgeId;
    /**
     * 文档编号
     */
    private Long documentId;
    /**
     * 分段内容
     */
    private String content;
    /**
     * 字符数
     */
    private Integer contentLength;
    /**
     * 向量库的编号
     */
    private String vectorId;
    /**
     * token 数量
     */
    private Integer tokens;
    /**
     * 召回次数
     */
    private Integer retrievalCount;
    /**
     * 是否启用
     */
    private String stateFlag;


}

