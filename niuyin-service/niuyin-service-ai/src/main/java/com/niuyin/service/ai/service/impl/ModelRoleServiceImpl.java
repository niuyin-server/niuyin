package com.niuyin.service.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.ai.domain.model.ModelRoleDO;
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
 * AI 聊天角色表(ModelRole)表服务实现类
 *
 * @author roydon
 * @since 2025-06-02 15:30:43
 */
@Service
public class ModelRoleServiceImpl extends ServiceImpl<ModelRoleMapper, ModelRoleDO> implements IModelRoleService {
    @Resource
    private ModelRoleMapper modelRoleMapper;

    @Resource
    private SnowFlake snowFlake;

    @Override
    public Long createModelRole(ModelRoleSaveDTO dto) {
        // todo 校验模型是否可用
        ModelRoleDO modelRole = BeanCopyUtils.copyBean(dto, ModelRoleDO.class);
        modelRole.setId(snowFlake.nextId());
        modelRole.setPublicFlag(TrueOrFalseEnum.TRUE.getCode());
        modelRoleMapper.insert(modelRole);
        return modelRole.getId();
    }

    @Override
    public void updateModelRole(ModelRoleSaveDTO dto) {
        // todo 校验模型是否可用
        ModelRoleDO modelRoleDO = BeanCopyUtils.copyBean(dto, ModelRoleDO.class);
        modelRoleMapper.updateById(modelRoleDO);
    }

    @Override
    public void deleteModelRole(Long id) {
        modelRoleMapper.deleteById(id);
    }

    @Override
    public ModelRoleDO getModelRole(Long id) {
        return modelRoleMapper.selectById(id);
    }

    @Override
    public PageData<ModelRoleDO> getModelRolePage(ModelRolePageDTO pageDTO) {
        LambdaQueryWrapper<ModelRoleDO> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(pageDTO.getName()), ModelRoleDO::getName, pageDTO.getName())
                .like(StringUtils.isNotBlank(pageDTO.getCategory()), ModelRoleDO::getCategory, pageDTO.getCategory())
                .eq(StringUtils.isNotBlank(pageDTO.getPublicFlag()), ModelRoleDO::getPublicFlag, pageDTO.getPublicFlag())
                .eq(StringUtils.isNotBlank(pageDTO.getStateFlag()), ModelRoleDO::getStateFlag, pageDTO.getStateFlag())
                .orderByAsc(ModelRoleDO::getSort);
        Page<ModelRoleDO> page = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), qw);
        return PageData.page(page);
    }

    @Override
    public void updateModelRoleState(AdminModelRoleController.ModelRoleStateDTO dto) {
        ModelRoleDO modelRoleDO = BeanCopyUtils.copyBean(dto, ModelRoleDO.class);
        modelRoleMapper.updateById(modelRoleDO);
    }

    @Override
    public PageData<ModelRoleDO> getModelRolePageForWeb(WebModelRolePageDTO pageDTO) {
        LambdaQueryWrapper<ModelRoleDO> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(pageDTO.getName()), ModelRoleDO::getName, pageDTO.getName())
                .like(StringUtils.isNotBlank(pageDTO.getCategory()), ModelRoleDO::getCategory, pageDTO.getCategory())
                .eq(ModelRoleDO::getPublicFlag, TrueOrFalseEnum.TRUE.getCode())
                .eq(ModelRoleDO::getStateFlag, StateFlagEnum.ENABLE.getCode())
                .orderByAsc(ModelRoleDO::getSort);
        Page<ModelRoleDO> page = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), qw);
        return PageData.page(page);
    }
}
