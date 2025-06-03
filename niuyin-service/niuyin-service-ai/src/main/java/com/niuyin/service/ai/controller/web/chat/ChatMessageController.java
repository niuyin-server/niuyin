package com.niuyin.service.ai.controller.web.chat;

import com.niuyin.common.core.domain.R;
import com.niuyin.model.ai.domain.chat.ChatMessageDO;
import com.niuyin.service.ai.service.IChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ChatMessageController
 *
 * @AUTHOR: roydon
 * @DATE: 2025/4/22
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat/message")
public class ChatMessageController {
    private final IChatMessageService chatMessageService;

    /**
     * todo 后续改造为游标分页
     */
    @Operation(summary = "获得指定对话的消息列表")
    @GetMapping("/list-by-cid")
    @Parameter(name = "cid", required = true, description = "对话编号", example = "1024")
    public R<List<ChatMessageDO>> getChatMessageListByConversationId(@RequestParam("cid") Long conversationId) {
        return R.ok(chatMessageService.listByCid(conversationId));
    }

}
