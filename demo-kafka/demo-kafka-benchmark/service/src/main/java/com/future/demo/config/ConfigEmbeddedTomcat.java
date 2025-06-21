package com.future.demo.config;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigEmbeddedTomcat implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> {
            AbstractHttp11Protocol protocol = (AbstractHttp11Protocol) connector.getProtocolHandler();
            connector.setEnableLookups(false);

            // keepAliveTimeout属性指定当前Connector在关闭连接前，等待另一个HTTP请求来到的时间毫秒数
            // 默认值与connectionTimeout属性值相同。-1代表没有超时时间。
            // 单位毫秒
            protocol.setKeepAliveTimeout(60000);

            // 表示一个keepalive连接传输的HTTP请求数达到该数值后，Tomcat服务器会关闭该连接。
            // 将该属性值设置为1代表禁用HTTP/1.0及HTTP/1.1的keep-alive（即禁用持久化连接）
            protocol.setMaxKeepAliveRequests(100000);

            protocol.setUseKeepAliveResponseHeader(true);
        });
    }
}
