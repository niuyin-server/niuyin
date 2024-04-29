package com.niuyin.service.recommend.algo;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 相似度算法
 *
 * @AUTHOR: roydon
 * @DATE: 2024/4/27
 **/
@Service
public class SimilarityAlgo {


    /**
     * 计算用户之间的相似度（皮尔逊相关系数）
     */
    public double pearsonCorrelation(Map<String, Double> user1Ratings, Map<String, Double> user2Ratings) {
        Set<String> commonVideos = new HashSet<>(user1Ratings.keySet());
        commonVideos.retainAll(user2Ratings.keySet());

        if (commonVideos.isEmpty()) {
            return 0;
        }

        double sum1 = commonVideos.stream().mapToDouble(user1Ratings::get).sum();
        double sum2 = commonVideos.stream().mapToDouble(user2Ratings::get).sum();

        double sum1Sq = commonVideos.stream().mapToDouble(video -> Math.pow(user1Ratings.get(video), 2)).sum();
        double sum2Sq = commonVideos.stream().mapToDouble(video -> Math.pow(user2Ratings.get(video), 2)).sum();

        double productSum = commonVideos.stream().mapToDouble(video -> user1Ratings.get(video) * user2Ratings.get(video)).sum();

        double num = productSum - (sum1 * sum2 / commonVideos.size());
        double den = Math.sqrt((sum1Sq - Math.pow(sum1, 2) / commonVideos.size()) * (sum2Sq - Math.pow(sum2, 2) / commonVideos.size()));

        if (den == 0) {
            return 0;
        }

        return num / den;
    }
}
