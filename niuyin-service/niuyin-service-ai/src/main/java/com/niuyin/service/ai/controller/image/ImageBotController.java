package com.niuyin.service.ai.controller.image;

import com.niuyin.common.core.domain.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.web.bind.annotation.*;

/**
 * ImageBotController
 *
 * @AUTHOR: roydon
 * @DATE: 2025/4/28
 **/
@Slf4j
@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/image")
public class ImageBotController {

    private final OpenAiImageModel openAiImageModel;

    @GetMapping("/generate")
    public R<?> chat(@RequestParam(value = "message", defaultValue = "生成一只萌萌的狮子？") String message) {
        ImageResponse imageResponse = openAiImageModel.call(new ImagePrompt(message));
        Image output = imageResponse.getResult().getOutput();
        log.debug("ImageBotController.chat: url: {} , base64 : {}", output.getUrl(), output.getB64Json());
        // 图片链接有效期为1个小时，todo @roydon 图片转存
        return R.ok(output.getUrl());
    }

}
