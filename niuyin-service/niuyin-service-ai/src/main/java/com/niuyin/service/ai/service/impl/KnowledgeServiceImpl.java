package com.niuyin.service.ai.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.ai.domain.knowledge.KnowledgeDO;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.common.enums.TrueOrFalseEnum;
import com.niuyin.service.ai.mapper.KnowledgeMapper;
import com.niuyin.service.ai.service.IKnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    private final SnowFlake snowFlake;

    @Override
    public KnowledgeDO validateKnowledgeExists(Long knowledgeId) {
        KnowledgeDO knowledgeDO = this.getById(knowledgeId);
        if (knowledgeDO == null) {
            throw new RuntimeException("知识库不存在");
        }
        return knowledgeDO;
    }

    /**
     * 创建知识库
     */
    @Override
    public Long createKnowledge(KnowledgeDO dto) {
        dto.setId(snowFlake.nextId());
        dto.setUserId(UserContext.getUserId());
        dto.setCreateBy(UserContext.getUser().getUserName());
        dto.setCreateTime(LocalDateTime.now());
        knowledgeMapper.insert(dto);
        return dto.getId();
    }

    @Override
    public Long editKnowledge(KnowledgeDO dto) {
        dto.setUpdateBy(UserContext.getUser().getUserName());
        dto.setUpdateTime(LocalDateTime.now());
        knowledgeMapper.updateById(dto);
        return dto.getId();
    }

    /**
     * 获取知识库分页列表
     *
     */
    @Override
    public PageDataInfo<KnowledgeDO> knowledgeList(PageDTO pageDTO) {
        Wrappers.<KnowledgeDO>lambdaQuery()
                .eq(KnowledgeDO::getUserId, UserContext.getUserId());
        Page<KnowledgeDO> page = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()));
        return PageDataInfo.page(page);
    }
}
