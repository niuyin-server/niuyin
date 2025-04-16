package com.niuyin.service.recommend;

import com.niuyin.model.recommend.modal.UserVideoScore;
import com.niuyin.service.recommend.mapper.UserVideoBehaveMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserVideoBehaveTest
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/27
 **/
@Slf4j
@SpringBootTest
public class UserVideoBehaveTest {

    @Resource
    private UserVideoBehaveMapper userVideoBehaveMapper;

    @Test
    public void testQueryAllUserVideoBehave() {
        List<UserVideoScore> userVideoScores = userVideoBehaveMapper.queryAllUserVideoBehave();
//        userVideoScores.forEach(System.out::println);
        Map<Long, Map<String, Double>> userVideoRatings = new HashMap<>();

        for (UserVideoScore userVideoScore : userVideoScores) {
            long userId = userVideoScore.getUserId();
            String videoId = userVideoScore.getVideoId();
            double totalScore = Double.parseDouble(userVideoScore.getScore());

            userVideoRatings.putIfAbsent(userId, new HashMap<>());
            userVideoRatings.get(userId).put(videoId, totalScore);
        }
        for (Map.Entry<Long, Map<String, Double>> entry : userVideoRatings.entrySet()) {
            long userId = entry.getKey();
            Map<String, Double> videoScores = entry.getValue();

            System.out.println("User ID: " + userId);
            for (Map.Entry<String, Double> videoEntry : videoScores.entrySet()) {
                String videoId = videoEntry.getKey();
                double score = videoEntry.getValue();
                System.out.println("Video ID: " + videoId + ", Score: " + score);
            }
        }


    }


}
