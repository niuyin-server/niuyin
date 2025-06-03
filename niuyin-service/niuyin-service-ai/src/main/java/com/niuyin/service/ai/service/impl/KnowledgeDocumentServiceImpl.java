package com.niuyin.service.ai.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.model.ai.domain.knowledge.KnowledgeDocumentDO;
import com.niuyin.service.ai.mapper.KnowledgeDocumentMapper;
import com.niuyin.service.ai.service.IKnowledgeDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * AI 知识库文档表(KnowledgeDocument)表服务实现类
 *
 * @author roydon
 * @since 2025-06-03 22:03:43
 */
@RequiredArgsConstructor
@Service
public class KnowledgeDocumentServiceImpl extends ServiceImpl<KnowledgeDocumentMapper, KnowledgeDocumentDO> implements IKnowledgeDocumentService {
    private final KnowledgeDocumentMapper knowledgeDocumentMapper;

    /**
     * 获取文档列表
     *
     * @param ids 文档编号列表
     * @return 文档列表
     */
    @Override
    public List<KnowledgeDocumentDO> getKnowledgeDocumentList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return knowledgeDocumentMapper.selectBatchIds(ids);
    }
}
