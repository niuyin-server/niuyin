package com.niuyin.service.ai.controller.admin;

import com.niuyin.common.ai.enums.AiPlatformEnum;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.ai.domain.model.ApiKeyDO;
import com.niuyin.model.ai.dto.model.ApiKeyPageDTO;
import com.niuyin.model.ai.dto.model.ApiKeySaveDTO;
import com.niuyin.model.ai.dto.model.ApiKeyStateDTO;
import com.niuyin.model.ai.vo.model.ApiKeySimpleVO;
import com.niuyin.model.common.vo.DictVO;
import com.niuyin.service.ai.service.IApiKeyService;
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
 * AI API 密钥表(AiApiKey)表控制层
 *
 * @author roydon
 * @since 2025-05-31 23:44:51
 */
@Tag(name = "管理后台 - AI API 密钥")
@RestController
@RequestMapping("v1/api-key")
public class AdminApiKeyController {

    @Resource
    private IApiKeyService apiKeyService;

    @PostMapping("/create")
    @Operation(summary = "创建 API 密钥")
    public R<Long> createApiKey(@Valid @RequestBody ApiKeySaveDTO dto) {
        return R.ok(apiKeyService.createApiKey(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 API 密钥")
    public R<Boolean> updateApiKey(@Valid @RequestBody ApiKeySaveDTO dto) {
        apiKeyService.updateApiKey(dto);
        return R.ok(true);
    }

    @PutMapping("/updateState")
    @Operation(summary = "更新 API 密钥状态")
    public R<Boolean> updateApiKey(@Valid @RequestBody ApiKeyStateDTO dto) {
        apiKeyService.updateApiKeyState(dto);
        return R.ok(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 API 密钥")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteApiKey(@RequestParam("id") Long id) {
        apiKeyService.deleteApiKey(id);
        return R.ok(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得 API 密钥")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public R<ApiKeyDO> getApiKey(@RequestParam("id") Long id) {
        ApiKeyDO apiKey = apiKeyService.getApiKey(id);
        return R.ok(apiKey);
    }

    @GetMapping("/page")
    @Operation(summary = "获得 API 密钥分页")
    public R<PageDataInfo<ApiKeyDO>> getApiKeyPage(@Valid ApiKeyPageDTO pageDTO) {
        PageDataInfo<ApiKeyDO> pageResult = apiKeyService.getApiKeyPage(pageDTO);
        return R.ok(pageResult);
    }

    @GetMapping("/platform-list")
    @Operation(summary = "获得 AI 平台列表")
    public R<List<DictVO>> getPlatformList() {
        List<DictVO> res = Arrays.stream(AiPlatformEnum.values()).map(platformEnum -> new DictVO().setLabel(platformEnum.getName()).setValue(platformEnum.getPlatform())).toList();
        return R.ok(res);
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得 API 密钥分页列表")
    public R<List<ApiKeySimpleVO>> getApiKeySimpleList() {
        List<ApiKeyDO> list = apiKeyService.list();
        return R.ok(convertList(list, key -> new ApiKeySimpleVO().setId(key.getId()).setName(key.getName()).setPlatform(key.getPlatform())));
    }
}

