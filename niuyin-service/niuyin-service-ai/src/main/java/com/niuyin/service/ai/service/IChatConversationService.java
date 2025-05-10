package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.ai.chat.domain.ChatConversationDO;
import com.niuyin.model.common.dto.PageDTO;

/**
 * AI 聊天对话表(AiChatConversation)表服务接口
 *
 * @author roydon
 * @since 2025-04-22 10:13:38
 */
public interface IChatConversationService extends IService<ChatConversationDO> {

    IPage<ChatConversationDO> getList(PageDTO dto);
}
