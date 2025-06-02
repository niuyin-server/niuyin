package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.ai.domain.model.ChatModelDO;
import com.niuyin.model.ai.dto.model.AiModelPageDTO;
import com.niuyin.model.ai.dto.model.AiModelSaveDTO;
import com.niuyin.model.ai.dto.model.AiModelStateDTO;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * AI 聊天模型表(AiChatModel)表服务接口
 *
 * @author roydon
 * @since 2025-06-02 13:41:28
 */
public interface IChatModelService extends IService<ChatModelDO> {


    Long createModel(AiModelSaveDTO dto);

    void updateModel(AiModelSaveDTO dto);

    void deleteModel(Long id);

    ChatModelDO getModel(Long id);

    PageDataInfo<ChatModelDO> getModelPage(AiModelPageDTO pageDTO);

    void updateModelState(AiModelStateDTO dto);

    ChatModelDO validateModel(Long id);

    ChatModel getChatModel(Long id);

    ImageModel getImageModel(Long id);
}
