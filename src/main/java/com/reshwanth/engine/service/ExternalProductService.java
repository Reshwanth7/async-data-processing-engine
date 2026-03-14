package com.reshwanth.engine.service;

import com.reshwanth.engine.model.Product;
import com.reshwanth.engine.util.EngineConstants;

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
}
