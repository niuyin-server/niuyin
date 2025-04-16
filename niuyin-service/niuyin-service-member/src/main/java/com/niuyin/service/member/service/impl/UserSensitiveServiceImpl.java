package com.niuyin.service.member.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niuyin.service.member.service.IUserSensitiveService;
import com.niuyin.model.member.domain.UserSensitive;
import com.niuyin.service.member.mapper.UserSensitiveMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * 用户敏感词信息表(UserSensitive)表服务实现类
 *
 * @author roydon
 * @since 2023-10-29 20:41:18
 */
@Service("userSensitiveService")
public class UserSensitiveServiceImpl extends ServiceImpl<UserSensitiveMapper, UserSensitive> implements IUserSensitiveService {
    @Resource
    private UserSensitiveMapper userSensitiveMapper;

}
