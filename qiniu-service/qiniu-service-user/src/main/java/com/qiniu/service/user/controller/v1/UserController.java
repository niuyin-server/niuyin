package com.qiniu.service.user.controller.v1;

import cn.hutool.core.util.PhoneUtil;
import com.qiniu.common.constant.Constants;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.domain.R;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.service.RedisService;
import com.qiniu.common.utils.EmailUtils;
import com.qiniu.common.utils.file.PathUtils;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.model.common.enums.HttpCodeEnum;
import com.qiniu.model.user.domain.User;
import com.qiniu.model.user.dto.LoginUserDTO;
import com.qiniu.model.user.dto.RegisterBody;
import com.qiniu.model.user.dto.UpdatePasswordDTO;
import com.qiniu.service.user.constants.QiniuUserOssConstants;
import com.qiniu.service.user.constants.UserCacheConstants;
import com.qiniu.service.user.service.IUserService;
import com.qiniu.starter.file.service.FileStorageService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * UserController
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/24
 **/
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private RedisService redisService;

    @Resource
    private FileStorageService fileStorageService;

    /**
     * 登录
     *
     * @param loginUserDTO
     * @return
     */
    @ApiOperation("登录")
    @PostMapping("/login")
    public R<Map<String, String>> login(@RequestBody LoginUserDTO loginUserDTO) {
        log.debug("登录用户：{}", loginUserDTO);
        String token = userService.login(loginUserDTO);
        Map<String, String> map = new HashMap<>();
        map.put(Constants.TOKEN, token);
        return R.ok(map);
    }

    /**
     * 注册
     *
     * @param registerBody
     * @return
     */
    @ApiOperation("注册")
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody RegisterBody registerBody) {
        log.debug("注册用户：{}", registerBody);
        boolean b = userService.register(registerBody);
        return R.ok(b);
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    @ApiOperation("更新信息")
    @PutMapping("/update")
    public R<User> save(@RequestBody User user) {
        // 校验邮箱
        if (StringUtils.isNotEmpty(user.getEmail()) && !EmailUtils.isValidEmail(user.getEmail())) {
            throw new CustomException(HttpCodeEnum.EMAIL_VALID_ERROR);
        }
        // 校验手机号
        if (StringUtils.isNotEmpty(user.getTelephone()) && !PhoneUtil.isPhone(user.getTelephone())) {
            throw new CustomException(HttpCodeEnum.TELEPHONE_VALID_ERROR);
        }
        return R.ok(userService.updateUserInfo(user));
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    @ApiOperation("根据id获取用户信息")
    @GetMapping("/{userId}")
    public R<User> userInfoById(@PathVariable Long userId) {
        return R.ok(getUserFromCache(userId));
    }


    /**
     * 通过token获取用户信息
     */
    @ApiOperation("获取用户信息")
    @GetMapping("/userinfo")
    public R<User> userInfo() {
        Long userId = UserContext.getUser().getUserId();
        if (StringUtils.isNull(userId)) {
            R.fail(HttpCodeEnum.NEED_LOGIN.getCode(), "请先登录");
        }
        return R.ok(getUserFromCache(userId));
    }

    /**
     * 从缓存获取用户详情
     */
    private User getUserFromCache(Long userId) {
        User userCache = redisService.getCacheObject(UserCacheConstants.USER_INFO_PREFIX + userId);
        if (StringUtils.isNotNull(userCache)) {
            return userCache;
        }
        User user = userService.queryById(userId);
        user.setPassword(null);
        user.setSalt(null);
        // 设置缓存
        redisService.setCacheObject(UserCacheConstants.USER_INFO_PREFIX + userId, user);
        redisService.expire(UserCacheConstants.USER_INFO_PREFIX + userId, UserCacheConstants.USER_INFO_EXPIRE_TIME, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 修改密码
     *
     * @param dto 原密码，新密码，确认密码
     * @return user
     */
    @ApiOperation("修改密码")
    @PostMapping("/updatepass")
    public R<?> updatePass(@RequestBody UpdatePasswordDTO dto) {
        return R.ok(userService.updatePass(dto));
    }

    /**
     * 头像上传
     *
     * @param file 图片文件，大小限制1M
     * @return url
     */
    @ApiOperation("上传头像")
    @PostMapping("/avatar")
    public R<String> avatar(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isNull(originalFilename)) {
            throw new CustomException(HttpCodeEnum.IMAGE_TYPE_FOLLOW);
        }
        //对原始文件名进行判断
        if (originalFilename.endsWith(".png")
                || originalFilename.endsWith(".jpg")
                || originalFilename.endsWith(".jpeg")
                || originalFilename.endsWith(".webp")) {
            String filePath = PathUtils.generateFilePath(originalFilename);
            String url = fileStorageService.uploadImgFile(file, QiniuUserOssConstants.PREFIX_URL, filePath);
            return R.ok(url);
        } else {
            throw new CustomException(HttpCodeEnum.IMAGE_TYPE_FOLLOW);
        }
    }

}
