package com.niuyin.service.ai.controller;

import cn.hutool.core.util.StrUtil;
import com.niuyin.common.cache.ratelimiter.core.annotation.RateLimiter;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin("*")
@RestController
public class ChatbotController {

    private final ChatClient chatClient;
    private final InMemoryChatMemory inMemoryChatMemory;

    @RateLimiter(count = 10, time = 1, timeUnit = TimeUnit.HOURS, message = "请求达到上限，可以过一个时辰再来试试哦o_0")
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest request) {
        //用户id
        String userId = request.userId();
        StringBuffer contentBuffer = new StringBuffer();
        return chatClient.prompt(request.message())
                .advisors(new MessageChatMemoryAdvisor(inMemoryChatMemory, userId, 10), new SimpleLoggerAdvisor())
                .stream().content().map((content) -> {
                    contentBuffer.append(StrUtil.nullToDefault(content, ""));
                    return ServerSentEvent.builder(content).event("message").build();
                })
                //问题回答结束标识,以便前端消息展示处理
                .concatWithValues(ServerSentEvent.builder("[DONE]").build())
                .doOnComplete(() -> {
                    log.info("用户:{}的提问:{}已结束", userId, request.message());
                    // todo 插入对话消息记录
                })
                .onErrorResume(e -> Flux.just(ServerSentEvent.builder("Error: " + e.getMessage()).event("error").build()));
    }

    record ChatRequest(Long conversationId, String userId, String message) {
    }

}
