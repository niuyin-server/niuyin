package com.niuyin.service.recommend.service.impl;

import com.niuyin.model.recommend.modal.UserVideoScore;
import com.niuyin.service.recommend.algo.SimilarityAlgo;
import com.niuyin.service.recommend.mapper.UserVideoBehaveMapper;
import com.niuyin.service.recommend.service.UserBasedCollaborativeFilterService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * VideoRecommendService
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/27
 **/
@Service
public class VideoRecommendServiceImpl implements UserBasedCollaborativeFilterService {

    @Resource
    private UserVideoBehaveMapper userVideoBehaveMapper;

    @Resource
    private SimilarityAlgo similarityAlgo;

    /**
     * 为指定用户生成推荐视频列表
     * todo 可以只取到近一周的本用户的行为
     */
    @Override
    public List<String> generateVideoRecommendations(Long userId) {
        List<UserVideoScore> userVideoScores = userVideoBehaveMapper.queryAllUserVideoBehave();
        Map<Long, Map<String, Double>> userVideoRatings = new HashMap<>();

        for (UserVideoScore userVideoScore : userVideoScores) {
            long userIdD = userVideoScore.getUserId();
            String videoId = userVideoScore.getVideoId();
            double totalScore = Double.parseDouble(userVideoScore.getScore());

            userVideoRatings.putIfAbsent(userIdD, new HashMap<>());
            userVideoRatings.get(userIdD).put(videoId, totalScore);
        }
        Map<String, Double> userSimilarity = new HashMap<>();
        Map<String, Double> totalSimilarity = new HashMap<>();

        for (Map.Entry<Long, Map<String, Double>> entry : userVideoRatings.entrySet()) {
            Long otherUser = entry.getKey();
            Map<String, Double> otherRatings = entry.getValue();

            if (Objects.equals(otherUser, userId)) {
                continue;
            }

            double similarity = similarityAlgo.pearsonCorrelation(userVideoRatings.get(userId), otherRatings);

            if (similarity <= 0) {
                continue;
            }

            for (Map.Entry<String, Double> videoEntry : otherRatings.entrySet()) {
                String video = videoEntry.getKey();
                Double rating = videoEntry.getValue();

                if (!userVideoRatings.get(userId).containsKey(video) || userVideoRatings.get(userId).get(video) == 0) {
                    userSimilarity.put(video, userSimilarity.getOrDefault(video, 0.0) + rating * similarity);
                    totalSimilarity.put(video, totalSimilarity.getOrDefault(video, 0.0) + similarity);
                }
            }
        }

        List<Map.Entry<String, Double>> recommendations = new ArrayList<>(userSimilarity.entrySet());
//        recommendations.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        List<String> recommendationKeys = new ArrayList<>();
        for (Map.Entry<String, Double> entry : recommendations) {
            recommendationKeys.add(entry.getKey());
        }

        return recommendationKeys;
    }

}
