package com.niuyin.service.ai.controller.web.knowledge;

import com.niuyin.service.ai.service.IKnowledgeSegmentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 知识库分段表(KnowledgeSegment)表控制层
 *
 * @author roydon
 * @since 2025-06-03 22:03:58
 */
@RestController
@RequestMapping("v1/knowledge/segment")
public class KnowledgeSegmentController {

    @Resource
    private IKnowledgeSegmentService knowledgeSegmentService;


}

