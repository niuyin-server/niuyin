package com.niuyin.service.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.ai.image.domain.ImageDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI文生图表(AiImage)表数据库访问层
 *
 * @author roydon
 * @since 2025-05-06 15:48:46
 */
@Mapper
public interface ImageMapper extends BaseMapper<ImageDO> {

}

