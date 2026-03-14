package com.reshwanth.analytics.async;

import com.reshwanth.analytics.domain.CategoryPriceStats;
import com.reshwanth.analytics.domain.ProductAggregateStats;
import com.reshwanth.analytics.domain.ProductDashboardSummary;
import com.reshwanth.analytics.dto.ProductSummaryDTO;
import com.reshwanth.analytics.domain.Product;
import com.reshwanth.analytics.external.ExternalProductService;
import com.reshwanth.analytics.service.ProductAnalyticsService;
import com.reshwanth.analytics.util.EngineConstants;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.reshwanth.analytics.util.ProductAnalyticsAccumulator.productStatsCollector;

public class OptimizedProductService {
    //Day 5
    Predicate<Product> productPredicate = product -> product.price() >= EngineConstants.Thresholds.SUMMARY_MIN_PRICE
            && product.rating() >= EngineConstants.Thresholds.SUMMARY_MIN_RATING
            && !EngineConstants.Categories.GROCERY.equals(product.category())
           && product.productName() != null && !product.productName().isEmpty()
            && product.productTags() != null && !product.productTags().isEmpty() && product.addedDate() != null;

    Comparator<Product> productComparator = (Comparator.comparing(Product::rating).reversed()
            .thenComparing(Product::price)
            .thenComparing(Product::productName));

    Predicate<Product> productFilterPredicate = p -> p.productName() != null && !p.productName().isEmpty()
            && p.price() >= EngineConstants.Thresholds.MIN_VALID_VALUE
            && p.rating() >= EngineConstants.Thresholds.MIN_VALID_VALUE
            && p.addedDate() != null && p.productTags() != null && !p.productTags().isEmpty();


    Predicate<Product> productCategoryPredicate = p -> p.price() >= EngineConstants.Thresholds.MIN_VALID_VALUE
            && p.addedDate() != null &&  p.category() != null && !p.category().isEmpty();

    public static Predicate<Product> isPremium = product -> product.rating() >= EngineConstants.Thresholds.HIGH_RATING
            && product.price() >= EngineConstants.Thresholds.PREMIUM_MIN_PRICE
            && !EngineConstants.Categories.GROCERY.equals(product.category());


    public List<ProductSummaryDTO> productSummaryDTOList(List<Product> productList){

        List<Product> productSafeList = Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList);

