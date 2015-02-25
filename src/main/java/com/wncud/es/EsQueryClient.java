package com.wncud.es;

import com.sun.tools.doclets.internal.toolkit.builders.FieldBuilder;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram;
import org.elasticsearch.search.aggregations.metrics.MetricsAggregationBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by cdyf on 2014/12/30.
 */
public class EsQueryClient {

    private Client client;

    @Before
    public void init(){
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "squirrel_es").build();
        client = new TransportClient(settings).addTransportAddress(
                new InetSocketTransportAddress("192.168.69.43", 9300)
        );
    }

    @Test
    public void testQueryCount(){
        CountResponse response = client.prepareCount("jerp_action_log")
                .setQuery(QueryBuilders.termQuery("category", "mq.sync"))
                .execute().actionGet();
        System.out.println("count:" + response.getCount());
    }

    @Test
    public void testQueryAggregation(){
        SearchResponse response = client.prepareSearch("jerp_action_log")
                .addAggregation(AggregationBuilders.dateHistogram("date_histogram")
                                .field("@timestamp")
                                .interval(DateHistogram.Interval.DAY)
                ).execute().actionGet();
        Map<String, Aggregation> result = response.getAggregations().getAsMap();
        System.out.println( response.getHits().getTotalHits());
        for(Iterator<String> iterator = result.keySet().iterator(); iterator.hasNext();){
            String key = iterator.next();
            DateHistogram aggregation = (DateHistogram) result.get(key);
            long count = 0;
            for(Iterator<DateHistogram.Bucket> iteratorXX = (Iterator<DateHistogram.Bucket>) aggregation.getBuckets().iterator();iteratorXX.hasNext();){
                DateHistogram.Bucket bucket = iteratorXX.next();
                count += bucket.getDocCount();
                System.out.println("key:" + bucket.getKey() + ", count:" + bucket.getDocCount());
            }
            System.out.println("sum:" + count);
        }
    }

    @Test
    public void testMetricsAggregation(){
        SearchResponse response = client.prepareSearch("jerp_action_log")
                .addAggregation(AggregationBuilders.dateHistogram("by_xx")
                        .field("@timestamp")
                        .interval(DateHistogram.Interval.WEEK))
                .execute().actionGet();
        Map<String, Aggregation> result = response.getAggregations().getAsMap();
        System.out.println( response.getHits().getTotalHits());
        for(Iterator<String> iterator = result.keySet().iterator(); iterator.hasNext();){
            String key = iterator.next();
            DateHistogram aggregation = (DateHistogram) result.get(key);
            long count = 0;
            for(Iterator<DateHistogram.Bucket> iteratorXX = (Iterator<DateHistogram.Bucket>) aggregation.getBuckets().iterator();iteratorXX.hasNext();){
                DateHistogram.Bucket bucket = iteratorXX.next();
                count += bucket.getDocCount();
                System.out.println("key:" + bucket.getKey() + ", count:" + bucket.getDocCount());
            }
            System.out.println("sum:" + count);
        }
    }

    @Test
    public void testQueryFilter(){
        String query = "host:backend-rd-49-92";
        SearchRequestBuilder requestBuilder = client.prepareSearch("jerp_action")
                /*.setPostFilter(
                        FilterBuilders.andFilter(
                                FilterBuilders.queryFilter(
                                        QueryBuilders.queryString(query)
                                ),
                                FilterBuilders.rangeFilter("timestamp")
                                        .from("2014-12-01")
                                        .to("2014-12-31")
                        )
                )*/
                .setQuery(QueryBuilders.filteredQuery(
                        QueryBuilders.queryString(query),
                        FilterBuilders.rangeFilter("timestamp")
                                .from("2014-12-01")
                                .to("2014-12-31")
                ))
                .addAggregation(AggregationBuilders.dateHistogram("by_day_data")
                        .field("timestamp")
                        .interval(DateHistogram.Interval.DAY));
        requestBuilder.addSort("timestamp", SortOrder.DESC);
        SearchResponse response = requestBuilder.setExplain(true).setFrom(1).setSize(2).execute().actionGet();
        Map<String, Aggregation> result = response.getAggregations().getAsMap();
        System.out.println( response.getHits().getTotalHits());
        for(Iterator<String> iterator = result.keySet().iterator(); iterator.hasNext();){
            String key = iterator.next();
            DateHistogram aggregation = (DateHistogram) result.get(key);
            long count = 0;
            for(Iterator<DateHistogram.Bucket> iteratorXX = (Iterator<DateHistogram.Bucket>) aggregation.getBuckets().iterator();iteratorXX.hasNext();){
                DateHistogram.Bucket bucket = iteratorXX.next();
                count += bucket.getDocCount();
                System.out.println("key:" + bucket.getKey() + ", count:" + bucket.getDocCount());
            }
            System.out.println("sum:" + count);
        }
    }

    @Test
    public void testHighlightQuery(){
        SearchRequestBuilder requestBuilder = client.prepareSearch("filelog_test");

        HighlightBuilder.Field field = new HighlightBuilder.Field("message");
        //field.
        requestBuilder.addHighlightedField(field).addHighlightedField("name");
        //requestBuilder.setHighlighterForceSource(false);
        requestBuilder.setHighlighterFragmentSize(Integer.MAX_VALUE);
        String query = "周*";
        requestBuilder.setQuery(QueryBuilders.queryString(query));
        SearchResponse response = requestBuilder.execute().actionGet();
        SearchHits hits = response.getHits();
        for(SearchHit hit : hits.getHits()){
            Map<String, HighlightField> item = hit.getHighlightFields();
            Map<String, Object> data = hit.getSource();
            for(Iterator<String> iterator = item.keySet().iterator(); iterator.hasNext();){
                String key = iterator.next();
                HighlightField val = item.get(key);
                System.out.println(val.getName());
                String txt = StringUtils.join(val.getFragments());
                data.put(val.getName(), txt);
                System.out.println(txt);
            }
        }
        client.close();
    }

    @After
    public void close(){
        if(client != null){
            client.close();
        }
    }
}
