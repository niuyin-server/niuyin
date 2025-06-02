package com.niuyin.service.ai.controller.admin;

import cn.hutool.core.util.ObjUtil;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.ai.domain.model.ModelRoleDO;
import com.niuyin.model.ai.dto.model.AiModelStateDTO;
import com.niuyin.model.ai.dto.model.ModelRolePageDTO;
import com.niuyin.model.ai.dto.model.ModelRoleSaveDTO;
import com.niuyin.service.ai.service.IModelRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 聊天角色表(ModelRole)表控制层
 *
 * @author roydon
 * @since 2025-06-02 15:30:42
 */
@RestController
@RequestMapping("v1/role")
public class AdminModelRoleController {

    @Resource
    private IModelRoleService modelRoleService;

//    @GetMapping("/my-page")
//    @Operation(summary = "获得【我的】聊天角色分页")
//    public CommonResult<PageResult<AiChatRoleRespVO>> getChatRoleMyPage(@Valid AiChatRolePageReqVO pageReqVO) {
//        PageResult<AiChatRoleDO> pageResult = chatRoleService.getChatRoleMyPage(pageReqVO, getLoginUserId());
//        return success(BeanUtils.toBean(pageResult, AiChatRoleRespVO.class));
//    }
//
//    @GetMapping("/get-my")
//    @Operation(summary = "获得【我的】聊天角色")
//    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    public CommonResult<AiChatRoleRespVO> getChatRoleMy(@RequestParam("id") Long id) {
//        AiChatRoleDO chatRole = chatRoleService.getChatRole(id);
//        if (ObjUtil.notEqual(chatRole.getUserId(), getLoginUserId())) {
//            return success(null);
//        }
//        return success(BeanUtils.toBean(chatRole, AiChatRoleRespVO.class));
//    }
//
//    @PostMapping("/create-my")
//    @Operation(summary = "创建【我的】聊天角色")
//    public CommonResult<Long> createChatRoleMy(@Valid @RequestBody AiChatRoleSaveMyReqVO createReqVO) {
//        return success(chatRoleService.createChatRoleMy(createReqVO, getLoginUserId()));
//    }
//
//    @PutMapping("/update-my")
//    @Operation(summary = "更新【我的】聊天角色")
//    public CommonResult<Boolean> updateChatRoleMy(@Valid @RequestBody AiChatRoleSaveMyReqVO updateReqVO) {
//        chatRoleService.updateChatRoleMy(updateReqVO, getLoginUserId());
//        return success(true);
//    }
//
//    @DeleteMapping("/delete-my")
//    @Operation(summary = "删除【我的】聊天角色")
//    @Parameter(name = "id", description = "编号", required = true)
//    public CommonResult<Boolean> deleteChatRoleMy(@RequestParam("id") Long id) {
//        chatRoleService.deleteChatRoleMy(id, getLoginUserId());
//        return success(true);
//    }
//
//    @GetMapping("/category-list")
//    @Operation(summary = "获得聊天角色的分类列表")
//    public CommonResult<List<String>> getChatRoleCategoryList() {
//        return success(chatRoleService.getChatRoleCategoryList());
//    }

    // ========== 角色管理 ==========

    @PostMapping("/create")
    @Operation(summary = "创建聊天角色")
    public R<Long> createModelRole(@Valid @RequestBody ModelRoleSaveDTO dto) {
        return R.ok(modelRoleService.createModelRole(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "更新聊天角色")
    public R<Boolean> updateModelRole(@Valid @RequestBody ModelRoleSaveDTO dto) {
        modelRoleService.updateModelRole(dto);
        return R.ok(true);
    }

    @PutMapping("/updateState")
    @Operation(summary = "更新模型状态")
    public R<Boolean> updateApiKey(@Valid @RequestBody ModelRoleStateDTO dto) {
        modelRoleService.updateModelRoleState(dto);
        return R.ok(true);
    }

    public record ModelRoleStateDTO(
            @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "23538")
            @NotNull(message = "编号不能为空")
            Long id,

            @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
            @NotNull(message = "状态不能为空")
            String stateFlag
    ) {
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除聊天角色")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteModelRole(@RequestParam("id") Long id) {
        modelRoleService.deleteModelRole(id);
        return R.ok(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得聊天角色")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public R<ModelRoleDO> getModelRole(@RequestParam("id") Long id) {
        ModelRoleDO chatRole = modelRoleService.getModelRole(id);
        return R.ok(chatRole);
    }

    @GetMapping("/page")
    @Operation(summary = "获得聊天角色分页")
    public R<PageDataInfo<ModelRoleDO>> getChatRolePage(@Valid ModelRolePageDTO pageDTO) {
        PageDataInfo<ModelRoleDO> pageResult = modelRoleService.getModelRolePage(pageDTO);
        return R.ok(pageResult);
    }


}

