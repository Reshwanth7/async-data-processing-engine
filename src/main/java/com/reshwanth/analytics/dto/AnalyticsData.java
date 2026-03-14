package com.reshwanth.analytics.dto;

public record AnalyticsData(
        double popularityScore,
        double qualityIndex,
        double weightedRating,
        double priceValueRatio
) {}
