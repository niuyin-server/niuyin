package com.niuyin.service.ai.controller.web.model;

import com.niuyin.common.core.domain.R;
import com.niuyin.model.ai.domain.model.ChatModelDO;
import com.niuyin.model.ai.vo.model.ModelVO;
import com.niuyin.service.ai.service.IChatModelService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ModelController
 *
 * @AUTHOR: roydon
 * @DATE: 2025/6/7
 **/
@RestController
@RequestMapping("v1/model")
public class ModelController {

    @Resource
    private IChatModelService chatModelService;

    @GetMapping("/list")
    public R<List<ModelVO>> getModelPage(@RequestParam(value = "type", required = false) String type,
                                         @RequestParam(value = "platform", required = false) String platform) {
        return R.ok(chatModelService.getModelList(type, platform));
    }

}
