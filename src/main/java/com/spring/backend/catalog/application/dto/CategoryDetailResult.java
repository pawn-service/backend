package com.spring.backend.catalog.application.dto;

import com.spring.backend.catalog.application.port.out.CreatorLookupPort.CreatorInfo;

/**
 * Category result enriched with creator info. Used by query operations that need to show "who
 * created this".
 */
public record CategoryDetailResult(CategoryResult category, CreatorInfo createdByUser) {}
