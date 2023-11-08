package com.qiniu.service.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.qiniu.common.context.UserContext;
import com.qiniu.common.exception.CustomException;
import com.qiniu.common.utils.string.StringUtils;
import com.qiniu.model.search.dto.VideoSearchKeywordDTO;
import com.qiniu.service.search.domain.VideoSearchVO;
import com.qiniu.service.search.service.VideoSearchHistoryService;
import com.qiniu.service.search.service.VideoSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
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
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.qiniu.service.search.constant.ESIndexConstants.INDEX_VIDEO;

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

    /**
     * 视频同步新增到es
     */
    @Override
    public void videoSync(String json) {
        VideoSearchVO videoSearchVO = JSON.parseObject(json, VideoSearchVO.class);
        IndexRequest indexRequest = new IndexRequest(INDEX_VIDEO);
        indexRequest.id(videoSearchVO.getVideoId());
        indexRequest.source(json, XContentType.JSON);
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
        UpdateRequest updateRequest = new UpdateRequest(INDEX_VIDEO, videoSearchVO.getVideoId());
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
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(INDEX_VIDEO);
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
    public List<VideoSearchVO> searchVideoFromES(VideoSearchKeywordDTO dto) throws IOException, InvocationTargetException, IllegalAccessException {
        //1.参数校验
        if (StringUtils.isNull(dto) || StringUtils.isBlank(dto.getKeyword())) {
            return null;
        }
        Long userId = UserContext.getUserId();
        // 异步调用 保存搜索记录
        if (StringUtils.isNotNull(userId) && dto.getFromIndex() == 0) {
            videoSearchHistoryService.insert(dto.getKeyword(), userId);
        }
        //2.设置查询条件
        SearchRequest searchRequest = new SearchRequest(INDEX_VIDEO);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //布尔查询videoTitle或者userNickName
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(dto.getKeyword()).field("videoTitle").field("userNickName").defaultOperator(Operator.OR);
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
            result.add(videoSearchVO);
        }
        result.forEach(System.out::println);
        return result;
    }
}
