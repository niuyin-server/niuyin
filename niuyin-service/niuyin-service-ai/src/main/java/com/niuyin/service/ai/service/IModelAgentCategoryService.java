package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.model.ai.domain.model.ModelAgentCategoryDO;
import com.niuyin.model.ai.dto.model.ModelAgentCategoryPageDTO;

/**
 * AI 智能体分类表(AiModelAgentCategory)表服务接口
 *
 * @author roydon
 * @since 2025-06-13 10:21:50
 */
public interface IModelAgentCategoryService extends IService<ModelAgentCategoryDO> {

    PageData<ModelAgentCategoryDO> getModelAgentCategoryPage(ModelAgentCategoryPageDTO pageDTO);

    ModelAgentCategoryDO getModelAgentCategory(Long id);

    Long createModelAgentCategory(ModelAgentCategoryDO dto);

    void updateModelAgentCategory(ModelAgentCategoryDO dto);

    void deleteModelAgentCategory(Long id);
}
