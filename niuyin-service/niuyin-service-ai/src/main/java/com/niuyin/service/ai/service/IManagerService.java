package com.niuyin.service.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niuyin.model.ai.AiManagerDO;
import com.niuyin.model.ai.AiManagerUserInfo;
import com.niuyin.service.ai.controller.admin.AdminManagerController;

/**
 * AI管理员表(AiManager)表服务接口
 *
 * @author roydon
 * @since 2025-05-30 23:39:17
 */
public interface IManagerService extends IService<AiManagerDO> {

    /**
     * 管理员登录
     *
     * @param dto 登录信息
     * @return 登录token
     */
    String login(AdminManagerController.LoginDTO dto);

     /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    AiManagerUserInfo userInfo();
}