        return   productSafeList
                .parallelStream()
                .filter(productPredicate)
                .sorted(productComparator)
                .limit(EngineConstants.Limits.SUMMARY_PRODUCT_COUNT)
                .map(product -> new ProductSummaryDTO(product.productId(),product.productName(),product.category()
                        ,product.price(), product.rating(), product.addedDate().getMonth(),product.productTags().size()))
                .toList();
    }

    public ProductDashboardSummary getProductDashBoard(List<Product> productList) {

        // PASS 1 — Filter once and collect all metrics except top-3
        List<Product> filteredList = Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(productFilterPredicate)
                .toList();

        long totalProducts = filteredList.size();

        DoubleSummaryStatistics priceStats = new DoubleSummaryStatistics();
        DoubleSummaryStatistics ratingStats = new DoubleSummaryStatistics();

        Map<String, Long> categoryCounts = new HashMap<>();
        Map<String, Long> priceBucketCounts = new HashMap<>();

        long premiumProductCount = 0;

        // Single loop — compute everything except top-3
        for (Product p : filteredList) {

            // price + rating stats
            priceStats.accept(p.price());
            ratingStats.accept(p.rating());

            // category counts
            categoryCounts.merge(p.category(), 1L, Long::sum);

            // price bucket counts
            String bucket = ProductAnalyticsService.priceCategory(p);
            priceBucketCounts.merge(bucket, 1L, Long::sum);

            // premium count
            if (isPremium.test(p)) {
                premiumProductCount++;
            }
        }

        double averagePrice = priceStats.getAverage();
        double averageRating = ratingStats.getAverage();

        // most common category
        String mostCommonCategory = categoryCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // PASS 2 — Top 3 rated products (only sort once)
        List<ProductSummaryDTO> topRatedProducts = filteredList.stream()
                .sorted(
                        Comparator.comparing(Product::rating).reversed()
                                .thenComparing(Product::price)
                )
                .limit(EngineConstants.Limits.TOP_RATED_PRODUCT_COUNT)
                .map(p -> new ProductSummaryDTO(
                        p.productId(),
                        p.productName(),
                        p.category(),
                        p.price(),
                        p.rating(),
                        p.addedDate().getMonth(),
                        p.productTags().size()
                ))
                .collect(Collectors.toList());

        return new ProductDashboardSummary(
                totalProducts,
                averagePrice,
                averageRating,
                categoryCounts,
                priceBucketCounts,
                topRatedProducts,
                premiumProductCount,
                mostCommonCategory
        );
    }


    public ConcurrentMap<String, ConcurrentMap<String, CategoryPriceStats>> groupByCategoryOfCategoryStats(List<Product> productList){

        List<Product> filteredList = Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(productCategoryPredicate)
                .toList();

        return filteredList.parallelStream()
                .collect(Collectors.groupingByConcurrent(
                                        Product::category,
                                        Collectors.groupingByConcurrent(ProductAnalyticsService::priceCategory,
                                                Collectors.collectingAndThen(
                                                       Collectors.toList(),
                                                        (List<Product> productBucket)-> {
                                                           // Price stats
                                                           DoubleSummaryStatistics priceStats = productBucket.stream()
                                                                   .collect(Collectors.summarizingDouble(Product::price));
                                                           return new CategoryPriceStats(
                                                                   priceStats.getCount(),
                                                                   priceStats.getMin(),
                                                                   priceStats.getMax(),
                                                                   priceStats.getAverage()
                                                           );
                                                       }
                                                )
                                        )
                ));


    }

    //Custom Collector
    public ProductAggregateStats getAggregateStats(List<Product> productList) {
        List<Product> filteredList = Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(productFilterPredicate)
                .toList();
        return filteredList
                .stream()
                .collect(productStatsCollector());
    }

    //Custom Collector using parallelStreams
    public ProductAggregateStats getAggregateStatsUsingParallelStreams(List<Product> productList) {
        List<Product> filteredList = Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .parallelStream()
                .filter(productFilterPredicate)
                .toList();
        return filteredList
                .stream()
                .collect(productStatsCollector());
    }

    //Day 6
    public void printDetails(String productId) {
         fetchDetailsWithTimeout(productId)
        .thenApply(product ->new Product(product.productId(), product.productName().toUpperCase(),product.category(),
                product.price(),product.rating(),product.addedDate(),product.productTags()))
                .thenAccept(product-> System.out.println("Returning Product for productId: "+productId+" Product is" + product))
                .join(); // waits for result
    }

    public Product getDetails(String productId) {
        return fetchDetailsWithTimeout(productId)
                .thenApply(product ->new Product(product.productId(), product.productName().toUpperCase(),product.category(),
                        product.price(),product.rating(),product.addedDate(),product.productTags()))
                .join(); // waits for result
    }


    public CompletableFuture<Product> fetchDetailsWithTimeout(String productId) {
        return CompletableFuture.supplyAsync(() -> ExternalProductService.fetchDetails(EngineConstants.Async.FIXED_THREAD_POOL_SIZE))
                .orTimeout(EngineConstants.Async.FETCH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .exceptionally(ex -> new Product(0, EngineConstants.Categories.TIMEOUT, EngineConstants.Categories.TIMEOUT,
                        EngineConstants.Thresholds.MIN_VALID_VALUE, EngineConstants.Thresholds.MIN_VALID_VALUE,
                        LocalDate.now(), List.of()));
    }

    public Product getHighestRatingProductDetails(String productId) {
        return fetchDetailsWithTimeoutAndCombine(productId)
                .thenApply(product ->new Product(product.productId(), product.productName().toUpperCase(),product.category(),
                        product.price(),product.rating(),product.addedDate(),product.productTags()))
                .join(); // waits for result
    }

    public CompletableFuture<Product> fetchDetailsWithTimeoutAndCombine(String productId) {
        CompletableFuture<Product> p1 = CompletableFuture.supplyAsync(() -> ExternalProductService.fetchDetails(EngineConstants.Async.FIXED_THREAD_POOL_SIZE));
        CompletableFuture<Product> p2 = CompletableFuture.supplyAsync(() -> ExternalProductService.fetchProductDetails(EngineConstants.Async.FIXED_THREAD_POOL_SIZE));
        return p1
        .exceptionally(ex -> new Product(0, EngineConstants.Categories.TIMEOUT, EngineConstants.Categories.TIMEOUT,
                EngineConstants.Thresholds.MIN_VALID_VALUE, EngineConstants.Thresholds.MIN_VALID_VALUE,
                LocalDate.now(), List.of()))
                .thenCombine(p2,(p,s)->{
                   return p.rating()>s.rating()? p : s;
                })
                .orTimeout(EngineConstants.Async.FETCH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .exceptionally(ex -> new Product(0, EngineConstants.Categories.TIMEOUT, EngineConstants.Categories.TIMEOUT,
                        EngineConstants.Thresholds.MIN_VALID_VALUE, EngineConstants.Thresholds.MIN_VALID_VALUE,
                        LocalDate.now(), List.of()));
    }

    public Product getHighestRatingProductDetailsWithCompose(String productId) {
        return fetchDetailsWithTimeoutAndCompose(productId)
                .thenApply(product ->new Product(product.productId(), product.productName().toUpperCase(),product.category(),
                        product.price(),product.rating(),product.addedDate(),product.productTags()))
                .join(); // waits for result
    }

    //use then compose when a function returns Completable Future
    public CompletableFuture<Product> fetchDetailsWithTimeoutAndCompose(String productId) {
     return CompletableFuture.supplyAsync(() -> ExternalProductService.fetchDetails(EngineConstants.Async.FIXED_THREAD_POOL_SIZE))
             .exceptionally(ex -> new Product(0, EngineConstants.Categories.TIMEOUT, EngineConstants.Categories.TIMEOUT,
                     EngineConstants.Thresholds.MIN_VALID_VALUE, EngineConstants.Thresholds.MIN_VALID_VALUE,
                     LocalDate.now(), List.of()))
        .thenCompose(previous ->ExternalProductService.fetchCompletableProductDetails(previous.productId()))
                .orTimeout(EngineConstants.Async.FETCH_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .exceptionally(ex -> new Product(0, EngineConstants.Categories.TIMEOUT, EngineConstants.Categories.TIMEOUT,
                        EngineConstants.Thresholds.MIN_VALID_VALUE, EngineConstants.Thresholds.MIN_VALID_VALUE,
                        LocalDate.now(), List.of()));
    }



}
