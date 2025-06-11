package com.niuyin.service.ai.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.utils.bean.BeanUtils;
import com.niuyin.model.ai.domain.knowledge.KnowledgeDocumentDO;
import com.niuyin.model.ai.dto.knowledge.web.KnowledgeDocumentCreateDTO;
import com.niuyin.model.common.enums.StateFlagEnum;
import com.niuyin.service.ai.mapper.KnowledgeDocumentMapper;
import com.niuyin.service.ai.service.IKnowledgeDocumentService;
import com.niuyin.service.ai.service.IKnowledgeSegmentService;
import com.niuyin.service.ai.service.IKnowledgeService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * AI 知识库文档表(KnowledgeDocument)表服务实现类
 *
 * @author roydon
 * @since 2025-06-03 22:03:43
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class KnowledgeDocumentServiceImpl extends ServiceImpl<KnowledgeDocumentMapper, KnowledgeDocumentDO> implements IKnowledgeDocumentService {
    private final KnowledgeDocumentMapper knowledgeDocumentMapper;
    private final IKnowledgeService knowledgeService;
    private final TokenCountEstimator tokenCountEstimator;
    private final IKnowledgeSegmentService knowledgeSegmentService;

    /**
     * 获取文档列表
     *
     * @param ids 文档编号列表
     * @return 文档列表
     */
    @Override
    public List<KnowledgeDocumentDO> getKnowledgeDocumentList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return knowledgeDocumentMapper.selectBatchIds(ids);
    }

    /**
     * 新建文档（单个）
     *
     * @return 文档编号
     */
    @Override
    public Long createKnowledgeDocument(KnowledgeDocumentCreateDTO dto) {
        // 1. 校验参数
        knowledgeService.validateKnowledgeExists(dto.getKnowledgeId());

        // 2. 下载文档
        String content = readUrl(dto.getUrl());

        // 3. 文档记录入库
        KnowledgeDocumentDO documentDO = BeanUtils.toBean(dto, KnowledgeDocumentDO.class)
                .setContent(content)
                .setContentLength(content.length())
                .setTokens(tokenCountEstimator.estimate(content))
                .setStateFlag(StateFlagEnum.ENABLE.getCode());
        knowledgeDocumentMapper.insert(documentDO);

        // 4. 文档切片入库（异步）
        knowledgeSegmentService.createKnowledgeSegmentBySplitContentAsync(documentDO.getId(), content);
        return documentDO.getId();
    }

    /**
     * 读取 URL 内容
     *
     * @param url URL
     * @return 内容
     */
    @Override
    public String readUrl(String url) {
        // 下载文件
        ByteArrayResource resource;
        try {
            byte[] bytes = HttpUtil.downloadBytes(url);
            if (bytes.length == 0) {
                throw new RuntimeException("文档内容为空!");
            }
            resource = new ByteArrayResource(bytes);
        } catch (Exception e) {
            log.error("[readUrl][url({}) 读取失败]", url, e);
            throw new RuntimeException("文件下载失败!");
        }

        // 读取文件
        TikaDocumentReader loader = new TikaDocumentReader(resource);
        List<Document> documents = loader.get();
        Document document = CollUtil.getFirst(documents);
        if (document == null || StrUtil.isEmpty(document.getText())) {
            throw new RuntimeException("文档加载失败!");
        }
        return document.getText();
    }

    @Override
    public KnowledgeDocumentDO validateKnowledgeDocumentExists(Long documentId) {
        KnowledgeDocumentDO knowledgeDocumentDO = this.getById(documentId);
        if (knowledgeDocumentDO == null) {
            throw new RuntimeException("文档不存在!");
        }
        return knowledgeDocumentDO;
    }
}
