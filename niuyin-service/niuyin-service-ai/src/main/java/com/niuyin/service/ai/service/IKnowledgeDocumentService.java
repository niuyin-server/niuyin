package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.ai.domain.knowledge.KnowledgeDocumentDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.niuyin.common.core.utils.CollectionUtils.convertMap;

/**
 * AI 知识库文档表(KnowledgeDocument)表服务接口
 *
 * @author roydon
 * @since 2025-06-03 22:03:43
 */
public interface IKnowledgeDocumentService extends IService<KnowledgeDocumentDO> {

    /**
     * 获取文档列表
     *
     * @param ids 文档编号列表
     * @return 文档列表
     */
    List<KnowledgeDocumentDO> getKnowledgeDocumentList(Collection<Long> ids);

    /**
     * 获取文档 Map
     *
     * @param ids 文档编号列表
     * @return 文档 Map
     */
    default Map<Long, KnowledgeDocumentDO> getKnowledgeDocumentMap(Collection<Long> ids) {
        return convertMap(getKnowledgeDocumentList(ids), KnowledgeDocumentDO::getId);
    }
}
