package com.readingtracker.boochive.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    final Path FILE_ROOT = Paths.get("./").normalize().toAbsolutePath();

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/uploads/**").addResourceLocations("file://"+FILE_ROOT+"/uploads/");
    }

    /**
     * Connection Pool 및 Timeout 설정
     */
    @Bean
    public RestTemplate restTemplate() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(10L, TimeUnit.SECONDS) // 커넥션 요청 후 할당 대기시간, 10초
                .setResponseTimeout(30L, TimeUnit.SECONDS) // 응답 대기시간, 30초
                .build();

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(5L, TimeUnit.SECONDS) // 서버 연결 대기시간, 5초
                .setSocketTimeout(30, TimeUnit.SECONDS) // 소켓 응답 대기시간, 30초
                .build();

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(connectionConfig)
                .setMaxConnTotal(100) // 연결 유지할 최대 커넥션 수, 100개
                .setMaxConnPerRoute(50) // 각 (IP:PORT)당 최대 커넥션 수, 50개
                .build();

        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictIdleConnections(TimeValue.ofSeconds(60)) // 유휴 커넥션 최대 유지시간, 60초 (해당 시간이 지나면 커넥션 해제)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        return new RestTemplate(requestFactory);
    }
}
