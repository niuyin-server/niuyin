package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.ai.domain.model.ModelRoleDO;
import com.niuyin.model.ai.dto.model.ModelRolePageDTO;
import com.niuyin.model.ai.dto.model.ModelRoleSaveDTO;
import com.niuyin.model.ai.dto.model.web.WebModelRolePageDTO;
import com.niuyin.service.ai.controller.admin.AdminModelRoleController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * AI 聊天角色表(ModelRole)表服务接口
 *
 * @author roydon
 * @since 2025-06-02 15:30:43
 */
public interface IModelRoleService extends IService<ModelRoleDO> {

    Long createModelRole(ModelRoleSaveDTO dto);

    void updateModelRole(ModelRoleSaveDTO dto);

    void deleteModelRole(Long id);

    ModelRoleDO getModelRole(Long id);

    PageDataInfo<ModelRoleDO> getModelRolePage(ModelRolePageDTO pageDTO);

    void updateModelRoleState(AdminModelRoleController.ModelRoleStateDTO dto);

    PageDataInfo<ModelRoleDO> getModelRolePageForWeb(WebModelRolePageDTO pageDTO);
}
