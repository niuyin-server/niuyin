package com.niuyin.service.ai.controller.chat;

import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.ai.domain.ChatConversationDO;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.service.ai.service.IChatConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return R.ok(chatConversationService.save(chatConversationDO));
    }
//
//    /**
//     * 编辑数据
//     *
//     * @param chatConversationDO 实体
//     * @return 编辑结果
//     */
//    @PutMapping
//    public AjaxResult edit(ChatConversationDO chatConversationDO) {
//        return AjaxResult.success(this.chatConversationService.update(chatConversationDO));
//    }
//
//    /**
//     * 删除数据
//     *
//     * @param id 主键
//     * @return 删除是否成功
//     */
//    @DeleteMapping
//    public AjaxResult removeById(Long id) {
//        return AjaxResult.success(this.chatConversationService.deleteById(id));
//    }

}

