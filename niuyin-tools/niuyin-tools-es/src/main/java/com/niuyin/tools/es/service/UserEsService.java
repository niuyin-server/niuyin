package com.niuyin.tools.es.service;

import com.niuyin.model.search.domain.UserEO;
import com.niuyin.tools.es.repository.UserEsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * UserEsService
 *
 * @AUTHOR: roydon
 * @DATE: 2025/8/4
 **/
@Service
public class UserEsService {
    private final UserEsRepository userEsRepository;

    public UserEsService(UserEsRepository userEsRepository) {
        this.userEsRepository = userEsRepository;
    }

    public UserEO save(UserEO user) {
        return userEsRepository.save(user);
    }

    // 根据 ID 查询
    public Optional<UserEO> findById(String userId) {
        return userEsRepository.findById(userId);
    }

    // 删除
    public void deleteById(String userId) {
        userEsRepository.deleteById(userId);
    }

    public List<UserEO> findByNickName(String nickName) {
        return userEsRepository.findByNickName(nickName);
    }
}
