package com.niuyin.service.ai.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.ai.domain.knowledge.KnowledgeSegmentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * AI 知识库分段表(KnowledgeSegment)表数据库访问层
 *
 * @author roydon
 * @since 2025-06-03 22:03:58
 */
@Mapper
public interface KnowledgeSegmentMapper extends BaseMapper<KnowledgeSegmentDO> {

    /**
     * 批量更新检索次数
     *
     * @param ids
     */
    default void updateRetrievalCountIncrByIds(List<Long> ids) {
        update(new LambdaUpdateWrapper<KnowledgeSegmentDO>()
                .setSql(" retrieval_count = retrieval_count + 1")
                .in(KnowledgeSegmentDO::getId, ids));
    }
}

