package com.niuyin.service.ai.controller.web.chat;

import com.niuyin.common.cache.ratelimiter.core.annotation.RateLimiter;
import com.niuyin.common.cache.ratelimiter.core.keyresolver.impl.ClientIpRateLimiterKeyResolver;
import com.niuyin.common.core.compont.SnowFlake;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.model.ai.vo.chat.ChatMessageVO;
import com.niuyin.service.ai.service.IChatConversationService;
import com.niuyin.service.ai.service.IChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/chat")
public class ChatbotController {

    //    private final ChatClient chatClient;
    private final InMemoryChatMemory inMemoryChatMemory;
    private final IChatConversationService chatConversationService;
    private final IChatMessageService chatMessageService;
    private final SnowFlake snowFlake;

//    @RateLimiter(count = 10, time = 1, timeUnit = TimeUnit.HOURS, message = "请求达到上限，可以过一个时辰再来试试哦o_0")
//    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<ServerSentEvent<String>> streamChat(@Validated @RequestBody ChatRequest request) {
//        // 校验对话是否存在
//        ChatConversationDO conversationDO = chatConversationService.getById(request.conversationId());
//        if (Objects.isNull(conversationDO)) {
//            return Flux.just(ServerSentEvent.builder("Error: 对话不存在").event("error").build());
//        }
//        // 插入用户提问
//        ChatMessageDO userMessage = new ChatMessageDO();
//        userMessage.setId(snowFlake.nextId());
//        userMessage.setConversationId(request.conversationId());
//        userMessage.setUserId(request.userId());
//        userMessage.setMessageType(MessageType.USER.getValue());
//        userMessage.setContent(request.message());
//        userMessage.setUseContext("1");
//        userMessage.setCreateTime(LocalDateTime.now());
//        chatMessageService.save(userMessage);
//
//        // todo @roydon 构建 Prompt，使用对话上下文
//        ChatClient.ChatClientRequestSpec prompt = chatClient.prompt(request.message());
//        if (StringUtils.isNotEmpty(conversationDO.getSystemMessage())) {
//            prompt.system(conversationDO.getSystemMessage());
//        }
//        prompt.advisors(new MessageChatMemoryAdvisor(inMemoryChatMemory, request.conversationId().toString(), 10), new SimpleLoggerAdvisor());
//
//        StringBuffer contentBuffer = new StringBuffer();
//        return prompt.stream().content().map((content) -> {
//                    contentBuffer.append(StrUtil.nullToDefault(content, ""));
//                    return ServerSentEvent.builder(content).event("message").build();
//                })
//                //问题回答结束标识,以便前端消息展示处理
//                .concatWithValues(ServerSentEvent.builder("[DONE]").build())
//                .doOnComplete(() -> {
//                    log.info("用户的提问:{}已结束", request.message());
//                    // 插入ai回复
//                    ChatMessageDO assistantMessage = new ChatMessageDO();
//                    assistantMessage.setId(snowFlake.nextId());
//                    assistantMessage.setConversationId(request.conversationId());
//                    assistantMessage.setUserId(request.userId());
//                    assistantMessage.setReplyId(userMessage.getReplyId());
//                    assistantMessage.setMessageType(MessageType.ASSISTANT.getValue());
//                    assistantMessage.setContent(contentBuffer.toString());
//                    assistantMessage.setUseContext("1");
//                    assistantMessage.setCreateTime(LocalDateTime.now());
//                    chatMessageService.save(assistantMessage);
//                    // 更新对话的最后一次回复时间
//                    conversationDO.setLastMessage(contentBuffer.substring(0, Math.min(contentBuffer.length(), 64)));
//                    conversationDO.setUpdateTime(LocalDateTime.now());
//                    chatConversationService.updateById(conversationDO);
//                })
//                .onErrorResume(e -> Flux.just(ServerSentEvent.builder("Error: " + e.getMessage()).event("error").build()));
//    }

    public record ChatRequest(@NotNull(message = "请选择对话") Long conversationId,
                              @NotNull(message = "请输入内容") String message,
                              @Schema(description = "是否携带上下文", example = "true") Boolean useContext) {
    }

    @PermitAll
    @Operation(summary = "发送消息（流式）", description = "流式返回，响应较快")
    @RateLimiter(count = 10, time = 2, timeUnit = TimeUnit.HOURS, message = "请求达到上限，可以过一个时辰再来试试哦o_0", keyResolver = ClientIpRateLimiterKeyResolver.class)
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<R<ChatMessageVO>> sendChatMessageStream(@Valid @RequestBody ChatRequest dto) {
        return chatMessageService.sendChatMessageStream(dto, UserContext.getUserId());
    }

}
