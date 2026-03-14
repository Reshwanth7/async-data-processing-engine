package com.reshwanth.analytics.domain;

import com.reshwanth.analytics.dto.ProductSummaryDTO;

import java.util.List;
import java.util.Map;

public record ProductDashboardSummary(
        long totalProducts,
        double averagePrice,
        double averageRating,
        Map<String, Long> categoryCounts,
        Map<String, Long> priceBucketCounts,
        List<ProductSummaryDTO> topRatedProducts,
        long premiumProductCount,
        String mostCommonCategory
) {}
