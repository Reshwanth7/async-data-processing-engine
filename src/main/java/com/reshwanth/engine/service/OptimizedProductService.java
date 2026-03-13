package com.reshwanth.engine.service;

import com.reshwanth.engine.DTO.CategoryPriceStats;
import com.reshwanth.engine.DTO.ProductAggregateStats;
import com.reshwanth.engine.DTO.ProductDashboardSummary;
import com.reshwanth.engine.DTO.ProductSummaryDTO;
import com.reshwanth.engine.model.Product;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.reshwanth.engine.util.ProductAnalyticsAccumulator.productStatsCollector;

public class OptimizedProductService {
    //Day 5
    Predicate<Product> productPredicate = product -> product.price() >= 100 && product.rating() >= 4.0 && !"Grocery".equals(product.category())
           && product.productName() != null && !product.productName().isEmpty()
            && product.productTags() != null && !product.productTags().isEmpty() && product.addedDate() != null;

    Comparator<Product> productComparator = (Comparator.comparing(Product::rating).reversed()
            .thenComparing(Product::price)
            .thenComparing(Product::productName));

    Predicate<Product> productFilterPredicate = p -> p.productName() != null && !p.productName().isEmpty()
            && p.price() >= 0 && p.rating() >= 0 && p.addedDate() != null && p.productTags() != null && !p.productTags().isEmpty();


    Predicate<Product> productCategoryPredicate = p -> p.price() >= 0 && p.addedDate() != null &&  p.category() != null && !p.category().isEmpty();

    public static Predicate<Product> isPremium = product -> product.rating()>= 4.5 && product.price()>=300 && !"Grocery".equals(product.category());


    public List<ProductSummaryDTO> productSummaryDTOList(List<Product> productList){

        List<Product> productSafeList = Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList);

        return   productSafeList
                .parallelStream()
                .filter(productPredicate)
                .sorted(productComparator)
                .limit(5)
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
                .limit(3)
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


}
