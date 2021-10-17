package com.liu.elasticsearch.service;

import com.alibaba.fastjson.JSON;
import com.liu.elasticsearch.domain.Content;
import com.liu.elasticsearch.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 王一宁
 * @date 2020/7/28 9:42
 */
@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    // 1.解析数据 放入ES索引库
    public Boolean parseContent(String keywords) throws IOException {
        List<Content> contents = new HtmlParseUtil().parseJD(keywords);
        // 2.查询的数据批量插入ES中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (int i = 0; i < contents.size(); i++) {
            bulkRequest.add(new IndexRequest("jd_goods")
            .source(JSON.toJSONString(contents.get(i)), XContentType.JSON)
            );
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    // 2.实现搜索功能
    public List<Map<String,Object>> searchPage(String keyword,int pageNo, int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo=1;
        }


        // 条件搜索
        SearchRequest jd_goods = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        // 精准查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title",keyword);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 执行
        jd_goods.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(jd_goods, RequestOptions.DEFAULT);

        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit documentFields : response.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }

    // 3.实现搜索高亮功能
    public List<Map<String,Object>> searchHighLightPage(String keyword,int pageNo, int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo=1;
        }


        // 条件搜索
        SearchRequest jd_goods = new SearchRequest("jd_goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        // 精准查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title",keyword);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        // 多个高亮
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);


        // 执行
        jd_goods.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(jd_goods, RequestOptions.DEFAULT);

        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit documentFields : response.getHits().getHits()) {
            // 解析高亮字段
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            // 原来的字段替换为高亮字段
            if (title!=null){
                Text[] fragments = title.fragments();
                String n_title = "";
                for (Text text : fragments){
                    n_title += text;
                }
                sourceAsMap.put("title",n_title);
            }
            list.add(sourceAsMap);
        }
        return list;
    }

}
