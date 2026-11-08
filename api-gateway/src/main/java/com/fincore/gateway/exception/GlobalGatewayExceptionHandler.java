
package com.fincore.gateway.exception;

import com.fincore.gateway.dto.ApiResponse;

import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalGatewayExceptionHandler
        implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange,
            Throwable exception) {

        HttpStatus status = determineStatus(exception);

        String message = status == HttpStatus.SERVICE_UNAVAILABLE
                ? "A downstream service is currently unavailable."
                : "An unexpected gateway error occurred.";

        ApiResponse<Void> apiResponse = ApiResponse.error(
                message,
                exchange.getRequest().getURI().getRawPath(),
                null
        );

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        byte[] responseBytes = objectMapper.writeValueAsBytes(apiResponse);

        return exchange.getResponse().writeWith(
                Mono.fromSupplier(() ->
                        exchange.getResponse()
                                .bufferFactory()
                                .wrap(responseBytes)
                )
        );
    }

    private HttpStatus determineStatus(Throwable exception) {

        Throwable current = exception;

        while (current != null) {

            if (current instanceof ConnectException
                    || current instanceof SocketTimeoutException) {
                return HttpStatus.SERVICE_UNAVAILABLE;
            }

            if (current instanceof org.springframework.web.reactive.function.client.WebClientResponseException responseException) {
                return HttpStatus.valueOf(responseException.getStatusCode().value());
            }


            if (current instanceof ResponseStatusException responseException) {
                return HttpStatus.valueOf(
                        responseException.getStatusCode().value()
                );
            }

            current = current.getCause();
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
