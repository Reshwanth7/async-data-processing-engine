package com.reshwanth.engine.service;

import com.reshwanth.engine.dto.EnhancedProductDTO;
import com.reshwanth.engine.util.EngineConstants;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OptimizedCompletableProductService {
    private final ExecutorService executor =
            Executors.newFixedThreadPool(EngineConstants.Async.FIXED_THREAD_POOL_SIZE);


    public CompletableFuture<EnhancedProductDTO> fetchEnrichedProduct(int productId) {

        CompletableFuture<Double> priceFuture =
                CompletableFuture.supplyAsync(() -> ExternalProductService.fetchPrice(productId), executor);

        CompletableFuture<Double> ratingFuture =
                CompletableFuture.supplyAsync(() -> ExternalProductService.fetchRating(productId), executor);

        CompletableFuture<List<String>> tagsFuture =
                CompletableFuture.supplyAsync(() -> ExternalProductService.fetchTags(productId), executor);

        CompletableFuture<String> metadataFuture =
                CompletableFuture.supplyAsync(() -> ExternalProductService.fetchMetadata(productId), executor);

        return CompletableFuture.allOf(priceFuture, ratingFuture, tagsFuture, metadataFuture)
                .thenApplyAsync(v -> new EnhancedProductDTO(
                        productId,
                        priceFuture.join(),
                        ratingFuture.join(),
                        tagsFuture.join(),
                        metadataFuture.join()
                ),executor)
                .orTimeout(EngineConstants.Async.ENRICH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .exceptionallyAsync(ex -> new EnhancedProductDTO(
                                productId,
                                EngineConstants.Thresholds.MIN_VALID_VALUE,
                                EngineConstants.Thresholds.MIN_VALID_VALUE,
                                List.of(),
                                EngineConstants.Async.TIMEOUT_ERROR_METADATA
                ),executor);

    }
}
