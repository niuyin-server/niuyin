package com.niuyin.service.ai.controller.web.model;

import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.model.ai.domain.model.ModelAgentDO;
import com.niuyin.model.ai.dto.model.web.WebModelRolePageDTO;
import com.niuyin.service.ai.service.IModelRoleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ModelRoleController
 *
 * @AUTHOR: roydon
 * @DATE: 2025/6/2
 **/
@RestController
@RequestMapping("v1/role")
public class ModelRoleController {

    @Resource
    private IModelRoleService modelRoleService;

    @GetMapping("/page")
    @Operation(summary = "获得智能体分页")
    public R<PageData<ModelAgentDO>> getChatRolePage(@Valid WebModelRolePageDTO pageDTO) {
        PageData<ModelAgentDO> pageResult = modelRoleService.getModelRolePageForWeb(pageDTO);
        return R.ok(pageResult);
    }

}
