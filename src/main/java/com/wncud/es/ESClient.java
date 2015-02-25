package com.wncud.es;


import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by yajunz on 2014/12/9.
 */
public class ESClient {
    @Test
    public void testClient(){
        String query = "target:85036820";
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "spoor_es").build();
        //TransportClient client =   new TransportClient(settings);
        TransportClient client =   new TransportClient();
        client.addTransportAddress(new InetSocketTransportAddress("192.168.69.10", 9300));
        try {
            //QueryBuilder queryBuilder = QueryBuilders.queryString(query);
            QueryBuilder queryBuilder = QueryBuilders.queryString("id:7050");
            SearchResponse response = client.prepareSearch("test_201412227")
                    .setQuery(queryBuilder)
                    .setFrom(0)
                    .setSize(60)
                    .setExplain(true).execute().actionGet();

            SearchHits hits = response.getHits();
            for(int i = 0; i < hits.hits().length; i++){
                Map<String, Object> node = hits.getAt(i).getSource();
                //Iterator<String> keys = node.keySet().iterator();
                System.out.println("---------------------------------------------------------------");
                for(Iterator<String> keys = node.keySet().iterator(); keys.hasNext();){
                    String key = keys.next();
                    System.out.print(key + ":" + node.get(key) + ", ");
                }
                System.out.println("\n---------------------------------------------------------------");
            }
        } catch (ElasticsearchException e) {
            e.printStackTrace();
        }
        finally {
            client.close();
        }
    }
}
