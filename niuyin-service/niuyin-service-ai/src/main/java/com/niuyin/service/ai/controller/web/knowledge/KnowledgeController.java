package com.niuyin.service.ai.controller.web.knowledge;

import com.niuyin.service.ai.service.IKnowledgeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 知识库表(Knowledge)表控制层
 *
 * @author roydon
 * @since 2025-06-03 22:03:24
 */
@RestController
@RequestMapping("v1/knowledge")
public class KnowledgeController {

    @Resource
    private IKnowledgeService knowledgeService;


}

