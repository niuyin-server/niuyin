package com.niuyin.service.ai.tool;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.niuyin.common.ai.util.AiUtils;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.utils.bean.BeanUtils;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.model.member.domain.Member;
import io.github.javpower.vectorexclient.req.LoginUser;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

/**
 * 工具：当前用户信息查询
 * <p>
 * 同时，也是展示 ToolContext 上下文的使用
 *
 * @author Ren
 */
@Component("user_profile_query")
public class UserProfileQueryToolFunction implements BiFunction<UserProfileQueryToolFunction.Request, ToolContext, UserProfileQueryToolFunction.Response> {

    @DubboReference(retries = 3, mock = "return null")
    private DubboMemberService dubboMemberService;

    @Data
    @JsonClassDescription("当前用户信息查询")
    public static class Request {
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        /**
         * 用户ID
         */
        private Long id;
        /**
         * 用户昵称
         */
        private String nickName;

        /**
         * 手机号码
         */
        private String telephone;
        /**
         * 用户头像
         */
        private String avatar;

    }

    @Override
    public Response apply(Request request, ToolContext toolContext) {
        Long loginUserId = (Long) toolContext.getContext().get(AiUtils.TOOL_CONTEXT_LOGIN_USER);
        if (loginUserId == null) {
            return null;
        }
        Member member = dubboMemberService.apiGetById(loginUserId);
        return BeanUtils.toBean(member, Response.class);

    }

}
