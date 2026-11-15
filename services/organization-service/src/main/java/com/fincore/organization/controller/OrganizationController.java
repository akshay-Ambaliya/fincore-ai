
package com.fincore.organization.controller;

import com.fincore.organization.dto.request.AddOrganizationMemberRequest;
import com.fincore.organization.dto.request.CreateOrganizationRequest;
import com.fincore.organization.dto.response.ApiResponse;
import com.fincore.organization.dto.response.OrganizationMemberResponse;
import com.fincore.organization.dto.response.OrganizationResponse;
import com.fincore.organization.service.OrganizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getMyOrganizations(
            @RequestParam("userId") Long userId,
            HttpServletRequest httpRequest
    ) {

        List<OrganizationResponse> response =
                organizationService.getMyOrganizations(userId);

        ApiResponse<List<OrganizationResponse>> apiResponse =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Organizations fetched successfully",
                        httpRequest.getRequestURI(),
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }


    @PostMapping("/{organizationId}/members")
    public ResponseEntity<ApiResponse<OrganizationMemberResponse>>
    addOrganizationMember(
            @PathVariable("organizationId") Long organizationId,
            @RequestParam("requesterUserId") Long requesterUserId,
            @Valid @RequestBody AddOrganizationMemberRequest request,
            HttpServletRequest httpRequest) {

        OrganizationMemberResponse response =
                organizationService.addOrganizationMember(
                        organizationId,
                        requesterUserId,
                        request
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Organization member added successfully",
                        httpRequest.getRequestURI(),
                        response
                ));
    }


}
