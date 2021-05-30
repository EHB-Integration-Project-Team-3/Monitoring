package com.brielage.monitor.Elastic;

import com.brielage.monitor.XML.Heartbeat;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.HashMap;

@SuppressWarnings({"rawtypes", "unchecked"})
public enum ElasticRequest {
    ;

    private static final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(new HttpHost("10.3.17.75", 9200, "http")));

    public static void sendToElastic(String index, Heartbeat heartbeat)
            throws IOException {
        HashMap hm = new HashMap();

        hm.put("timestamp", heartbeat.getTimeStamp().toGregorianCalendar());
        hm.put("source", heartbeat.getSource().toLowerCase());
        hm.put("status", heartbeat.getStatus());

        send(index, hm);
    }

    private static void send(String index, HashMap hm)
            throws IOException {
        client.index(new IndexRequest(index).source(hm), RequestOptions.DEFAULT);
    }
}
