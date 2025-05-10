package com.niuyin.service.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.model.ai.chat.domain.ChatConversationDO;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.service.ai.mapper.ChatConversationMapper;
import com.niuyin.service.ai.service.IChatConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI 聊天对话表(AiChatConversation)表服务实现类
 *
 * @author roydon
 * @since 2025-04-22 10:13:39
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ChatConversationServiceImpl extends ServiceImpl<ChatConversationMapper, ChatConversationDO> implements IChatConversationService {
    private final ChatConversationMapper chatConversationMapper;

    @Override
    public IPage<ChatConversationDO> getList(PageDTO dto) {
        LambdaQueryWrapper<ChatConversationDO> qw = new LambdaQueryWrapper<>();
        qw.eq(ChatConversationDO::getUserId, UserContext.getUserId());
//        qw.orderByDesc(ChatConversationDO::getPinned);
//        qw.orderByDesc(ChatConversationDO::getPinnedTime);
        qw.orderByDesc(ChatConversationDO::getCreateTime);
        return this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), qw);
    }
}
