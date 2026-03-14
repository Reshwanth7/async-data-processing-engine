package com.reshwanth.analytics.domain;

import com.reshwanth.analytics.dto.ProductSummaryDTO;

public record ProductAggregateStats(
        long totalProducts,
        double averagePrice,
        double averageRating,
        double minPrice,
        double maxPrice,
        long premiumCount,
        ProductSummaryDTO topProduct
) {}

