package com.niuyin.service.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.ai.domain.knowledge.KnowledgeDO;
import com.niuyin.service.ai.mapper.KnowledgeMapper;
import com.niuyin.service.ai.service.IKnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * AI 知识库表(Knowledge)表服务实现类
 *
 * @author roydon
 * @since 2025-06-03 22:03:27
 */
@RequiredArgsConstructor
@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, KnowledgeDO> implements IKnowledgeService {
    private final KnowledgeMapper knowledgeMapper;

    @Override
    public KnowledgeDO validateKnowledgeExists(Long knowledgeId) {
        KnowledgeDO knowledgeDO = this.getById(knowledgeId);
        if (knowledgeDO == null) {
            throw new RuntimeException("知识库不存在");
        }
        return knowledgeDO;
    }
}
