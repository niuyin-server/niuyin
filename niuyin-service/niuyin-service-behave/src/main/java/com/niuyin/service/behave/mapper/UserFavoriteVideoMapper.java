package com.niuyin.service.behave.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niuyin.model.behave.domain.UserFavoriteVideo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * (UserFavoriteVideo)表数据库访问层
 *
 * @author lzq
 * @since 2023-11-17 10:16:03
 */
@Mapper
public interface UserFavoriteVideoMapper extends BaseMapper<UserFavoriteVideo>{

}

