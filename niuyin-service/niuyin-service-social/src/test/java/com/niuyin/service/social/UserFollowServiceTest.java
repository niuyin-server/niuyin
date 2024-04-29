package com.niuyin.service.social;

import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.model.common.dto.PageDTO;
import com.niuyin.model.video.vo.VideoVO;
import com.niuyin.service.social.service.IUserFollowService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * UserFollowServiceTest
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/7
 **/
@Slf4j
@SpringBootTest
public class UserFollowServiceTest {

    @Resource
    private IUserFollowService userFollowService;

    @Test
    public void test() {
        PageDataInfo<VideoVO> socialDynamicVideoPage = userFollowService.getSocialDynamicVideoPage(new PageDTO());
        socialDynamicVideoPage.toString();
    }
}
