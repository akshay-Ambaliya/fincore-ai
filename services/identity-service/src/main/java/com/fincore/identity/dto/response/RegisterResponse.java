package com.fincore.identity.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class RegisterResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Instant createdAt;
}