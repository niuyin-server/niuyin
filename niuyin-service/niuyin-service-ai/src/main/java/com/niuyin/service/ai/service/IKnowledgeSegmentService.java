package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.ai.bo.KnowledgeSegmentSearchReqBO;
import com.niuyin.model.ai.bo.KnowledgeSegmentSearchRespBO;
import com.niuyin.model.ai.domain.knowledge.KnowledgeSegmentDO;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * AI 知识库分段表(KnowledgeSegment)表服务接口
 *
 * @author roydon
 * @since 2025-06-03 22:03:58
 */
public interface IKnowledgeSegmentService extends IService<KnowledgeSegmentDO> {

    /**
     * 搜索知识库分段
     */
    List<KnowledgeSegmentSearchRespBO> searchKnowledgeSegment(KnowledgeSegmentSearchReqBO bo);

    /**
     * 基于 content 内容，切片创建多个段落
     *
     * @param documentId 知识库文档编号
     * @param content    文档内容
     */
    void createKnowledgeSegmentBySplitContent(Long documentId, String content);

    /**
     * 【异步】基于 content 内容，切片创建多个段落
     *
     * @param documentId 知识库文档编号
     * @param content    文档内容
     */
    @Async
    default void createKnowledgeSegmentBySplitContentAsync(Long documentId, String content) {
        createKnowledgeSegmentBySplitContent(documentId, content);
    }

    /**
     * 根据 URL 内容，切片创建多个段落
     *
     * @param url              URL 地址
     * @param segmentMaxTokens 段落最大 Token 数
     * @return 切片后的段落列表
     */
    List<KnowledgeSegmentDO> splitContent(String url, Integer segmentMaxTokens);

    /**
     * 根据文档编号删除段落
     *
     * @param documentId 文档编号
     */
    void deleteKnowledgeSegmentByDocumentId(Long documentId);
}
