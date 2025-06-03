package com.niuyin.service.ai.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.common.core.utils.bean.BeanUtils;
import com.niuyin.model.ai.bo.KnowledgeSegmentSearchReqBO;
import com.niuyin.model.ai.bo.KnowledgeSegmentSearchRespBO;
import com.niuyin.model.ai.domain.knowledge.KnowledgeDO;
import com.niuyin.model.ai.domain.knowledge.KnowledgeSegmentDO;
import com.niuyin.service.ai.mapper.KnowledgeSegmentMapper;
import com.niuyin.service.ai.service.IChatModelService;
import com.niuyin.service.ai.service.IKnowledgeSegmentService;
import com.niuyin.service.ai.service.IKnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.niuyin.common.core.utils.CollectionUtils.convertList;

/**
 * AI 知识库分段表(KnowledgeSegment)表服务实现类
 *
 * @author roydon
 * @since 2025-06-03 22:03:58
 */
@RequiredArgsConstructor
@Service
public class KnowledgeSegmentServiceImpl extends ServiceImpl<KnowledgeSegmentMapper, KnowledgeSegmentDO> implements IKnowledgeSegmentService {

    private static final String VECTOR_STORE_METADATA_KNOWLEDGE_ID = "knowledgeId";
    private static final String VECTOR_STORE_METADATA_DOCUMENT_ID = "documentId";
    private static final String VECTOR_STORE_METADATA_SEGMENT_ID = "segmentId";

    private static final Map<String, Class<?>> VECTOR_STORE_METADATA_TYPES = Map.of(
            VECTOR_STORE_METADATA_KNOWLEDGE_ID, String.class,
            VECTOR_STORE_METADATA_DOCUMENT_ID, String.class,
            VECTOR_STORE_METADATA_SEGMENT_ID, String.class);

    private final KnowledgeSegmentMapper knowledgeSegmentMapper;
    private final IKnowledgeService knowledgeService;
    private final IChatModelService chatModelService;

    /**
     * 搜索知识库分段
     */
    @Override
    public List<KnowledgeSegmentSearchRespBO> searchKnowledgeSegment(KnowledgeSegmentSearchReqBO bo) {
        // 1. 校验
        KnowledgeDO knowledge = knowledgeService.validateKnowledgeExists(bo.getKnowledgeId());

        // 2.1 向量检索
        VectorStore vectorStore = getVectorStoreById(knowledge);
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder()
                .query(bo.getContent())
                .topK(ObjUtil.defaultIfNull(bo.getTopK(), knowledge.getTopK()))
                .similarityThreshold(ObjUtil.defaultIfNull(bo.getSimilarityThreshold(), knowledge.getSimilarityThreshold().doubleValue()))
                .filterExpression(new FilterExpressionBuilder().eq(VECTOR_STORE_METADATA_KNOWLEDGE_ID, bo.getKnowledgeId().toString()).build())
                .build());
        if (CollUtil.isEmpty(documents)) {
            return ListUtil.empty();
        }
        // 2.2 段落召回
        List<String> convertIds = convertList(documents, Document::getId);
        List<KnowledgeSegmentDO> segments = this.list(Wrappers.<KnowledgeSegmentDO>lambdaQuery().in(KnowledgeSegmentDO::getVectorId, convertIds).orderByDesc(KnowledgeSegmentDO::getId));
        if (CollUtil.isEmpty(segments)) {
            return ListUtil.empty();
        }

        // 3. 增加召回次数
        knowledgeSegmentMapper.updateRetrievalCountIncrByIds(convertList(segments, KnowledgeSegmentDO::getId));

        // 4. 构建结果
        List<KnowledgeSegmentSearchRespBO> result = convertList(segments, segment -> {
            Document document = CollUtil.findOne(documents, // 找到对应的文档
                    doc -> Objects.equals(doc.getId(), segment.getVectorId()));
            if (document == null) {
                return null;
            }
            return BeanUtils.toBean(segment, KnowledgeSegmentSearchRespBO.class).setScore(document.getScore());
        });
        result.sort((o1, o2) -> Double.compare(o2.getScore(), o1.getScore())); // 按照分数降序排序
        return result;
    }

    private VectorStore getVectorStoreById(KnowledgeDO knowledge) {
        return chatModelService.getOrCreateVectorStore(knowledge.getEmbeddingModelId(), VECTOR_STORE_METADATA_TYPES);
    }

}
