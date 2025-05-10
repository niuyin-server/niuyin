package com.niuyin.service.ai.controller.chat;

import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.ai.chat.domain.ChatConversationDO;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.service.ai.service.IChatConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * AI 聊天对话表(AiChatConversation)表控制层
 *
 * @author roydon
 * @since 2025-04-22 10:13:36
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat/conversation")
public class ChatConversationController {

    private final IChatConversationService chatConversationService;
    private final SnowFlake snowFlake;

    /**
     * 分页查询
     */
    @PostMapping("/list")
    public PageDataInfo<ChatConversationDO> list(@Validated @RequestBody PageDTO dto) {
        return PageDataInfo.page(chatConversationService.getList(dto));
    }

    /**
     * 新增对话
     */
    @PostMapping
    public R<?> add(@RequestBody ChatConversationDO chatConversationDO) {
        chatConversationDO.setId(snowFlake.nextId());
        chatConversationDO.setUserId(UserContext.getUserId());
        chatConversationDO.setCreateBy(UserContext.getUser().getUserName());
        LocalDateTime localDateTime = LocalDateTime.now();
        chatConversationDO.setCreateTime(localDateTime);
        chatConversationDO.setUpdateTime(localDateTime);
        chatConversationDO.setTitle(StringUtils.isEmpty(chatConversationDO.getTitle()) ? "新对话" : chatConversationDO.getTitle());
        chatConversationDO.setTemperature(0.75);
        chatConversationDO.setMaxTokens(4096);
        chatConversationDO.setMaxContexts(20);
        chatConversationService.save(chatConversationDO);
        return R.ok(chatConversationDO);
    }

    /**
     * 编辑数据
     */
    @PutMapping
    public R<?> edit(@RequestBody ChatConversationDO chatConversationDO) {
        chatConversationDO.setUpdateBy(UserContext.getUser().getUserName());
        chatConversationDO.setUpdateTime(LocalDateTime.now());
        return R.ok(this.chatConversationService.updateById(chatConversationDO));
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/{id}")
    public R<?> removeById(@PathVariable Long id) {
        return R.ok(this.chatConversationService.removeById(id));
    }

}

