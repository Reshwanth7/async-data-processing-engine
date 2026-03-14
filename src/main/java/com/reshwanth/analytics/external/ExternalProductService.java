package com.reshwanth.analytics.external;

import com.reshwanth.analytics.dto.AnalyticsData;
import com.reshwanth.analytics.dto.EnrichmentData;
import com.reshwanth.analytics.domain.Product;
import com.reshwanth.analytics.util.EngineConstants;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ExternalProductService {

    public static Product fetchDetails(int productId) {
        try {
            Thread.sleep(EngineConstants.ExternalService.FETCH_DETAILS_DELAY_MILLIS); // simulate network delay
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new Product(
                productId,
                EngineConstants.ExternalService.SAMPLE_PRODUCT_PREFIX + productId,
                EngineConstants.Categories.SAMPLE_DESCRIPTION_PREFIX + productId,
                EngineConstants.ExternalService.DEFAULT_PRICE,
                EngineConstants.ExternalService.DEFAULT_RATING,
                LocalDate.now(),
                EngineConstants.ExternalService.DEFAULT_TAGS
        );
    }

    public static Product fetchProductDetails(int productId) {
        try {
            Thread.sleep(EngineConstants.ExternalService.FETCH_DETAILS_DELAY_MILLIS); // simulate network delay
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return new Product(
                productId,
                EngineConstants.ExternalService.SAMPLE_PRODUCT_PREFIX + productId,
                EngineConstants.Categories.SAMPLE_DESCRIPTION_PREFIX + productId,
                EngineConstants.ExternalService.MAX_PRICE,
                EngineConstants.ExternalService.MAX_RATING,
                LocalDate.now(),
                EngineConstants.ExternalService.DEFAULT_TAGS
        );
    }

    public static CompletableFuture<Product> fetchCompletableProductDetails(int productId) {
        try {
            Thread.sleep(EngineConstants.ExternalService.FETCH_DETAILS_DELAY_MILLIS); // simulate network delay
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return  CompletableFuture.supplyAsync(() -> new Product(productId,
                EngineConstants.ExternalService.SAMPLE_PRODUCT_PREFIX + productId,
                EngineConstants.Categories.SAMPLE_DESCRIPTION_PREFIX + productId,
                EngineConstants.ExternalService.MAX_PRICE,
                EngineConstants.ExternalService.MAX_RATING,
                LocalDate.now(),
                EngineConstants.ExternalService.DEFAULT_TAGS));
    }

    public static double fetchPrice(int productId) {
        sleep(EngineConstants.ExternalService.FETCH_PRICE_DELAY_MILLIS);
        return EngineConstants.ExternalService.ENRICHED_DEFAULT_PRICE;
    }

    public static double fetchRating(int productId) {
        sleep(EngineConstants.ExternalService.FETCH_RATING_DELAY_MILLIS);
        return EngineConstants.ExternalService.ENRICHED_DEFAULT_RATING;
    }

    public static List<String> fetchTags(int productId) {
        sleep(EngineConstants.ExternalService.FETCH_TAGS_DELAY_MILLIS);
        return EngineConstants.ExternalService.ENRICHED_TAGS;
    }

    public static String fetchMetadata(int productId) {
        sleep(EngineConstants.ExternalService.FETCH_METADATA_DELAY_MILLIS);
        return EngineConstants.ExternalService.METADATA_MESSAGE;
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (Exception ignored) {}
    }

    public static Product transformProduct(Product p) {
        return new Product(
                p.productId(),
                p.productName().toUpperCase(),
                p.category().trim(),
                p.price(),
                p.rating(),
                p.addedDate(),
                p.productTags().stream().sorted().toList()
        );
    }

    public static AnalyticsData computeAnalytics(Product p, EnrichmentData e) {
        double popularityScore = e.rating() * e.tags().size();
        double qualityIndex = (e.rating() + p.rating()) / 2.0;
        double weightedRating = (e.rating() * 0.7) + (p.rating() * 0.3);
        double priceValueRatio = (e.price() == 0.0) ? 0.0 : weightedRating / e.price();

        return new AnalyticsData(
                popularityScore,
                qualityIndex,
                weightedRating,
                priceValueRatio
        );
    }
}
