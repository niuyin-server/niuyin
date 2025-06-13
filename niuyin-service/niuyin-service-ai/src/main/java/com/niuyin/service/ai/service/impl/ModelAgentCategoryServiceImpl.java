package com.niuyin.service.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.model.ai.domain.model.ModelAgentCategoryDO;
import com.niuyin.model.ai.dto.model.ModelAgentCategoryPageDTO;
import com.niuyin.service.ai.mapper.ModelAgentCategoryMapper;
import com.niuyin.service.ai.service.IModelAgentCategoryService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * AI 智能体分类表(AiModelAgentCategory)表服务实现类
 *
 * @author roydon
 * @since 2025-06-13 10:21:50
 */
@Service
public class ModelAgentCategoryServiceImpl extends ServiceImpl<ModelAgentCategoryMapper, ModelAgentCategoryDO> implements IModelAgentCategoryService {
    @Resource
    private ModelAgentCategoryMapper modelAgentCategoryMapper;
    @Resource
    private SnowFlake snowFlake;

    @Override
    public PageData<ModelAgentCategoryDO> getModelAgentCategoryPage(ModelAgentCategoryPageDTO pageDTO) {
        LambdaQueryWrapper<ModelAgentCategoryDO> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(pageDTO.getName()), ModelAgentCategoryDO::getName, pageDTO.getName());
        Page<ModelAgentCategoryDO> page = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), qw);
        return PageData.page(page);
    }

    @Override
    public ModelAgentCategoryDO getModelAgentCategory(Long id) {
        return modelAgentCategoryMapper.selectById(id);
    }

    @Override
    public Long createModelAgentCategory(ModelAgentCategoryDO dto) {
        dto.setId(snowFlake.nextId());
        dto.setCreateBy(UserContext.getUser().getUserName());
        dto.setCreateTime(LocalDateTime.now());
        modelAgentCategoryMapper.insert(dto);
        return dto.getId();
    }

    @Override
    public void updateModelAgentCategory(ModelAgentCategoryDO dto) {
        dto.setUpdateBy(UserContext.getUser().getUserName());
        dto.setUpdateTime(LocalDateTime.now());
        modelAgentCategoryMapper.updateById(dto);
    }

    @Override
    public void deleteModelAgentCategory(Long id) {
        modelAgentCategoryMapper.deleteById(id);
    }
}
