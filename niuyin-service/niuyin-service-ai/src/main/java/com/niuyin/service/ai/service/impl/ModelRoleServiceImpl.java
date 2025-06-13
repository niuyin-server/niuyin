package com.niuyin.service.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.ai.domain.model.ModelAgentDO;
import com.niuyin.model.ai.dto.model.ModelRolePageDTO;
import com.niuyin.model.ai.dto.model.ModelRoleSaveDTO;
import com.niuyin.model.ai.dto.model.web.WebModelRolePageDTO;
import com.niuyin.model.common.enums.StateFlagEnum;
import com.niuyin.model.common.enums.TrueOrFalseEnum;
import com.niuyin.service.ai.controller.admin.AdminModelRoleController;
import com.niuyin.service.ai.mapper.ModelRoleMapper;
import com.niuyin.service.ai.service.IModelRoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * AI 智能体表(ModelRole)表服务实现类
 *
 * @author roydon
 * @since 2025-06-02 15:30:43
 */
@Service
public class ModelRoleServiceImpl extends ServiceImpl<ModelRoleMapper, ModelAgentDO> implements IModelRoleService {
    @Resource
    private ModelRoleMapper modelRoleMapper;

    @Resource
    private SnowFlake snowFlake;

    @Override
    public Long createModelRole(ModelRoleSaveDTO dto) {
        // todo 校验模型是否可用
        ModelAgentDO modelRole = BeanCopyUtils.copyBean(dto, ModelAgentDO.class);
        modelRole.setId(snowFlake.nextId());
        modelRole.setPublicFlag(TrueOrFalseEnum.TRUE.getCode());
        modelRoleMapper.insert(modelRole);
        return modelRole.getId();
    }

    @Override
    public void updateModelRole(ModelRoleSaveDTO dto) {
        // todo 校验模型是否可用
        ModelAgentDO modelAgentDO = BeanCopyUtils.copyBean(dto, ModelAgentDO.class);
        modelRoleMapper.updateById(modelAgentDO);
    }

    @Override
    public void deleteModelRole(Long id) {
        modelRoleMapper.deleteById(id);
    }

    @Override
    public ModelAgentDO getModelRole(Long id) {
        return modelRoleMapper.selectById(id);
    }

    @Override
    public PageData<ModelAgentDO> getModelRolePage(ModelRolePageDTO pageDTO) {
        LambdaQueryWrapper<ModelAgentDO> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(pageDTO.getName()), ModelAgentDO::getName, pageDTO.getName())
                .like(StringUtils.isNotBlank(pageDTO.getCategory()), ModelAgentDO::getCategoryIds, pageDTO.getCategory())
                .eq(StringUtils.isNotBlank(pageDTO.getPublicFlag()), ModelAgentDO::getPublicFlag, pageDTO.getPublicFlag())
                .eq(StringUtils.isNotBlank(pageDTO.getStateFlag()), ModelAgentDO::getStateFlag, pageDTO.getStateFlag())
                .orderByAsc(ModelAgentDO::getSort);
        Page<ModelAgentDO> page = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), qw);
        return PageData.page(page);
    }

    @Override
    public void updateModelRoleState(AdminModelRoleController.ModelRoleStateDTO dto) {
        ModelAgentDO modelAgentDO = BeanCopyUtils.copyBean(dto, ModelAgentDO.class);
        modelRoleMapper.updateById(modelAgentDO);
    }

    @Override
    public PageData<ModelAgentDO> getModelRolePageForWeb(WebModelRolePageDTO pageDTO) {
        LambdaQueryWrapper<ModelAgentDO> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(pageDTO.getName()), ModelAgentDO::getName, pageDTO.getName())
                .like(StringUtils.isNotBlank(pageDTO.getCategory()), ModelAgentDO::getCategoryIds, pageDTO.getCategory())
                .eq(ModelAgentDO::getPublicFlag, TrueOrFalseEnum.TRUE.getCode())
                .eq(ModelAgentDO::getStateFlag, StateFlagEnum.ENABLE.getCode())
                .orderByAsc(ModelAgentDO::getSort);
        Page<ModelAgentDO> page = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), qw);
        return PageData.page(page);
    }
}
