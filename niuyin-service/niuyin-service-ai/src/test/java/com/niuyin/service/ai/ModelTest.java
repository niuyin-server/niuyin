package com.niuyin.service.ai;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ModelTest
 *
 * @AUTHOR: roydon
 * @DATE: 2025/6/11
 **/
@Slf4j
@SpringBootTest
public class ModelTest {

    @Resource
    private TokenCountEstimator tokenCountEstimator;

    @Test
    @DisplayName("测试token数量")
    void testTokenCount() {
        String input = """
                我用spring ai对接了一个豆包大模型，实现自定义chatmodel，如下是代码
                public DouBaoChatModel buildDouBaoChatClient(NiuyinAiProperties.DouBaoProperties properties) {
                        if (StrUtil.isEmpty(properties.getModel())) {
                            properties.setModel(DouBaoChatModel.MODEL_DEFAULT);
                        }
                        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                                .openAiApi(OpenAiApi.builder()
                                        .baseUrl(DouBaoChatModel.BASE_URL)
                                        .apiKey(properties.getApiKey())
                                        .build())
                                .defaultOptions(OpenAiChatOptions.builder()
                                        .model(properties.getModel())
                                        .temperature(properties.getTemperature())
                                        .maxTokens(properties.getMaxTokens())
                                        .topP(properties.getTopP())
                                        .build())
                                .toolCallingManager(getToolCallingManager())
                                .build();
                        return new DouBaoChatModel(openAiChatModel);
                    }
                你现在是高级java开发，现在有一个工具表，字段有工具名称（即程序里的bean），对话时指定工具，可将工具传入给大模型进行调用，请你实现。
                """;

        int tokenCount = tokenCountEstimator.estimate(input);
        log.debug("tokenCount : {}", tokenCount);
        // 输出
        // tokenCount : 257
    }

}
