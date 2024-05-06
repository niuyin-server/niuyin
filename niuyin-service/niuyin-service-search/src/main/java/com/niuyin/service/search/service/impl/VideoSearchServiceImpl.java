package com.niuyin.service.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.niuyin.common.core.context.UserContext;
import com.niuyin.common.core.domain.vo.PageDataInfo;
import com.niuyin.common.cache.service.RedisService;
import com.niuyin.common.core.utils.bean.BeanCopyUtils;
import com.niuyin.common.core.utils.date.DateUtils;
import com.niuyin.common.core.utils.string.StringUtils;
import com.niuyin.dubbo.api.DubboMemberService;
import com.niuyin.dubbo.api.DubboVideoService;
import com.niuyin.model.common.enums.VideoPlatformEnum;
import com.niuyin.model.member.domain.Member;
import com.niuyin.model.search.dto.PageDTO;
import com.niuyin.model.search.dto.VideoSearchSuggestDTO;
import com.niuyin.model.search.enums.VideoSearchScreenPublishTime;
import com.niuyin.model.search.vo.app.AppVideoSearchVO;
import com.niuyin.model.video.domain.Video;
import com.niuyin.model.video.domain.VideoTag;
import com.niuyin.model.video.vo.Author;
import com.niuyin.service.search.constant.ESIndexConstants;
import com.niuyin.service.search.constant.VideoHotTitleCacheConstants;
import com.niuyin.service.search.domain.VideoSearchHistory;
import com.niuyin.service.search.service.VideoSearchService;
import com.niuyin.model.search.dto.VideoSearchKeywordDTO;
import com.niuyin.service.search.domain.VideoSearchVO;
import com.niuyin.service.search.service.VideoSearchHistoryService;
import lombok.SneakyThrows;
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
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 视频同步新增到es
     */
    @Override
    public void videoSync(String videoId) {
        Video video = dubboVideoService.apiGetVideoByVideoId(videoId);
        VideoSearchVO searchVO = new VideoSearchVO();
        searchVO.setVideoId(video.getVideoId());
        searchVO.setVideoTitle(video.getVideoTitle());
        searchVO.setPublishTime(Date.from(video.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
        searchVO.setCoverImage(video.getCoverImage());
        searchVO.setVideoUrl(video.getVideoUrl());
        searchVO.setPublishType(video.getPublishType());
        searchVO.setUserId(video.getUserId());
        searchVO.setTags(dubboVideoService.apiGetVideoTagStack(videoId).stream().map(VideoTag::getTag).toArray(String[]::new));

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
        if (StringUtils.isEmpty(dto.getKeyword())) {
            return new ArrayList<>();
        }
        // 保存搜索记录到mongodb
        Long userId = UserContext.getUserId();
        if (StringUtils.isNotNull(userId) && dto.getFromIndex() == 0) {
            videoSearchHistoryService.insert(dto.getKeyword(), userId);
        }
        // 构建查询请求
        String publishTimeLimit = dto.getPublishTimeLimit();
        if (StringUtils.isNotNull(publishTimeLimit) && !publishTimeLimit.equals(VideoSearchScreenPublishTime.NO_LIMIT.getCode())) {
            long dayStartLong = DateUtils.getTodayPlusStartLocalLong(-Objects.requireNonNull(VideoSearchScreenPublishTime.findByCode(publishTimeLimit)).getLimit());
            dto.setMinBehotTime(new Date(dayStartLong));
        }
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
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(videoSearchKeywordDTO.getKeyword(), VideoSearchVO.VIDEO_TITLE, VideoSearchVO.TAGS);
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
                .field(VideoSearchVO.TAGS)
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
            HighlightField tagsHighlightField = highlightFields.get(VideoSearchVO.TAGS);
            // 处理高亮显示的片段
            String highlightedTitle = getHighlightedText(titleHighlightField);
            String[] highlightedTags = getHighlightedTags(tagsHighlightField);
            // 结果封装
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            VideoSearchVO searchVO = JSON.parseObject(JSON.toJSONString(sourceMap), VideoSearchVO.class);
            searchVO.setVideoTitle(highlightedTitle.isEmpty() ? searchVO.getVideoTitle() : highlightedTitle);
            if (highlightedTags.length > 0) {
                String[] tags = searchVO.getTags();
                for (int i = 0; i < tags.length; i++) {
                    String originalTag = tags[i];
                    for (String highlightedTag : highlightedTags) {
                        String replacedTag = highlightedTag.replace(Highlight_preTags, "").replace(Highlight_postTags, "");
                        if (originalTag.equals(replacedTag)) {
                            tags[i] = highlightedTag;
                            break;
                        }
                    }
                }
                searchVO.setTags(tags);
            }
            res.add(searchVO);
        }
        return res;
    }

    private String getHighlightedText(HighlightField field) {
        if (field != null) {
            Text[] fragments = field.fragments();
            StringBuilder highlightedText = new StringBuilder();
            for (Text fragment : fragments) {
                highlightedText.append(fragment.string());
            }
            return highlightedText.toString();
        }
        return "";
    }

    private String[] getHighlightedTags(HighlightField field) {
        if (field != null) {
            Text[] fragments = field.fragments();
            String[] highlightedTags = new String[fragments.length];
            for (int i = 0; i < fragments.length; i++) {
                highlightedTags[i] = fragments[i].string();
            }
            return highlightedTags;
        }
        return new String[0];
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

    /**
     * 视频搜索推荐
     *
     * @param videoSearchSuggestDTO
     * @return
     */
    @SneakyThrows
    @Override
    public List<String> pushVideoSearchSuggest(VideoSearchSuggestDTO videoSearchSuggestDTO) {
        String keyword = videoSearchSuggestDTO.getKeyword();
        if (StringUtils.isEmpty(keyword)) {
            return new ArrayList<>();
        }
        AnalyzeRequest request = AnalyzeRequest.withGlobalAnalyzer("ik_smart", keyword);
        Set<String> keywordRes = new HashSet<>();
        AnalyzeResponse response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
        response.getTokens().forEach(token -> {
            String term = token.getTerm();
            keywordRes.add(term);
        });
        // 构建模糊查询条件
        Criteria criteria = Criteria.where("keyword").regex(String.join("|", keywordRes), "i"); // "i" 表示不区分大小写

        // 构建聚合管道
        MatchOperation matchOperation = Aggregation.match(criteria);
        GroupOperation groupOperation = Aggregation.group("keyword").first("$$ROOT").as("doc");
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "doc.createdTime"));
        LimitOperation limitOperation = Aggregation.limit(10);
        SampleOperation sampleOperation = Aggregation.sample(10);

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation, sortOperation, limitOperation, sampleOperation);

        // 执行聚合查询
        List<VideoSearchHistory> videoSearchHistory = mongoTemplate.aggregate(aggregation, "video_search_history", VideoSearchHistory.class).getMappedResults();
        // 结果集添加高亮标签

        return videoSearchHistory.stream().map(VideoSearchHistory::getId).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public PageDataInfo searchVideoFromESForApp(VideoSearchKeywordDTO dto) {
        if (StringUtils.isEmpty(dto.getKeyword())) {
            return PageDataInfo.emptyPage();
        }
        // 保存搜索记录到 mongodb
        Long userId = UserContext.getUserId();
        if (StringUtils.isNotNull(userId) && userId != 0L && dto.getFromIndex() == 0) {
            videoSearchHistoryService.insertPlatform(userId, dto.getKeyword(), VideoPlatformEnum.APP);
        }
        // 构建查询请求
        String publishTimeLimit = dto.getPublishTimeLimit();
        if (StringUtils.isNotNull(publishTimeLimit) && !publishTimeLimit.equals(VideoSearchScreenPublishTime.NO_LIMIT.getCode())) {
            long dayStartLong = DateUtils.getTodayPlusStartLocalLong(-Objects.requireNonNull(VideoSearchScreenPublishTime.findByCode(publishTimeLimit)).getLimit());
            dto.setMinBehotTime(new Date(dayStartLong));
        }
        SearchRequest searchRequest = buildAppVideoSearchRequest(dto);
        // 执行查询请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 处理搜索结果
        return processAppVideoSearchResponse(searchResponse);
    }

    /**
     * 构建app视频搜索请求（仅搜索视频标题
     *
     * @param videoSearchKeywordDTO
     * @return
     */
    private SearchRequest buildAppVideoSearchRequest(VideoSearchKeywordDTO videoSearchKeywordDTO) {
        SearchRequest searchRequest = new SearchRequest(ESIndexConstants.INDEX_VIDEO);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 设置分页
        searchSourceBuilder.from((videoSearchKeywordDTO.getPageNum() - 1) * videoSearchKeywordDTO.getPageSize());
        searchSourceBuilder.size(videoSearchKeywordDTO.getPageSize());

        // 构建查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 构建多字段匹配查询
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(videoSearchKeywordDTO.getKeyword(), VideoSearchVO.VIDEO_TITLE, VideoSearchVO.TAGS);
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
                .field(VideoSearchVO.TAGS)
                .preTags(Highlight_preTags_RED)
                .postTags(Highlight_postTags_RED);
        searchSourceBuilder.highlighter(highlightBuilder);

        // 设置排序
        ScoreSortBuilder scoreSortField = SortBuilders.scoreSort().order(SortOrder.DESC);
        FieldSortBuilder publishTimeSortField = SortBuilders.fieldSort(VideoSearchVO.PUBLISH_TIME).order(SortOrder.DESC).sortMode(SortMode.MAX);
        searchSourceBuilder.sort(scoreSortField);
        searchSourceBuilder.sort(publishTimeSortField);

        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

    private PageDataInfo processAppVideoSearchResponse(SearchResponse searchResponse) {
        // 处理搜索结果
        long totalValue = searchResponse.getHits().getTotalHits().value;
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<VideoSearchVO> originVo = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 处理每个搜索结果

            // 获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField titleHighlightField = highlightFields.get(VideoSearchVO.VIDEO_TITLE);
            HighlightField tagsHighlightField = highlightFields.get(VideoSearchVO.TAGS);
            // 处理高亮显示的片段
            String highlightedTitle = getHighlightedText(titleHighlightField);
            String[] highlightedTags = getHighlightedTags(tagsHighlightField);
            // 结果封装
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            VideoSearchVO searchVO = JSON.parseObject(JSON.toJSONString(sourceMap), VideoSearchVO.class);
            searchVO.setVideoTitle(highlightedTitle.isEmpty() ? searchVO.getVideoTitle() : highlightedTitle);
            if (highlightedTags.length > 0) {
                String[] tags = searchVO.getTags();
                for (int i = 0; i < tags.length; i++) {
                    String originalTag = tags[i];
                    for (String highlightedTag : highlightedTags) {
                        String replacedTag = highlightedTag.replace(Highlight_preTags_RED, "").replace(Highlight_postTags_RED, "");
                        if (originalTag.equals(replacedTag)) {
                            tags[i] = highlightedTag;
                            break;
                        }
                    }
                }
                searchVO.setTags(tags);
            }
            originVo.add(searchVO);
        }
        if (originVo.isEmpty()) {
            return PageDataInfo.emptyPage();
        }
        List<AppVideoSearchVO> res = BeanCopyUtils.copyBeanList(originVo, AppVideoSearchVO.class);
        // 封装用户，视频点赞量，喜欢量。。。
        res.forEach(v -> {
            // 作者
            Member member = dubboMemberService.apiGetById(v.getUserId());
            Author author = BeanCopyUtils.copyBean(member, Author.class);
            v.setAuthor(author);
            v.setCreateTime(DateUtils.date2LocalDateTime(v.getPublishTime()));
            v.setPublishTime(null);
            Integer cacheViewNum = redisService.getCacheMapValue("video:view:num", v.getVideoId());
            v.setViewNum(StringUtils.isNull(cacheViewNum) ? 0L : cacheViewNum);
        });
        return PageDataInfo.genPageData(res, totalValue);
    }

}
