package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.ai.bo.KnowledgeSegmentSearchReqBO;
import com.niuyin.model.ai.bo.KnowledgeSegmentSearchRespBO;
import com.niuyin.model.ai.domain.knowledge.KnowledgeSegmentDO;

import java.util.List;

/**
 * AI 知识库分段表(KnowledgeSegment)表服务接口
 *
 * @author roydon
 * @since 2025-06-03 22:03:58
 */
public interface IKnowledgeSegmentService extends IService<KnowledgeSegmentDO> {

    /**
     * 搜索知识库分段
     */
    List<KnowledgeSegmentSearchRespBO> searchKnowledgeSegment(KnowledgeSegmentSearchReqBO bo);
}
