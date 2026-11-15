package com.fincore.organization.service.impl;

import com.fincore.organization.repository.OrganizationRepository;
import com.fincore.organization.service.OrganizationSlugService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class OrganizationSlugServiceImpl implements OrganizationSlugService {

    private final OrganizationRepository organizationRepository;

    @Override
    public String generateUniqueSlug(String organizationName) {

        // Convert organization name into lowercase
        String slug = organizationName.toLowerCase(Locale.ROOT);

        // Normalize special characters and accents
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        // Replace special characters and spaces with hyphens
        slug = slug.replaceAll("[^a-z0-9]+", "-");

        // Remove leading and trailing hyphens
        slug = slug.replaceAll("^-|-$", "");

        // Fallback if the name contains no usable characters
        if (slug.isBlank()) {
            slug = "organization";
        }

        // Ensure slug uniqueness
        String uniqueSlug = slug;
        int counter = 2;

        while (organizationRepository.existsBySlug(uniqueSlug)) {
            uniqueSlug = slug + "-" + counter;
            counter++;
        }

        return uniqueSlug;
    }
}
