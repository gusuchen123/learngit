package com.imooc.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author gusuchen
 * Created in 2018-01-21 18:17
 * Description: elasticsearch 配置
 * Modified by:
 */
@Configuration
public class ElasticSearchConfig {
    @Value("${elasticsearch.cluster.name}")
    private String esClusterName;

    @Value("${elasticsearch.host}")
    private String esHost;

    @Value("${elasticsearch.port}")
    private int esPort;

    @Bean
    public TransportClient esClient() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", this.esClusterName)
                .put("client.transport.sniff", true)
                .build();

        InetSocketTransportAddress master = new InetSocketTransportAddress(
                InetAddress.getByName(esHost),
                esPort
        );

        TransportClient esClient = new PreBuiltTransportClient(settings)
                .addTransportAddress(master);

        return esClient;
    }
}
