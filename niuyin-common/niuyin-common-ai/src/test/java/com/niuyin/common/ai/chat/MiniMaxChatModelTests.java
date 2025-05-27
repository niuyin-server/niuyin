package com.niuyin.common.ai.chat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.minimax.MiniMaxChatModel;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.ai.minimax.api.MiniMaxApi;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link MiniMaxChatModel} 的集成测试
 */
public class MiniMaxChatModelTests {

    private final MiniMaxChatModel chatModel = new MiniMaxChatModel(
            new MiniMaxApi("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJHcm91cE5hbWUiOiJyb3lkb24iLCJVc2VyTmFtZSI6InJveWRvbiIsIkFjY291bnQiOiIiLCJTdWJqZWN0SUQiOiIxOTIzMTE4MjM0NjM1MDgwNTIzIiwiUGhvbmUiOiIxODIwMzcwNzgzNyIsIkdyb3VwSUQiOiIxOTIzMTE4MjM0NjI2NjkxOTE1IiwiUGFnZU5hbWUiOiIiLCJNYWlsIjoiIiwiQ3JlYXRlVGltZSI6IjIwMjUtMDUtMTcgMTc6Mzc6NTIiLCJUb2tlblR5cGUiOjEsImlzcyI6Im1pbmltYXgifQ.QqudD2YKVoIP6FgAx3QkqkQxdrHxbpX7HuP-6yiIIBz1rjJ0H-H0cCQrMLyzspNMvmHWPulnDAv564pSJenMFrd1L9lOVAbiZQkwPEdS-zzSELeyV_ViMNmwIs31eXngIXE35KSa7owCLnQKCiGwCzPb1m_2SpA3NmJRN1bJ0-_DCdVlAE4fzNQZ73iDX_LSN_lBPV7WB5_jK4RZ61HpyjU3ot0-uIzZdtHLSGqt0PV75O5k25xX5IdQ22fgmH2CMoziObYbnBcgBB3VtN2gXGzjV2_6jhxS9oCMfGnWf6dyGpb-jaZ9HYNzR8ocIxq4kHgzU8GKKFYozQ_s3AIYIA"), // 密钥
            MiniMaxChatOptions.builder()
                    .model(MiniMaxApi.ChatModel.ABAB_6_5_G_Chat.getValue()) // 模型
                    .build());

    @Test
    @Disabled
    public void testCall() {
        // 准备参数
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage("你是一个优质的文言文作者，用文言文描述着各城市的人文风景。"));
        messages.add(new UserMessage("1 + 1 = ？"));

        // 调用
        ChatResponse response = chatModel.call(new Prompt(messages));
        // 打印结果
        System.out.println(response);
        System.out.println(response.getResult().getOutput());
    }

    @Test
    @Disabled
    public void testStream() {
        // 准备参数
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage("你是一个优质的文言文作者，用文言文描述着各城市的人文风景。"));
        messages.add(new UserMessage("1 + 1 = ？"));

        // 调用
        Flux<ChatResponse> flux = chatModel.stream(new Prompt(messages));
        // 打印结果
        flux.doOnNext(response -> {
//            System.out.println(response);
            System.out.println(response.getResult().getOutput());
        }).then().block();
    }

}
