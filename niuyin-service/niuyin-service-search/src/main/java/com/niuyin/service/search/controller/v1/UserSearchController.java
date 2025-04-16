package com.niuyin.service.search.controller.v1;

import com.niuyin.common.core.domain.R;
import com.niuyin.model.search.dto.UserSearchKeywordDTO;
import com.niuyin.service.search.domain.vo.UserSearchVO;
import com.niuyin.service.search.service.UserSearchService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.annotation.Resource;
import java.util.List;

/**
 * 用户搜索控制层
 *
 * @AUTHOR: roydon
 * @DATE: 2024/10/11
 **/
@RestController
@RequestMapping("/api/v1/user")
public class UserSearchController {

    @Resource
    private UserSearchService userSearchService;

    /**
     * 分页搜索用户
     */
    @PostMapping()
    public R<List<UserSearchVO>> searchVideo(@Validated @RequestBody UserSearchKeywordDTO dto) {
        List<UserSearchVO> userSearchResultList = userSearchService.searchUserFromES(dto);
        return R.ok(userSearchResultList);
    }
}
