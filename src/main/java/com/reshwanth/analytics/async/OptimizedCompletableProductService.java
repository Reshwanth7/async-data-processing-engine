package com.reshwanth.analytics.async;

import com.reshwanth.analytics.config.CustomThreadPool;
import com.reshwanth.analytics.dto.AnalyticsData;
import com.reshwanth.analytics.dto.AsyncEnrichedAnalyticsDTO;
import com.reshwanth.analytics.dto.EnrichmentData;
import com.reshwanth.analytics.domain.Product;
import com.reshwanth.analytics.external.ExternalProductService;
import com.reshwanth.analytics.util.EngineConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class OptimizedCompletableProductService {


    public CompletableFuture<EnrichmentData> fetchEnrichedProductAsync(int productId) {

        CompletableFuture<Double> priceFuture =
                CompletableFuture.supplyAsync(() -> ExternalProductService.fetchPrice(productId), CustomThreadPool.executor);

        CompletableFuture<Double> ratingFuture =
                CompletableFuture.supplyAsync(() -> ExternalProductService.fetchRating(productId), CustomThreadPool.executor);

        CompletableFuture<List<String>> tagsFuture =
                CompletableFuture.supplyAsync(() -> ExternalProductService.fetchTags(productId), CustomThreadPool.executor);

        CompletableFuture<String> metadataFuture =
                CompletableFuture.supplyAsync(() -> ExternalProductService.fetchMetadata(productId), CustomThreadPool.executor);

        return CompletableFuture.allOf(priceFuture, ratingFuture, tagsFuture, metadataFuture)
                .thenApplyAsync(v -> new EnrichmentData(
                        priceFuture.join(),
                        ratingFuture.join(),
                        tagsFuture.join(),
                        metadataFuture.join()
                ),CustomThreadPool.executor)
                .orTimeout(EngineConstants.Async.ENRICH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .exceptionallyAsync(ex -> new EnrichmentData(
                                EngineConstants.Thresholds.MIN_VALID_VALUE,
                                EngineConstants.Thresholds.MIN_VALID_VALUE,
                                List.of(),
                                EngineConstants.Async.TIMEOUT_ERROR_METADATA
                ),CustomThreadPool.executor);

    }

    public CompletableFuture<Product> fetchProductAsync(int productId)
    {
        return CompletableFuture.supplyAsync(()->ExternalProductService.fetchDetails(productId),CustomThreadPool.executor)
                .orTimeout(EngineConstants.Async.ENRICH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .exceptionallyAsync(ex -> new Product(0, EngineConstants.Categories.TIMEOUT, EngineConstants.Categories.TIMEOUT,
                        EngineConstants.Thresholds.MIN_VALID_VALUE, EngineConstants.Thresholds.MIN_VALID_VALUE,
                        LocalDate.now(), List.of()),CustomThreadPool.executor);

    }


    public CompletableFuture<Product> transformProductAsync(Product p) {
        return CompletableFuture.supplyAsync(() -> ExternalProductService.transformProduct(p), CustomThreadPool.executor);
    }

    public CompletableFuture<AnalyticsData> computeAnalyticsAsync(Product p, EnrichmentData e) {
        return CompletableFuture.supplyAsync(() -> ExternalProductService.computeAnalytics(p, e), CustomThreadPool.executor);
    }


    public CompletableFuture<AsyncEnrichedAnalyticsDTO> buildAsyncPipeline(int productId) {

        CompletableFuture<Product> productFuture = fetchProductAsync(productId);
        CompletableFuture<EnrichmentData> enrichmentFuture = fetchEnrichedProductAsync(productId);

        CompletableFuture<Product> transformedProductFuture =
                productFuture.thenCompose(this::transformProductAsync);

        CompletableFuture<AnalyticsData> analyticsFuture =
                productFuture.thenCombine(
                        enrichmentFuture,
                        this::computeAnalyticsAsync
                ).thenCompose(f->f);

        return CompletableFuture.allOf(transformedProductFuture, enrichmentFuture, analyticsFuture)
                .thenApply(v -> {
                    Product p = transformedProductFuture.join();
                    EnrichmentData e = enrichmentFuture.join();
                    AnalyticsData a = analyticsFuture.join();

                    return new AsyncEnrichedAnalyticsDTO(
                            p.productId(),
                            p.productName(),
                            p.category(),
                            e.price(),
                            e.rating(),
                            e.tags(),
                            e.metadata(),
                            a.popularityScore(),
                            a.qualityIndex(),
                            a.weightedRating(),
                            a.priceValueRatio(),
                            LocalDateTime.now()
                    );
                })
                .orTimeout(1_000, TimeUnit.MILLISECONDS)
                .exceptionally(ex -> new AsyncEnrichedAnalyticsDTO(
                        productId,
                        "ERROR",
                        "ERROR",
                        0.0,
                        0.0,
                        List.of(),
                        "PIPELINE_FAILED",
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        LocalDateTime.now()
                ));
    }

}
