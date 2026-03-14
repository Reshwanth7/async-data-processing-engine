package com.reshwanth.analytics.dto;

import java.util.List;

public record EnrichmentData(
        double price,
        double rating,
        List<String> tags,
        String metadata
) {}
