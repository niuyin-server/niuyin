package com.niuyin.service.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.ai.enums.AiPlatformEnum;
import com.niuyin.common.ai.factory.AiModelFactory;
import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.ai.domain.model.ApiKeyDO;
import com.niuyin.model.ai.domain.model.ChatModelDO;
import com.niuyin.model.ai.dto.model.AiModelPageDTO;
import com.niuyin.model.ai.dto.model.AiModelSaveDTO;
import com.niuyin.model.ai.dto.model.AiModelStateDTO;
import com.niuyin.model.common.enums.StateFlagEnum;
import com.niuyin.service.ai.mapper.ChatModelMapper;
import com.niuyin.service.ai.service.IApiKeyService;
import com.niuyin.service.ai.service.IChatModelService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.stereotype.Service;

/**
 * AI 聊天模型表(AiChatModel)表服务实现类
 *
 * @author roydon
 * @since 2025-06-02 13:41:28
 */
@Service
public class ChatModelServiceImpl extends ServiceImpl<ChatModelMapper, ChatModelDO> implements IChatModelService {
    @Resource
    private ChatModelMapper chatModelMapper;
    @Resource
    private SnowFlake snowFlake;
    @Resource
    private IApiKeyService apiKeyService;
    @Resource
    private AiModelFactory modelFactory;

    @Override
    public Long createModel(AiModelSaveDTO dto) {
        // 校验apikey
        apiKeyService.validateApiKey(dto.getKeyId());
        ChatModelDO modelDO = BeanCopyUtils.copyBean(dto, ChatModelDO.class);
        modelDO.setId(snowFlake.nextId());
        chatModelMapper.insert(modelDO);
        return modelDO.getId();
    }

    @Override
    public void updateModel(AiModelSaveDTO dto) {
        // 校验apikey
        apiKeyService.validateApiKey(dto.getKeyId());
        ChatModelDO modelDO = BeanCopyUtils.copyBean(dto, ChatModelDO.class);
        chatModelMapper.updateById(modelDO);
    }

    @Override
    public void deleteModel(Long id) {
        chatModelMapper.deleteById(id);
    }

    @Override
    public ChatModelDO getModel(Long id) {
        return chatModelMapper.selectById(id);
    }

    @Override
    public PageDataInfo<ChatModelDO> getModelPage(AiModelPageDTO pageDTO) {
        LambdaQueryWrapper<ChatModelDO> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(pageDTO.getName()), ChatModelDO::getName, pageDTO.getName())
                .like(StringUtils.isNotBlank(pageDTO.getModel()), ChatModelDO::getModel, pageDTO.getModel())
                .eq(StringUtils.isNotBlank(pageDTO.getPlatform()), ChatModelDO::getPlatform, pageDTO.getPlatform())
                .eq(StringUtils.isNotBlank(pageDTO.getType()), ChatModelDO::getType, pageDTO.getType())
                .eq(StringUtils.isNotBlank(pageDTO.getStateFlag()), ChatModelDO::getStateFlag, pageDTO.getStateFlag())
                .orderByAsc(ChatModelDO::getSort);
        Page<ChatModelDO> page = this.page(new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize()), qw);
        return PageDataInfo.page(page);
    }

    private ChatModelDO validateModelExists(Long id) {
        ChatModelDO model = chatModelMapper.selectById(id);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }
        return model;
    }

    @Override
    public ChatModelDO validateModel(Long id) {
        ChatModelDO model = validateModelExists(id);
        if (StateFlagEnum.isDisable(model.getStateFlag())) {
            throw new RuntimeException("模型被禁用");
        }
        return model;
    }

    @Override
    public void updateModelState(AiModelStateDTO dto) {
        ChatModelDO modelDO = BeanCopyUtils.copyBean(dto, ChatModelDO.class);
        chatModelMapper.updateById(modelDO);
    }

    // ========== 与 Spring AI 集成 ==========

    @Override
    public ChatModel getChatModel(Long id) {
        ChatModelDO model = validateModel(id);
        ApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        return modelFactory.getOrCreateChatModel(platform, apiKey.getApiKey(), apiKey.getUrl());
    }

    @Override
    public ImageModel getImageModel(Long id) {
        ChatModelDO model = validateModel(id);
        ApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        return modelFactory.getOrCreateImageModel(platform, apiKey.getApiKey(), apiKey.getUrl());
    }

}
