package com.niuyin.service.ai.controller.chat;

import cn.hutool.core.util.StrUtil;
import com.niuyin.common.cache.ratelimiter.core.annotation.RateLimiter;
import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.model.ai.chat.domain.ChatConversationDO;
import com.niuyin.model.ai.chat.domain.ChatMessageDO;
import com.niuyin.service.ai.service.IChatConversationService;
import com.niuyin.service.ai.service.IChatMessageService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
public class ChatbotController {

    private final ChatClient chatClient;
    private final InMemoryChatMemory inMemoryChatMemory;
    private final IChatConversationService chatConversationService;
    private final IChatMessageService chatMessageService;
    private final SnowFlake snowFlake;

    @RateLimiter(count = 10, time = 1, timeUnit = TimeUnit.HOURS, message = "请求达到上限，可以过一个时辰再来试试哦o_0")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@Validated @RequestBody ChatRequest request) {
        // 校验对话是否存在
        ChatConversationDO conversationDO = chatConversationService.getById(request.conversationId());
        if (Objects.isNull(conversationDO)) {
            return Flux.just(ServerSentEvent.builder("Error: 对话不存在").event("error").build());
        }
        // 插入用户提问
        ChatMessageDO userMessage = new ChatMessageDO();
        userMessage.setId(snowFlake.nextId());
        userMessage.setConversationId(request.conversationId());
        userMessage.setUserId(request.userId());
        userMessage.setMessageType(MessageType.USER.getValue());
        userMessage.setContent(request.message());
        userMessage.setUseContext("1");
        userMessage.setCreateTime(LocalDateTime.now());
        chatMessageService.save(userMessage);

        // todo @roydon 构建 Prompt，使用对话上下文
        ChatClient.ChatClientRequestSpec prompt = chatClient.prompt(request.message());
        if (StringUtils.isNotEmpty(conversationDO.getSystemMessage())) {
            prompt.system(conversationDO.getSystemMessage());
        }
        prompt.advisors(new MessageChatMemoryAdvisor(inMemoryChatMemory, request.conversationId().toString(), 10), new SimpleLoggerAdvisor());

        StringBuffer contentBuffer = new StringBuffer();
        return prompt.stream().content().map((content) -> {
                    contentBuffer.append(StrUtil.nullToDefault(content, ""));
                    return ServerSentEvent.builder(content).event("message").build();
                })
                //问题回答结束标识,以便前端消息展示处理
                .concatWithValues(ServerSentEvent.builder("[DONE]").build())
                .doOnComplete(() -> {
                    log.info("用户的提问:{}已结束", request.message());
                    // 插入ai回复
                    ChatMessageDO assistantMessage = new ChatMessageDO();
                    assistantMessage.setId(snowFlake.nextId());
                    assistantMessage.setConversationId(request.conversationId());
                    assistantMessage.setUserId(request.userId());
                    assistantMessage.setReplyId(userMessage.getReplyId());
                    assistantMessage.setMessageType(MessageType.ASSISTANT.getValue());
                    assistantMessage.setContent(contentBuffer.toString());
                    assistantMessage.setUseContext("1");
                    assistantMessage.setCreateTime(LocalDateTime.now());
                    chatMessageService.save(assistantMessage);
                    // 更新对话的最后一次回复时间
                    conversationDO.setLastMessage(contentBuffer.substring(0, Math.min(contentBuffer.length(), 64)));
                    conversationDO.setUpdateTime(LocalDateTime.now());
                    chatConversationService.updateById(conversationDO);
                })
                .onErrorResume(e -> Flux.just(ServerSentEvent.builder("Error: " + e.getMessage()).event("error").build()));
    }

    record ChatRequest(@NotNull(message = "请选择对话") Long conversationId,
                       Long userId,
                       @NotNull(message = "请输入内容") String message) {
    }

}
