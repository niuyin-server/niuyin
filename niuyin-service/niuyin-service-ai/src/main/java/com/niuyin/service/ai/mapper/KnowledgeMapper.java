package com.niuyin.service.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.ai.domain.knowledge.KnowledgeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * AI 知识库表(Knowledge)表数据库访问层
 *
 * @author roydon
 * @since 2025-06-03 22:03:24
 */
@Mapper
public interface KnowledgeMapper extends BaseMapper<KnowledgeDO>{



}

