package com.niuyin.service.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.member.dto.LoginUserDTO;
import com.niuyin.model.member.dto.RegisterBody;
import com.niuyin.model.member.dto.UpdatePasswordDTO;

import java.util.List;

/**
 * 用户表(User)表服务接口
 *
 * @author roydon
 * @since 2023-10-24 19:18:25
 */
public interface IMemberService extends IService<Member> {

    /**
     * 通过ID查询单条数据
     */
    Member queryById(Long userId);
    List<Member> queryInIds(List<Long> userIds);

    /**
     * 用户注册
     */
    boolean register(RegisterBody registerBody);

    /**
     * 登录
     */
    String login(LoginUserDTO loginUserDTO);

    /**
     * 更新信息
     */
    Member updateUserInfo(Member user);

    /**
     * 修改密码
     */
    boolean updatePass(UpdatePasswordDTO dto);

    /**
     * 获取头像
     */
    String getAvatarById(Long userId);
}
