package com.niuyin.service.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.ai.domain.model.ModelAgentCategoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * AI 智能体分类表(AiModelAgentCategory)表数据库访问层
 *
 * @author roydon
 * @since 2025-06-13 10:21:49
 */
@Mapper
public interface ModelAgentCategoryMapper extends BaseMapper<ModelAgentCategoryDO> {


}

