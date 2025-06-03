package com.niuyin.model.ai.bo;

import lombok.Data;

/**
 * 知识库段落搜索结果
 */
@Data
public class KnowledgeSegmentSearchRespBO {

    /**
     * 段落编号
     */
    private Long id;
    /**
     * 文档编号
     */
    private Long documentId;
    /**
     * 知识库编号
     */
    private Long knowledgeId;

    /**
     * 内容
     */
    private String content;
    /**
     * 内容长度
     */
    private Integer contentLength;

    /**
     * Token 数量
     */
    private Integer tokens;

    /**
     * 相似度分数
     */
    private Double score;

}
