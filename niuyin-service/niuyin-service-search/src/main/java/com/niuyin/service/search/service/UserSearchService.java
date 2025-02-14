package com.niuyin.service.search.service;

import com.niuyin.model.search.dto.UserSearchKeywordDTO;
import com.niuyin.service.search.domain.vo.UserSearchVO;

import java.util.List;

/**
 * UserSearchService
 *
 * @AUTHOR: roydon
 * @DATE: 2024/10/11
 **/
public interface UserSearchService {
    /**
     * 从es分页搜索用户
     */
    List<UserSearchVO> searchUserFromES(UserSearchKeywordDTO dto);
}
