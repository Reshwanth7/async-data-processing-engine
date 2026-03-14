package com.reshwanth.analytics.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AsyncEnrichedAnalyticsDTO(
        int productId,
        String productName,
        String category,
        double price,
        double rating,
        List<String> tags,
        String metadata,
        double popularityScore,
        double qualityIndex,
        double weightedRating,
        double priceValueRatio,
        LocalDateTime processedTimestamp
) {}
