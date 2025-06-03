package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.common.core.domain.R;
import com.niuyin.model.ai.domain.chat.ChatMessageDO;
import com.niuyin.model.ai.vo.chat.ChatMessageVO;
import com.niuyin.service.ai.controller.web.chat.ChatbotController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * IChatMessageService
 *
 * @AUTHOR: roydon
 * @DATE: 2025/4/22
 **/
public interface IChatMessageService extends IService<ChatMessageDO> {

    /**
     * 获得指定对话的消息列表
     */
    List<ChatMessageDO> listByCid(Long conversationId);

    Flux<R<ChatMessageVO>> sendChatMessageStream(ChatbotController.ChatRequest dto, Long userId);
}
