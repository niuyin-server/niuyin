package com.niuyin.service.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.ai.domain.ChatConversationDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 聊天对话表(AiChatConversation)表数据库访问层
 *
 * @author roydon
 * @since 2025-04-22 10:13:36
 */
@Mapper
public interface ChatConversationMapper extends BaseMapper<ChatConversationDO>{

}

