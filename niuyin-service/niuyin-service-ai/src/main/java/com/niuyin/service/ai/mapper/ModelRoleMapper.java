package com.niuyin.service.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.ai.domain.model.ModelRoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * AI 聊天角色表(ModelRole)表数据库访问层
 *
 * @author roydon
 * @since 2025-06-02 15:30:42
 */
@Mapper
public interface ModelRoleMapper extends BaseMapper<ModelRoleDO> {


}

