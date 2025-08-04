package com.niuyin.service.ai.controller.web.chat;

import com.niuyin.common.cache.ratelimiter.core.annotation.RateLimiter;
import com.niuyin.common.cache.ratelimiter.core.keyresolver.impl.ClientIpRateLimiterKeyResolver;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.R;
import com.niuyin.model.ai.vo.chat.ChatMessageVO;
import com.niuyin.service.ai.service.IChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("v1/chat")
public class ChatbotController {

    private final IChatMessageService chatMessageService;

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
