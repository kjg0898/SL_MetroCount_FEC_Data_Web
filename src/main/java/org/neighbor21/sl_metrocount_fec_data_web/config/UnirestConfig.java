package org.neighbor21.sl_metrocount_fec_data_web.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import jakarta.annotation.PostConstruct;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * packageName    : org.neighbor21.sl_metrocount_fec_data_web.config
 * fileName       : UnirestConfig.java
 * author         : kjg08
 * date           : 2024-07-15
 * description    : Unirest 설정을 분리하여 관리하는 클래스. API 호출 시의 연결 및 소켓 타임아웃을 설정합니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 7.15.        kjg08           최초 생성
 */
@Configuration
public class UnirestConfig {

    @Value("${timeout.connect}")
    private int connectTimeout;

    @Value("${timeout.socket}")
    private int socketTimeout;

    @Bean
    public Retry apiRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(1000))
                .build();
        return Retry.of("apiRetry", config);
    }

    @Bean
    public Retry dbRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(2)
                .waitDuration(Duration.ofMillis(2000))
                .build();
        return Retry.of("dbRetry", config);
    }

    @PostConstruct
    public void init() {
        Unirest.config()
                .connectTimeout(connectTimeout)
                .socketTimeout(socketTimeout);
    }
}
