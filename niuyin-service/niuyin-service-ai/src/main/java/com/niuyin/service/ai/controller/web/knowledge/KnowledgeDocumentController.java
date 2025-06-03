package com.niuyin.service.ai.controller.web.knowledge;

import com.niuyin.service.ai.service.IKnowledgeDocumentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 知识库文档表(KnowledgeDocument)表控制层
 *
 * @author roydon
 * @since 2025-06-03 22:03:43
 */
@RestController
@RequestMapping("v1/knowledge/document")
public class KnowledgeDocumentController {

    @Resource
    private IKnowledgeDocumentService knowledgeDocumentService;


}

