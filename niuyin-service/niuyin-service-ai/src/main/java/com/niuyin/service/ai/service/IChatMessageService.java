package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.ai.domain.ChatMessageDO;

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
}
