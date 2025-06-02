package com.niuyin.service.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.ai.ApiKeyDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI API 密钥表(AiApiKey)表数据库访问层
 *
 * @author roydon
 * @since 2025-05-31 23:44:52
 */
@Mapper
public interface ApiKeyMapper extends BaseMapper<ApiKeyDO> {


}

