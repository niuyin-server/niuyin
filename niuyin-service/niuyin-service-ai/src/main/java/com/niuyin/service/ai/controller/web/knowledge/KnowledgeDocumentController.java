package com.niuyin.service.ai.controller.web.knowledge;

import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.common.core.utils.bean.BeanUtils;
import com.niuyin.model.ai.domain.knowledge.KnowledgeDocumentDO;
import com.niuyin.model.ai.dto.knowledge.web.KnowledgeDocumentCreateDTO;
import com.niuyin.model.ai.dto.knowledge.web.KnowledgeDocumentPageDTO;
import com.niuyin.model.ai.vo.knowledge.web.KnowledgeDocumentVO;
import com.niuyin.service.ai.service.IKnowledgeDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/list")
    @Operation(summary = "获取文档分页")
    public R<PageData<KnowledgeDocumentVO>> getKnowledgeDocumentPage(@Valid KnowledgeDocumentPageDTO dto) {
        return R.ok(knowledgeDocumentService.getKnowledgeDocumentPage(dto));
    }

    @PostMapping("/create")
    @Operation(summary = "新建文档（单个）")
    public R<Long> createKnowledgeDocument(@Valid @RequestBody KnowledgeDocumentCreateDTO dto) {
        Long id = knowledgeDocumentService.createKnowledgeDocument(dto);
        return R.ok(id);
    }

    @Operation(summary = "上传文档")
    @PostMapping("/upload")
    public R<String> uploadKnowledgeDocument(@RequestParam("knowledgeId")Long knowledgeId,
                                             @RequestParam("segmentMaxTokens") Integer segmentMaxTokens,
                                             @RequestParam("file") MultipartFile file) {
        return R.ok(knowledgeDocumentService.uploadKnowledgeDocument(  knowledgeId,segmentMaxTokens,file));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文档")
    public R<Boolean> deleteKnowledgeDocument(@RequestParam("id") Long id) {
        return R.ok(knowledgeDocumentService.removeKnowledgeDocumentById(id));
    }

}

