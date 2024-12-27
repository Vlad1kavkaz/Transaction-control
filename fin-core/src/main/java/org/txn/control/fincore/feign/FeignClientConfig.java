package org.txn.control.fincore.feign;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class FeignClientConfig {

    @Value("${spring.feign-client.period}")
    private int period;

    @Value("${spring.feign-client.max-period}")
    private int maxPeriod;

    @Value("${spring.feign-client.max-attempts}")
    private int maxAttempts;

    @Bean
    public Retryer clearingFinCoreRetryer() {
        return new FinCoreRetryer(period, maxPeriod, maxAttempts);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FinCoreErrorDecoder();
    }
}
