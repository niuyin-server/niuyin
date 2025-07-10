package com.niuyin.model.ai.dto.knowledge.web;

import com.niuyin.model.common.dto.PageDTO;
import lombok.Data;

/**
 * KnowledgeDocumentPageDTO
 *
 * @AUTHOR: roydon
 * @DATE: 2025/7/10
 **/
@Data
public class KnowledgeDocumentPageDTO extends PageDTO {
    /**
     * 知识库id
     */
    private Long knowledgeId;
}
