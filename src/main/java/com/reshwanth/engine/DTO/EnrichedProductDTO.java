package com.reshwanth.engine.DTO;

import java.time.Month;

public record EnrichedProductDTO(
        int productId,
        String productName,
        String category,
        double price,
        double rating,
        boolean discountApplied,
        double discountedPrice,
        String priceBucket,
        Month addedMonth,
        int tagCount,
        boolean isPremiumProduct
) {}
