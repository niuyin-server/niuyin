package com.niuyin.service.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.ai.AiManagerDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI管理员表(AiManager)表数据库访问层
 *
 * @author roydon
 * @since 2025-05-30 23:39:16
 */
@Mapper
public interface ManagerMapper extends BaseMapper<AiManagerDO>{

}

