
package com.fincore.identity.service;

import com.fincore.identity.dto.response.InternalUserVerificationResponse;

public interface InternalUserVerificationService {

    InternalUserVerificationResponse verifyUser(Long userId);
}
