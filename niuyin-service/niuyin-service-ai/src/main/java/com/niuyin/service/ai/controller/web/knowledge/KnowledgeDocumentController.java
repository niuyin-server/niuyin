package com.niuyin.service.ai.controller.web.knowledge;

import com.niuyin.common.core.domain.R;
import com.niuyin.model.ai.dto.knowledge.web.KnowledgeDocumentCreateDTO;
import com.niuyin.service.ai.service.IKnowledgeDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/create")
    @Operation(summary = "新建文档（单个）")
    public R<Long> createKnowledgeDocument(@Valid @RequestBody KnowledgeDocumentCreateDTO dto) {
        Long id = knowledgeDocumentService.createKnowledgeDocument(dto);
        return R.ok(id);
    }

}

