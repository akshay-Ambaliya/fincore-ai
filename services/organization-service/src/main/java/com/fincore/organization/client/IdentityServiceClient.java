
package com.fincore.organization.client;

import com.fincore.organization.dto.response.IdentityUserVerificationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "identity-service",
    url = "${identity.service.base-url}",
    configuration = com.fincore.organization.client.config.IdentityServiceFeignConfig.class
)
public interface IdentityServiceClient {

    @GetMapping("/internal/users/{userId}/verification")
    IdentityUserVerificationResponse verifyUser(
        @PathVariable("userId") Long userId
    );
}
