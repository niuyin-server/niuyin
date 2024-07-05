//package com.niuyin.service.recommend.service;
//
//import java.util.*;
//
//public class ItemBasedCF {
//
//    // 假设ratings是一个Map，键是用户ID，值是一个Map，内部Map的键是物品ID，值是评分
//    private static Map<Integer, Map<Integer, Double>> ratings;
//
//    // 物品相似度矩阵，初始化为0
//    private static Map<Integer, Map<Integer, Double>> similarities = new HashMap<>();
//
//    // 计算物品间的相似度，这里简化为使用余弦相似度
//    public static void computeSimilarities() {
//        for (Map.Entry<Integer, Map<Integer, Double>> itemRatings : ratings.entrySet()) {
//            int itemId = itemRatings.getKey();
//            for (Map.Entry<Integer, Double> otherItemRatings : ratings.entrySet()) {
//                if (itemId != otherItemRatings.getKey()) {
//                    double similarity = cosineSimilarity(itemRatings.getValue(), otherItemRatings.getValue());
//                    similarities.computeIfAbsent(itemId, k -> new HashMap<>()).put(otherItemRatings.getKey(), similarity);
//                }
//            }
//        }
//    }
//
//    // 计算两个物品的余弦相似度
//    private static double cosineSimilarity(Map<Integer, Double> ratings1, Map<Integer, Double> ratings2) {
//        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
//        Set<Integer> commonUsers = new HashSet<>(ratings1.keySet());
//        commonUsers.retainAll(ratings2.keySet());
//
//        for (Integer userId : commonUsers) {
//            dotProduct += ratings1.get(userId) * ratings2.get(userId);
//            normA += Math.pow(ratings1.get(userId), 2);
//            normB += Math.pow(ratings2.get(userId), 2);
//        }
//
//        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
//    }
//
//    // 为用户生成推荐
//    public static List<Integer> recommendItems(int userId, int numberOfRecommendations) {
//        Map<Integer, Double> userRatings = ratings.get(userId);
//        if (userRatings == null) return Collections.emptyList();
//
//        // 找到用户未评分且与已评分物品最相似的N个物品
//        PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(
//                (a, b) -> b.getValue().compareTo(a.getValue()));
//
//        for (Map.Entry<Integer, Double> ratedItem : userRatings.entrySet()) {
//            int itemId = ratedItem.getKey();
//            if (similarities.containsKey(itemId)) {
//                for (Map.Entry<Integer, Double> similarity : similarities.get(itemId).entrySet()) {
//                    int otherItemId = similarity.getKey();
//                    if (!userRatings.containsKey(otherItemId)) {
//                        pq.offer(new AbstractMap.SimpleEntry<>(otherItemId, similarity.getValue()));
//                        if (pq.size() > numberOfRecommendations) {
//                            pq.poll();
//                        }
//                    }
//                }
//            }
//        }
//
//        List<Integer> recommendations = new ArrayList<>();
//        while (!pq.isEmpty()) {
//            recommendations.add(pq.poll().getKey());
//        }
//        return recommendations;
//    }
//
//    public static void main(String[] args) {
//        // 初始化ratings数据（这里省略了具体数据填充过程）
//        computeSimilarities();
//        int userId = 1; // 假定的用户ID
//        int numRecs = 5; // 推荐物品数量
//        List<Integer> recommendedItems = recommendItems(userId, numRecs);
//        System.out.println("Recommended items for user " + userId + ": " + recommendedItems);
//    }
//}
