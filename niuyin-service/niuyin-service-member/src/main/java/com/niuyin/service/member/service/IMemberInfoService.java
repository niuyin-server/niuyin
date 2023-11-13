package com.niuyin.service.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.member.domain.MemberInfo;
import com.niuyin.model.member.vo.MemberInfoVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 会员详情表(MemberInfo)表服务接口
 *
 * @author roydon
 * @since 2023-11-12 22:26:25
 */
public interface IMemberInfoService extends IService<MemberInfo> {

    /**
     * 查询我的信息详情
     *
     * @return
     */
    MemberInfoVO queryMemberInfo();

    /**
     * 上传背景图片
     *
     * @param file
     * @return
     */
    String uploadBackGround(MultipartFile file);
}
