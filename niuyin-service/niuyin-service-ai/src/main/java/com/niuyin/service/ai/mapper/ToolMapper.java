package com.niuyin.service.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.ai.domain.model.ToolDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 工具表(Tool)表数据库访问层
 *
 * @author roydon
 * @since 2025-06-05 16:02:46
 */
@Mapper
public interface ToolMapper extends BaseMapper<ToolDO>{

}

