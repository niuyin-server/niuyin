package com.niuyin.service.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.domain.vo.PageData;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.ai.domain.model.ApiKeyDO;
import com.niuyin.model.ai.dto.model.ApiKeyPageDTO;
import com.niuyin.model.ai.dto.model.ApiKeySaveDTO;
import com.niuyin.model.ai.dto.model.ApiKeyStateDTO;
import com.niuyin.model.common.enums.StateFlagEnum;
import com.niuyin.service.ai.mapper.ApiKeyMapper;
import com.niuyin.service.ai.service.IApiKeyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * AI API 密钥表(AiApiKey)表服务实现类
 *
 * @author roydon
 * @since 2025-05-31 23:44:53
 */
@Service
public class ApiKeyServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKeyDO> implements IApiKeyService {
    @Resource
    private ApiKeyMapper apiKeyMapper;
    @Resource
    private SnowFlake snowFlake;

    @Override
    public Long createApiKey(ApiKeySaveDTO dto) {
        ApiKeyDO apiKey = BeanCopyUtils.copyBean(dto, ApiKeyDO.class);
        apiKey.setId(snowFlake.nextId());
        apiKeyMapper.insert(apiKey);
        return apiKey.getId();
    }

    @Override
    public void updateApiKey(ApiKeySaveDTO dto) {
        ApiKeyDO apiKey = BeanCopyUtils.copyBean(dto, ApiKeyDO.class);
        apiKeyMapper.updateById(apiKey);
    }

    @Override
    public void deleteApiKey(Long id) {
        apiKeyMapper.deleteById(id);
    }

    @Override
    public ApiKeyDO getApiKey(Long id) {
        return apiKeyMapper.selectById(id);
    }

    @Override
    public PageData<ApiKeyDO> getApiKeyPage(ApiKeyPageDTO pageDTO) {
        LambdaQueryWrapper<ApiKeyDO> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(pageDTO.getName()), ApiKeyDO::getName, pageDTO.getName())
                .eq(StringUtils.isNotBlank(pageDTO.getPlatform()), ApiKeyDO::getPlatform, pageDTO.getPlatform())
                .eq(StringUtils.isNotBlank(pageDTO.getStateFlag()), ApiKeyDO::getStateFlag, pageDTO.getStateFlag());
        Page<ApiKeyDO> page = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), qw);
        return PageData.page(page);
    }

    @Override
    public void updateApiKeyState(ApiKeyStateDTO dto) {
        ApiKeyDO apiKey = BeanCopyUtils.copyBean(dto, ApiKeyDO.class);
        apiKeyMapper.updateById(apiKey);
    }

    @Override
    public ApiKeyDO validateApiKey(Long id) {
        ApiKeyDO apiKey = getApiKey(id);
        if (StateFlagEnum.isDisable(apiKey.getStateFlag())) {
            throw new RuntimeException("密钥已被禁用");
        }
        return apiKey;
    }
}
