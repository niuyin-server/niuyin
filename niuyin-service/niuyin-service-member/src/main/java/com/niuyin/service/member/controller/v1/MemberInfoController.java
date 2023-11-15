package com.niuyin.service.member.controller.v1;

import com.niuyin.common.domain.R;
import com.niuyin.model.member.vo.MemberInfoVO;
import com.niuyin.service.member.service.IMemberInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 会员详情表(MemberInfo)表控制层
 *
 * @author roydon
 * @since 2023-11-12 22:26:24
 */
@RestController
@RequestMapping("/api/v1/info")
public class MemberInfoController {

    @Resource
    private IMemberInfoService memberInfoService;

    /**
     * 获取用户详情
     */
    @GetMapping()
    public R<MemberInfoVO> getMemberInfo() {
        return R.ok(memberInfoService.queryMemberInfo());
    }

    /**
     * 上传用户背景图片
     */
    @PostMapping("/backImage/upload")
    public R<?> uploadBackImage(@RequestParam("file") MultipartFile file) {
        memberInfoService.uploadBackGround(file);
        return R.ok();


    }

}

