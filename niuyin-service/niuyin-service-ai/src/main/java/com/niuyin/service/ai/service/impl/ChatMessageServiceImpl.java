package com.niuyin.service.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.model.ai.domain.ChatConversationDO;
import com.niuyin.model.ai.domain.ChatMessageDO;
import com.niuyin.service.ai.mapper.ChatMessageMapper;
import com.niuyin.service.ai.service.IChatConversationService;
import com.niuyin.service.ai.service.IChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * ChatMessageServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2025/4/22
 **/
@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessageDO> implements IChatMessageService {
    private final ChatMessageMapper chatMessageMapper;
    private final IChatConversationService chatConversationService;

    /**
     * 获得指定对话的消息列表
     */
    @Override
    public List<ChatMessageDO> listByCid(Long conversationId) {
        ChatConversationDO conversationDO = chatConversationService.getById(conversationId);
        if (Objects.isNull(conversationDO) || !Objects.equals(UserContext.getUserId(), conversationDO.getUserId())) {
            return null;
        }
        LambdaQueryWrapper<ChatMessageDO> qw = new LambdaQueryWrapper<>();
        qw.eq(ChatMessageDO::getConversationId, conversationId);
        qw.orderByAsc(ChatMessageDO::getId);
        return this.list(qw);
    }
}
