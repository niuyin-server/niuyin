package com.niuyin.service.ai.controller.web.knowledge;

import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.ai.domain.chat.ChatConversationDO;
import com.niuyin.model.ai.domain.knowledge.KnowledgeDO;
import com.niuyin.model.ai.dto.model.web.ChatConversationSaveDTO;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.service.ai.service.IKnowledgeService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 新增知识库
     */
    @GetMapping("/list")
    public R<PageDataInfo<KnowledgeDO>> list(PageDTO pageDTO) {
        return R.ok(knowledgeService.knowledgeList(pageDTO));
    }

    /**
     * 新增知识库
     */
    @PostMapping
    public R<Long> create(@RequestBody KnowledgeDO dto) {
        return R.ok(knowledgeService.createKnowledge(dto));
    }

    /**
     * 编辑知识库
     */
    @PutMapping
    public R<Long> edit(@RequestBody KnowledgeDO dto) {
        return R.ok(knowledgeService.editKnowledge(dto));
    }

}

