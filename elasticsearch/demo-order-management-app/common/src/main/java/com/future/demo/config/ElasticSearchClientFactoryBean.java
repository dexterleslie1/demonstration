package com.future.demo.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class ElasticSearchClientFactoryBean implements FactoryBean<ElasticsearchClient>, InitializingBean, DisposableBean {

    protected ElasticsearchClient client = null;
    private RestClient restClient;
    private ElasticsearchTransport transport;

    @Override
    public ElasticsearchClient getObject() {
        return client;
    }

    @Override
    public Class<?> getObjectType() {
        return ElasticsearchClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        // region 自定义销毁逻辑

        if (transport != null) {
            transport.close();
            transport = null;
        }
        if (restClient != null) {
            restClient.close();
            restClient = null;
        }

        // endregion
    }

    @Override
    public void afterPropertiesSet() {
        // region 自定义 ElasticSearchClient 创建逻辑

        Assert.isNull(client, "意料之外错误，client 不为 null");

        // 1. 创建RestClient（底层HTTP客户端）
        restClient = RestClient.builder(
//                new HttpHost("localhost", 9200, "http") // 单节点配置
                new HttpHost("192.168.1.190", 9200, "http") // 单节点配置
                // 多节点示例：new HttpHost("host2", 9200, "http"), ...
        ).build();

        // 2. 创建Transport层（序列化/反序列化）
        // 支持处理 LocalDateTime
        JacksonJsonpMapper mapper = new JacksonJsonpMapper(new ObjectMapper().registerModule(new JavaTimeModule()));
        transport = new RestClientTransport(
                restClient,
                mapper // 使用Jackson作为JSON处理器
        );

        // 3. 创建ElasticsearchClient
        client = new ElasticsearchClient(transport);

        // endregion
    }
}
