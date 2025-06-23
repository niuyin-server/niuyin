package com.niuyin.service.ai.controller.web.knowledge;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.model.ai.domain.knowledge.KnowledgeDO;
import com.niuyin.model.ai.vo.knowledge.web.KnowledgeSimpleVO;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.common.enums.StateFlagEnum;
import com.niuyin.service.ai.service.IKnowledgeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public R<PageData<KnowledgeDO>> list(PageDTO pageDTO) {
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

    /**
     * 新增知识库
     */
    @GetMapping("/simple-list")
    public R<List<KnowledgeSimpleVO>> simpleList() {
        List<KnowledgeDO> knowledgeDOS = knowledgeService.list(Wrappers.<KnowledgeDO>lambdaQuery().eq(KnowledgeDO::getUserId, UserContext.getUserId()).eq(KnowledgeDO::getStateFlag, StateFlagEnum.ENABLE.getCode()));
        return R.ok(BeanCopyUtils.copyBeanList(knowledgeDOS, KnowledgeSimpleVO.class));
    }

}

