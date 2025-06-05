package com.niuyin.common.ai.image;

import com.alibaba.cloud.ai.dashscope.api.DashScopeImageApi;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;

/**
 * {@link DashScopeImageModel} 集成测试类
 */
public class TongYiImagesModelTest {

    private final DashScopeImageModel imageModel = new DashScopeImageModel(new DashScopeImageApi(""));

    @Test
    @Disabled
    public void imageCallTest() {
        // 准备参数
        ImageOptions options = DashScopeImageOptions.builder()
                .withModel("wanx2.1-t2i-turbo")
                .withHeight(1024).withWidth(1024)
                .build();
        ImagePrompt prompt = new ImagePrompt("一只散发着威严的腾空中国龙!", options);

        // 方法调用
        ImageResponse response = imageModel.call(prompt);
        // 打印结果
        System.out.println(response);
    }

}
