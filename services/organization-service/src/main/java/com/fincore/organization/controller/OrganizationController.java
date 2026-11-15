
package com.fincore.organization.controller;

import com.fincore.organization.dto.request.CreateOrganizationRequest;
import com.fincore.organization.dto.response.ApiResponse;
import com.fincore.organization.dto.response.OrganizationResponse;
import com.fincore.organization.service.OrganizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request,
            @RequestParam("userId") Long userId,
            HttpServletRequest httpRequest
    ) {

        OrganizationResponse response =
                organizationService.createOrganization(request, userId);

        ApiResponse<OrganizationResponse> apiResponse =
                ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Organization created successfully",
                        httpRequest.getRequestURI(),
                        response
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }


    @GetMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganizationById(
            @PathVariable("organizationId") Long organizationId,
            @RequestParam("userId") Long userId,
            HttpServletRequest httpRequest
    ) {

        OrganizationResponse response =
                organizationService.getOrganizationById(
                        organizationId,
                        userId
                );

        ApiResponse<OrganizationResponse> apiResponse =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Organization fetched successfully",
                        httpRequest.getRequestURI(),
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }

}
