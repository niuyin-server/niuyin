package com.niuyin.service.ai.controller.web.knowledge;

import com.niuyin.common.core.domain.R;
import com.niuyin.common.core.utils.bean.BeanUtils;
import com.niuyin.model.ai.domain.knowledge.KnowledgeSegmentDO;
import com.niuyin.model.ai.vo.knowledge.web.AiKnowledgeSegmentRespVO;
import com.niuyin.service.ai.service.IKnowledgeSegmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.annotation.Resource;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI 知识库分段表(KnowledgeSegment)表控制层
 *
 * @author roydon
 * @since 2025-06-03 22:03:58
 */
@RestController
@RequestMapping("v1/knowledge/segment")
public class KnowledgeSegmentController {

    @Resource
    private IKnowledgeSegmentService knowledgeSegmentService;

    @GetMapping("/split")
    @Operation(summary = "内容切片")
    @Parameters({
            @Parameter(name = "url", description = "文档 URL", required = true),
            @Parameter(name = "segmentMaxTokens", description = "分段的最大 Token 数", required = true)
    })
    public R<List<AiKnowledgeSegmentRespVO>> splitContent(
            @RequestParam("url") @URL String url,
            @RequestParam(value = "segmentMaxTokens") Integer segmentMaxTokens) {
        List<KnowledgeSegmentDO> segments = knowledgeSegmentService.splitContent(url, segmentMaxTokens);
        return R.ok(BeanUtils.toBean(segments, AiKnowledgeSegmentRespVO.class));
    }
}

