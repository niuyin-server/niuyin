package com.niuyin.service.ai.controller.web.chat;

import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.model.ai.domain.chat.ChatConversationDO;
import com.niuyin.model.ai.domain.chat.ChatMessageDO;
import com.niuyin.model.ai.domain.model.ChatModelDO;
import com.niuyin.model.ai.domain.model.ModelRoleDO;
import com.niuyin.model.ai.dto.model.web.ChatConversationSaveDTO;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.common.enums.TrueOrFalseEnum;
import com.niuyin.service.ai.service.IChatConversationService;
import com.niuyin.service.ai.service.IChatMessageService;
import com.niuyin.service.ai.service.IChatModelService;
import com.niuyin.service.ai.service.IModelRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * AI 聊天对话表(AiChatConversation)表控制层
 *
 * @author roydon
 * @since 2025-04-22 10:13:36
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/chat/conversation")
public class ChatConversationController {

    private final IChatConversationService chatConversationService;
    private final SnowFlake snowFlake;
    private final IModelRoleService modelRoleService;
    private final IChatMessageService chatMessageService;
    private final IChatModelService chatModelService;

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
    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public R<ChatConversationDO> add(@RequestBody ChatConversationSaveDTO dto) {
        ChatConversationDO chatConversationDO = BeanCopyUtils.copyBean(dto, ChatConversationDO.class);
        chatConversationDO.setId(snowFlake.nextId());
        chatConversationDO.setUserId(UserContext.getUserId());
        chatConversationDO.setCreateBy(UserContext.getUser().getUserName());
        LocalDateTime localDateTime = LocalDateTime.now();
        chatConversationDO.setCreateTime(localDateTime);
        chatConversationDO.setUpdateTime(localDateTime);
        chatConversationDO.setTitle("新对话");
        chatConversationDO.setTemperature(0.75);
        chatConversationDO.setMaxTokens(4096);
        chatConversationDO.setMaxContexts(20);
        // 是否添加了角色
        if (chatConversationDO.getRoleId() != null) {
            ModelRoleDO modelRole = modelRoleService.getModelRole(chatConversationDO.getRoleId());
            if (Objects.isNull(modelRole)) {
                throw new RuntimeException("角色不存在");
            }
            chatConversationDO.setTitle(modelRole.getName());
            chatConversationDO.setSystemMessage(modelRole.getSystemMessage());
            chatConversationDO.setSystemMessage(modelRole.getSystemMessage());
            // todo 获取角色关联的模型，填充参数
            ChatModelDO modelDO = chatModelService.getById(modelRole.getModelId());
            if (Objects.isNull(modelDO)) {
                throw new RuntimeException("模型不存在");
            }
            chatConversationDO.setTemperature(modelDO.getTemperature().doubleValue());
            chatConversationDO.setMaxTokens(modelDO.getMaxTokens());
            chatConversationDO.setMaxContexts(modelDO.getMaxContexts());
            // 填充默认第一条ai回复预设消息
            ChatMessageDO message = new ChatMessageDO().setConversationId(chatConversationDO.getId())
                    .setModel(modelDO.getModel())
                    .setModelId(modelDO.getId())
                    .setUserId(UserContext.getUserId())
                    .setRoleId(modelRole.getId())
                    .setMessageType(MessageType.ASSISTANT.getValue())
                    .setContent(modelRole.getChatPrologue())
                    .setUseContext(TrueOrFalseEnum.FALSE.getCode());
            message.setCreateTime(LocalDateTime.now());
            chatMessageService.save(message);
        }
//        chatConversationDO.setTitle(StringUtils.isEmpty(chatConversationDO.getTitle()) ? "新对话" : chatConversationDO.getTitle());
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

