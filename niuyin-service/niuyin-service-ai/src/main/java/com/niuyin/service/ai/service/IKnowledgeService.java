package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.ai.domain.knowledge.KnowledgeDO;
import com.niuyin.model.common.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * AI 知识库表(Knowledge)表服务接口
 *
 * @author roydon
 * @since 2025-06-03 22:03:27
 */
public interface IKnowledgeService extends IService<KnowledgeDO> {

    KnowledgeDO validateKnowledgeExists(Long knowledgeId);

    /**
     * 创建知识库
     */
    Long createKnowledge(KnowledgeDO dto);

    Long editKnowledge(KnowledgeDO dto);

    /**
     * 获取知识库分页列表
     */
    PageDataInfo<KnowledgeDO> knowledgeList(PageDTO pageDTO);
}
