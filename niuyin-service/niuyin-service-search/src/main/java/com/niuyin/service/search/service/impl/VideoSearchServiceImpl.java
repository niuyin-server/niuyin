package com.niuyin.service.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.niuyin.common.context.UserContext;
import com.niuyin.common.service.RedisService;
import com.niuyin.common.utils.bean.BeanCopyUtils;
import com.niuyin.common.utils.string.StringUtils;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.dubbo.api.DubboVideoService;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.search.dto.PageDTO;
import com.niuyin.model.video.domain.Video;
import com.niuyin.service.search.constant.ESIndexConstants;
import com.niuyin.service.search.constant.VideoHotTitleCacheConstants;
import com.niuyin.service.search.service.VideoSearchService;
import com.niuyin.model.search.dto.VideoSearchKeywordDTO;
import com.niuyin.service.search.domain.VideoSearchVO;
import com.niuyin.service.search.service.VideoSearchHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.ZoneId;
import java.util.*;

import static com.niuyin.service.search.constant.ESQueryConstants.*;

/**
 * VideoSearchServiceImpl
 *
 * @AUTHOR: roydon
 * @DATE: 2023/10/31
 **/
@Slf4j
@Service("videoSearchServiceImpl")
public class VideoSearchServiceImpl implements VideoSearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private VideoSearchHistoryService videoSearchHistoryService;

    @Resource
    private RedisService redisService;

    @DubboReference
    private DubboVideoService dubboVideoService;

    @DubboReference
    private DubboMemberService dubboMemberService;

    /**
     * 视频同步新增到es
     */
    @Override
    public void videoSync(String videoId) {
        Video video = dubboVideoService.apiGetVideoByVideoId(videoId);
        Member member = dubboMemberService.apiGetById(video.getUserId());
        VideoSearchVO searchVO = new VideoSearchVO();
        searchVO.setVideoId(video.getVideoId());
        searchVO.setVideoTitle(video.getVideoTitle());
        searchVO.setPublishTime(Date.from(video.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
        searchVO.setCoverImage(video.getCoverImage());
        searchVO.setVideoUrl(video.getVideoUrl());
        searchVO.setPublishType(video.getPublishType());
        searchVO.setUserId(video.getUserId());
        searchVO.setUserNickName(member.getNickName());
        searchVO.setUserAvatar(member.getAvatar());

        IndexRequest indexRequest = new IndexRequest(ESIndexConstants.INDEX_VIDEO);
        indexRequest.id(searchVO.getVideoId())
                .source(JSON.toJSONString(searchVO), XContentType.JSON);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("sync es error ==> {}", e.getMessage());
        }
    }

    /**
     * 更新视频索引文档
     *
     * @param json
     */
    @Override
    public void updateVideoDoc(String json) {
        VideoSearchVO videoSearchVO = JSON.parseObject(json, VideoSearchVO.class);
        UpdateRequest updateRequest = new UpdateRequest(ESIndexConstants.INDEX_VIDEO, videoSearchVO.getVideoId());
        updateRequest.doc(json, XContentType.JSON);
        updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        try {
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("failed to update document to elasticsearch,the doc is:{},the exception is {}", json, e.getMessage());
        }
    }

    /**
     * 删除文档
     *
     * @param videoId
     */
    @Override
    public void deleteVideoDoc(String videoId) {
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(ESIndexConstants.INDEX_VIDEO);
        deleteByQueryRequest.setQuery(new TermsQueryBuilder("videoId", videoId));
        deleteByQueryRequest.setRefresh(true);
        try {
            restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("failed to delete the document in elasticsearch,the doc id is:{},the exception is {}", videoId, e.getMessage());
        }
    }

    /**
     * es分页搜索视频
     *
     * @param dto String keyword;
     *            private Integer pageNum;
     *            private Integer pageSize;
     *            最小时间  minBehotTime;
     * @return
     */
    @Override
    public List<VideoSearchVO> searchVideoFromES(VideoSearchKeywordDTO dto) {
        // 构建查询请求
//        long todayStartLong = DateUtils.getTodayPlusStartLocalLong(-1); //今日数据
//        long dayStartLong = DateUtils.getTodayPlusStartLocalLong(-7); //本周数据
//        log.debug("todayStartLong:{}", dayStartLong);
//        dto.setMinBehotTime(new Date(dayStartLong));
        SearchRequest searchRequest = buildSearchRequest(dto);

        try {
            // 执行查询请求
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 处理搜索结果
            return processSearchResponse(searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private SearchRequest buildSearchRequest(VideoSearchKeywordDTO videoSearchKeywordDTO) {
        SearchRequest searchRequest = new SearchRequest(ESIndexConstants.INDEX_VIDEO);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 设置分页
        searchSourceBuilder.from((videoSearchKeywordDTO.getPageNum() - 1) * videoSearchKeywordDTO.getPageSize());
        searchSourceBuilder.size(videoSearchKeywordDTO.getPageSize());

        // 构建查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 构建多字段匹配查询
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(videoSearchKeywordDTO.getKeyword(), VideoSearchVO.VIDEO_TITLE, VideoSearchVO.USER_NICKNAME);
        boolQueryBuilder.must(multiMatchQueryBuilder);

        // 构建范围过滤器
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(VideoSearchVO.PUBLISH_TIME)
                .gte(videoSearchKeywordDTO.getMinBehotTime())
                .lte(new Date().getTime());
        boolQueryBuilder.filter(rangeQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);

        // 设置高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .boundaryScannerLocale(zh_CN)
                .field(VideoSearchVO.VIDEO_TITLE)
                .field(VideoSearchVO.USER_NICKNAME)
                .preTags(Highlight_preTags)
                .postTags(Highlight_postTags);
        searchSourceBuilder.highlighter(highlightBuilder);

        // 设置排序
        ScoreSortBuilder scoreSortField = SortBuilders.scoreSort().order(SortOrder.DESC);
        FieldSortBuilder publishTimeSortField = SortBuilders.fieldSort(VideoSearchVO.PUBLISH_TIME).order(SortOrder.DESC).sortMode(SortMode.MAX);
        searchSourceBuilder.sort(scoreSortField);
        searchSourceBuilder.sort(publishTimeSortField);

        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

    private List<VideoSearchVO> processSearchResponse(SearchResponse searchResponse) {
        // 处理搜索结果
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<VideoSearchVO> res = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 处理每个搜索结果

            // 获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField titleHighlightField = highlightFields.get(VideoSearchVO.VIDEO_TITLE);
            HighlightField nicknameHighlightField = highlightFields.get(VideoSearchVO.USER_NICKNAME);

            // 处理高亮显示的片段
            String highlightedTitle = "";
            String highlightedNickname = "";
            if (titleHighlightField != null) {
                Text[] titleFragments = titleHighlightField.fragments();
                for (Text fragment : titleFragments) {
                    highlightedTitle += fragment;
                }
            }
            if (nicknameHighlightField != null) {
                Text[] nicknameFragments = nicknameHighlightField.fragments();
                for (Text fragment : nicknameFragments) {
                    highlightedNickname += fragment;
                }
            }

            // 结果封装
            Map map = JSON.parseObject(hit.getSourceAsString(), Map.class);
            VideoSearchVO searchVO = new VideoSearchVO();
            try {
                BeanUtils.populate(searchVO, map);
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
            searchVO.setVideoTitle((highlightedTitle.equals("") || highlightedTitle.isEmpty() ? (String) hit.getSourceAsMap().get(VideoSearchVO.VIDEO_TITLE) : highlightedTitle));
            searchVO.setUserNickName((highlightedNickname.equals("") || highlightedNickname.isEmpty() ? (String) hit.getSourceAsMap().get(VideoSearchVO.USER_NICKNAME) : highlightedNickname));

//            VideoSearchVO searchVO = new VideoSearchVO();
//            searchVO.setVideoId((String) hit.getSourceAsMap().get(VideoSearchVO.VIDEO_ID));
//            searchVO.setVideoTitle((highlightedTitle.equals("") || highlightedTitle.isEmpty() ? (String) hit.getSourceAsMap().get(VideoSearchVO.VIDEO_TITLE) : highlightedTitle));
//            searchVO.setPublishTime(new Date((Long) hit.getSourceAsMap().get(VideoSearchVO.PUBLISH_TIME)));
//            searchVO.setCoverImage((String) hit.getSourceAsMap().get(VideoSearchVO.COVER_IMAGE));
//            searchVO.setVideoUrl((String) hit.getSourceAsMap().get(VideoSearchVO.VIDEO_URL));
//            searchVO.setPublishType((String) hit.getSourceAsMap().get(VideoSearchVO.PUBLISH_TYPE));
//            searchVO.setUserId((Long) hit.getSourceAsMap().get(VideoSearchVO.USER_ID));
//            searchVO.setUserNickName((String) hit.getSourceAsMap().get(VideoSearchVO.USER_NICKNAME));
//            searchVO.setUserAvatar((String) hit.getSourceAsMap().get(VideoSearchVO.USER_AVATAR));
            res.add(searchVO);
        }
        return res;
    }

    /**
     * @param dto
     * @return
     * @throws Exception
     */
    @Override
    public List<VideoSearchVO> searchAllVideoFromES(VideoSearchKeywordDTO dto) throws Exception {
        //1.参数校验
        if (StringUtils.isNull(dto) || StringUtils.isBlank(dto.getKeyword())) {
            return null;
        }
        //2.设置查询条件
        SearchRequest searchRequest = new SearchRequest(ESIndexConstants.INDEX_VIDEO);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //布尔查询videoTitle或者userNickName
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        FuzzyQueryBuilder queryStringQueryBuilder = QueryBuilders.fuzzyQuery("videoTitle", dto.getKeyword());
        boolQueryBuilder.must(queryStringQueryBuilder);
        //查询小于mindate的数据
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("publishTime").lt(dto.getMinBehotTime() == null ? new Date().getTime() : dto.getMinBehotTime().getTime());
        boolQueryBuilder.filter(rangeQueryBuilder);
        //分页查询
        searchSourceBuilder.from(dto.getPageNum());
        searchSourceBuilder.size(dto.getPageSize());
        //按照发布时间倒序查询
        searchSourceBuilder.sort("publishTime", SortOrder.DESC);
        //设置高亮  videoTitle
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("videoTitle");
        highlightBuilder.preTags("<font class='keyword-hint'>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //3.结果封装返回
        List<VideoSearchVO> result = new ArrayList<>();

        SearchHit[] hits = searchResponse.getHits().getHits();
        String removeS1 = "<font class='keyword-hint'>";
        String removeS2 = "</font>";
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            Map map = JSON.parseObject(source, Map.class);
            //处理高亮
            if (hit.getHighlightFields() != null && hit.getHighlightFields().size() > 0) {
                Text[] titles = hit.getHighlightFields().get("videoTitle").getFragments();
                String title = org.apache.commons.lang3.StringUtils.join(titles);
                //高亮标题
                map.put("videoTitle", title);
            } else {
                //原始标题
                map.put("videoTitle", map.get("videoTitle"));
            }
            VideoSearchVO videoSearchVO = new VideoSearchVO();
            BeanUtils.populate(videoSearchVO, map);
            videoSearchVO.setVideoTitle(videoSearchVO.getVideoTitle().replace(removeS1, "").replace(removeS2, ""));
            result.add(videoSearchVO);
        }
        result.forEach(System.out::println);
        return result;
    }

    /**
     * 从redis中获取热搜排行榜
     *
     * @return
     */
    @Override
    public Set findSearchHot(PageDTO pageDTO) {
        int startIndex = (pageDTO.getPageNum() - 1) * pageDTO.getPageSize();
        int endIndex = startIndex + pageDTO.getPageSize() - 1;
        Set cacheZSetRange = redisService.getCacheZSetRange(VideoHotTitleCacheConstants.VIDEO_HOT_TITLE_PREFIX, startIndex, endIndex);
        return cacheZSetRange;
    }
}
