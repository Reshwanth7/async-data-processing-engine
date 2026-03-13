package com.reshwanth.engine.DTO;

public record ProductAggregateStats(
        long totalProducts,
        double averagePrice,
        double averageRating,
        double minPrice,
        double maxPrice,
        long premiumCount,
        ProductSummaryDTO topProduct
) {}

