
package com.fincore.organization.client.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class IdentityServiceFeignConfig {

    @Bean
    public RequestInterceptor internalServiceKeyInterceptor(
            @Value("${internal.service.api-key}") String apiKey) {

        return requestTemplate ->
                requestTemplate.header("X-Internal-Service-Key", apiKey);
    }
}
