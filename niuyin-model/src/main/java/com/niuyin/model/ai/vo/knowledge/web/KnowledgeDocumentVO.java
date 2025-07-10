package com.niuyin.model.ai.vo.knowledge.web;

import com.niuyin.model.ai.domain.knowledge.KnowledgeDocumentDO;
import lombok.Data;

@Data
public class KnowledgeDocumentVO extends KnowledgeDocumentDO {

    // 向量化状态
    private String embeddingState;

}

