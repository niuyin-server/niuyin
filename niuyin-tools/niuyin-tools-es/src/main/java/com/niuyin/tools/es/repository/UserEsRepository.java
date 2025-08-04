package com.niuyin.tools.es.repository;

import com.niuyin.model.search.domain.UserEO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserEsRepository
 *
 * @AUTHOR: roydon
 * @DATE: 2025/8/4
 **/
@Repository
public interface UserEsRepository extends ElasticsearchRepository<UserEO, String> {

    /**
     * 根据 昵称 查询
     */
    List<UserEO> findByNickName(String nickName);
}
