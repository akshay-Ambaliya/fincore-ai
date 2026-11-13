
package com.fincore.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IdentityServiceApplication {

    public static void main(String[] args) {
        System.out.println(">>> STARTING IDENTITY SERVICE <<<");

        SpringApplication.run(IdentityServiceApplication.class, args);

        System.out.println(">>> IDENTITY SERVICE STARTED <<<");
    }
}
