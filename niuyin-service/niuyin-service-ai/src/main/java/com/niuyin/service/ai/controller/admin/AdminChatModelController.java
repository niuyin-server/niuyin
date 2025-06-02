package com.niuyin.service.ai.controller.admin;

import com.niuyin.common.ai.enums.AiModelTypeEnum;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.ai.domain.model.ChatModelDO;
import com.niuyin.model.ai.dto.model.AiModelPageDTO;
import com.niuyin.model.ai.dto.model.AiModelSaveDTO;
import com.niuyin.model.ai.dto.model.AiModelStateDTO;
import com.niuyin.model.ai.vo.model.ChatModelSimpleVO;
import com.niuyin.model.common.enums.StateFlagEnum;
import com.niuyin.model.common.vo.DictVO;
import com.niuyin.service.ai.service.IChatModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static com.niuyin.common.core.utils.CollectionUtils.convertList;


/**
 * AI 聊天模型表(AiChatModel)表控制层
 *
 * @author roydon
 * @since 2025-06-02 13:41:26
 */
@Tag(name = "管理后台 - AI 模型")
@RestController
@RequestMapping("v1/model")
public class AdminChatModelController {

    @Resource
    private IChatModelService chatModelService;

    @PostMapping("/create")
    @Operation(summary = "创建模型")
    public R<Long> createModel(@Valid @RequestBody AiModelSaveDTO dto) {
        return R.ok(chatModelService.createModel(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "更新模型")
    public R<Boolean> updateModel(@Valid @RequestBody AiModelSaveDTO dto) {
        chatModelService.updateModel(dto);
        return R.ok(true);
    }

    @PutMapping("/updateState")
    @Operation(summary = "更新模型状态")
    public R<Boolean> updateApiKey(@Valid @RequestBody AiModelStateDTO dto) {
        chatModelService.updateModelState(dto);
        return R.ok(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除模型")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteModel(@RequestParam("id") Long id) {
        chatModelService.deleteModel(id);
        return R.ok(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得模型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public R<ChatModelDO> getModel(@RequestParam("id") Long id) {
        ChatModelDO model = chatModelService.getModel(id);
        return R.ok(model);
    }

    @GetMapping("/page")
    @Operation(summary = "获得模型分页")
    public R<PageDataInfo<ChatModelDO>> getModelPage(@Valid AiModelPageDTO pageDTO) {
        PageDataInfo<ChatModelDO> pageResult = chatModelService.getModelPage(pageDTO);
        return R.ok(pageResult);
    }

    @GetMapping("/type-list")
    @Operation(summary = "获得 AI 模型类型列表")
    public R<List<DictVO>> getModelTypeList() {
        List<DictVO> res = Arrays.stream(AiModelTypeEnum.values()).map(platformEnum -> new DictVO().setLabel(platformEnum.getName()).setValue(platformEnum.getType())).toList();
        return R.ok(res);
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得模型列表")
    @Parameter(name = "type", description = "类型", example = "1")
    @Parameter(name = "platform", description = "平台", example = "midjourney")
    public R<List<ChatModelSimpleVO>> getModelSimpleList(ModelSimpleListDTO dto) {
        List<ChatModelDO> list = chatModelService.getModelListByStateAndTypeAndPlatform(StateFlagEnum.ENABLE.getCode(), dto.type, dto.platform);
        return R.ok(convertList(list, model -> new ChatModelSimpleVO().setId(model.getId())
                .setName(model.getName())
                .setModel(model.getModel())
                .setPlatform(model.getPlatform())
                .setType(model.getType())));
    }

    public record ModelSimpleListDTO(String type, String platform) {
    }


}

