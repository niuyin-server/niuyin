package com.niuyin.service.ai.controller.admin;

import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.ai.domain.model.ToolDO;
import com.niuyin.model.ai.dto.model.AiToolSaveDTO;
import com.niuyin.model.ai.dto.model.ToolPageDTO;
import com.niuyin.model.ai.vo.model.ToolSimpleVO;
import com.niuyin.model.common.enums.StateFlagEnum;
import com.niuyin.service.ai.service.IToolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.niuyin.common.core.utils.CollectionUtils.convertList;

/**
 * AI 工具表(Tool)表控制层
 *
 * @author makejava
 * @since 2025-06-05 16:02:46
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/tool")
public class AdminToolController {
    private final IToolService toolService;

    @PostMapping("/create")
    @Operation(summary = "创建工具")
    public R<Long> createTool(@Valid @RequestBody AiToolSaveDTO dto) {
        return R.ok(toolService.createTool(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "更新工具")
    public R<Boolean> updateTool(@Valid @RequestBody AiToolSaveDTO dto) {
        toolService.updateTool(dto);
        return R.ok(true);
    }

    @PutMapping("/updateState")
    @Operation(summary = "更新工具状态")
    public R<Boolean> updateToolState(@Valid @RequestBody ToolStateDTO dto) {
        toolService.updateToolState(dto);
        return R.ok(true);
    }

    public record ToolStateDTO(
            @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "23538")
            @NotNull(message = "编号不能为空")
            Long id,

            @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
            @NotNull(message = "状态不能为空")
            String stateFlag
    ) {
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除工具")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteTool(@RequestParam("id") Long id) {
        toolService.deleteTool(id);
        return R.ok(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得工具")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public R<ToolDO> getTool(@RequestParam("id") Long id) {
        ToolDO tool = toolService.getTool(id);
        return R.ok(tool);
    }

    @GetMapping("/page")
    @Operation(summary = "获得工具分页")
    public R<PageDataInfo<ToolDO>> getToolPage(@Valid ToolPageDTO pageDTO) {
        PageDataInfo<ToolDO> pageResult = toolService.getToolPage(pageDTO);
        return R.ok(pageResult);
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得工具列表")
    public R<List<ToolSimpleVO>> getToolSimpleList() {
        List<ToolDO> list = toolService.getToolListByState(StateFlagEnum.ENABLE.getCode());
        return R.ok(convertList(list, tool -> new ToolSimpleVO().setId(tool.getId()).setName(tool.getName())));
    }

}

