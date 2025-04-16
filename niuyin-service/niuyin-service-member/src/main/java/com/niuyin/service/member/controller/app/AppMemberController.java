package com.niuyin.service.member.controller.app;

import com.niuyin.common.core.domain.R;
import com.niuyin.model.member.vo.app.AppMemberInfoVO;
import com.niuyin.service.member.service.IMemberInfoService;
import com.niuyin.service.member.service.IMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * UserController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1/app")
public class AppMemberController {

    @Resource
    private IMemberService memberService;

    @Resource
    private IMemberInfoService memberInfoService;

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public R<AppMemberInfoVO> userInfoById(@PathVariable Long userId) {
        return R.ok(memberInfoService.getUserInfoById(userId));
    }

}
