package com.reshwanth.engine.dto;

import java.util.List;

public record EnhancedProductDTO(
        int productId,
        double price,
        double rating,
        List<String> tags,
        String metadata
) {}