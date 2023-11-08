package com.niuyin.service.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.user.domain.User;
import com.niuyin.model.user.dto.LoginUserDTO;
import com.niuyin.model.user.dto.RegisterBody;
import com.niuyin.model.user.dto.UpdatePasswordDTO;

/**
 * 用户表(User)表服务接口
 *
 * @author roydon
 * @since 2023-10-24 19:18:25
 */
public interface IUserService extends IService<User> {

    /**
     * 通过ID查询单条数据
     */
    User queryById(Long userId);

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
    User updateUserInfo(User user);

    /**
     * 修改密码
     */
    boolean updatePass(UpdatePasswordDTO dto);
}
