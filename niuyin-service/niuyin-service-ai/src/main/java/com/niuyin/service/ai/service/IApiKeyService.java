package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.ai.domain.model.ApiKeyDO;
import com.niuyin.model.ai.dto.model.ApiKeyPageDTO;
import com.niuyin.model.ai.dto.model.ApiKeySaveDTO;
import com.niuyin.model.ai.dto.model.ApiKeyStateDTO;

/**
 * AI API 密钥表(AiApiKey)表服务接口
 *
 * @author roydon
 * @since 2025-05-31 23:44:53
 */
public interface IApiKeyService extends IService<ApiKeyDO> {

    Long createApiKey(ApiKeySaveDTO dto);

    void updateApiKey(ApiKeySaveDTO dto);

    void deleteApiKey(Long id);

    ApiKeyDO getApiKey(Long id);

    PageDataInfo<ApiKeyDO> getApiKeyPage(ApiKeyPageDTO pageDTO);

    void updateApiKeyState(ApiKeyStateDTO dto);

    ApiKeyDO validateApiKey(Long id);
}
