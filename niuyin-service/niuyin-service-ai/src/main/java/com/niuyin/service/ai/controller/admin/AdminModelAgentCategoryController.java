package com.niuyin.service.ai.controller.admin;

import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.model.ai.domain.model.ModelAgentCategoryDO;
import com.niuyin.model.ai.dto.model.ModelAgentCategoryPageDTO;
import com.niuyin.model.ai.vo.model.ModelAgentCategorySimpleVO;
import com.niuyin.service.ai.service.IModelAgentCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.niuyin.common.core.utils.CollectionUtils.convertList;

/**
 * AI 智能体分类表(AiModelAgentCategory)表控制层
 *
 * @author roydon
 * @since 2025-06-13 10:21:49
 */
@Tag(name = "管理后台 - AI 智能体分类")
@RestController
@RequestMapping("v1/agentCategory")
public class AdminModelAgentCategoryController {

    @Resource
    private IModelAgentCategoryService modelAgentCategoryService;

    @PostMapping("/create")
    @Operation(summary = "创建智能体分类")
    public R<Long> createModelAgentCategory(@Valid @RequestBody ModelAgentCategoryDO dto) {
        return R.ok(modelAgentCategoryService.createModelAgentCategory(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "更新智能体分类")
    public R<Boolean> updateModelAgentCategory(@Valid @RequestBody ModelAgentCategoryDO dto) {
        modelAgentCategoryService.updateModelAgentCategory(dto);
        return R.ok(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除智能体分类")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteModelAgentCategory(@RequestParam("id") Long id) {
        modelAgentCategoryService.deleteModelAgentCategory(id);
        return R.ok(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得智能体分类")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public R<ModelAgentCategoryDO> getModelAgentCategory(@RequestParam("id") Long id) {
        ModelAgentCategoryDO chatRole = modelAgentCategoryService.getModelAgentCategory(id);
        return R.ok(chatRole);
    }

    @GetMapping("/page")
    @Operation(summary = "获得智能体分类分页")
    public R<PageData<ModelAgentCategoryDO>> getModelAgentCategoryPage(@Valid ModelAgentCategoryPageDTO pageDTO) {
        PageData<ModelAgentCategoryDO> pageResult = modelAgentCategoryService.getModelAgentCategoryPage(pageDTO);
        return R.ok(pageResult);
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得智能体分类列表")
    public R<List<ModelAgentCategorySimpleVO>> getSimpleList() {
        List<ModelAgentCategoryDO> list = modelAgentCategoryService.list();
        return R.ok(convertList(list, model -> new ModelAgentCategorySimpleVO().setId(model.getId())
                .setName(model.getName())
                .setIcon(model.getIcon())));
    }

}

