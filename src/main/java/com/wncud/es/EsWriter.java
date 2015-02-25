package com.wncud.es;

import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by yajunz on 2015/1/30.
 */
public class EsWriter {


    public static void main(String[] args) throws IOException {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                Locale.CHINA);
        sdf.setTimeZone(TimeZone.getDefault());

        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "spoor_es").build();
        TransportClient client =   new TransportClient(settings);
        //TransportClient client =   new TransportClient();
        client.addTransportAddress(new InetSocketTransportAddress("192.168.53.13", 9300));

        String index = "inventory";
        String indexType = "Inventory-Pub";

        IndicesAdminClient adminClient = client.admin().indices();

        GetMappingsResponse response = adminClient.prepareGetMappings(index)
                .setTypes(indexType)
                .execute().actionGet();
        ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> indexMappings = response.mappings();
        indexMappings.get(index);



        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject(indexType)
                .startObject("properties")
                .startObject("test356").field("type", "string").field("index", "analyzed").field("store", "yes")
                .endObject()
                .endObject()
                .endObject()
                .endObject();

        adminClient.preparePutMapping(index)
                .setType(indexType)
                .setSource(
                        mapping
                )
                .execute().actionGet();

        /*BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        XContentBuilder jsonBuilder = XContentFactory.jsonBuilder()
                .startObject();
        //jsonBuilder.field()



        bulkRequestBuilder.add(client.prepareIndex("yajunz", "db", "1")
            .setSource(jsonBuilder)
        );*/
    }
}
