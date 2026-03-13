package com.reshwanth.engine.util;

import com.reshwanth.engine.DTO.ProductAggregateStats;
import com.reshwanth.engine.DTO.ProductSummaryDTO;
import com.reshwanth.engine.model.Product;
import com.reshwanth.engine.service.OptimizedProductService;

import java.util.stream.Collector;


public class ProductAnalyticsAccumulator {

    long totalProducts = 0;
    double totalPrice = 0;
    double totalRating = 0;
    double minPrice = Double.MAX_VALUE;
    double maxPrice = Double.MIN_VALUE;
    long premiumCount = 0;
    Product topProduct = null;

    void add(Product p) {
        totalProducts++;
        totalPrice += p.price();
        totalRating += p.rating();

        minPrice = Math.min(minPrice, p.price());
        maxPrice = Math.max(maxPrice, p.price());

        if (OptimizedProductService.isPremium.test(p)) {
            premiumCount++;
        }

        if (topProduct == null ||
                p.rating() > topProduct.rating() ||
                (p.rating() == topProduct.rating() && p.price() < topProduct.price())) {
            topProduct = p;
        }
    }

    ProductAnalyticsAccumulator combine(ProductAnalyticsAccumulator other) {
        this.totalProducts += other.totalProducts;
        this.totalPrice += other.totalPrice;
        this.totalRating += other.totalRating;
        this.minPrice = Math.min(this.minPrice, other.minPrice);
        this.maxPrice = Math.max(this.maxPrice, other.maxPrice);
        this.premiumCount += other.premiumCount;

        if (this.topProduct == null ||
                (other.topProduct != null &&
                        (other.topProduct.rating() > this.topProduct.rating() ||
                                (other.topProduct.rating() == this.topProduct.rating()
                                        && other.topProduct.price() < this.topProduct.price())))) {
            this.topProduct = other.topProduct;
        }

        return this;
    }

    ProductAggregateStats finish() {
        double avgPrice = totalProducts == 0 ? 0 : totalPrice / totalProducts;
        double avgRating = totalProducts == 0 ? 0 : totalRating / totalProducts;

        ProductSummaryDTO topDto = topProduct == null ? null :
                new ProductSummaryDTO(
                        topProduct.productId(),
                        topProduct.productName(),
                        topProduct.category(),
                        topProduct.price(),
                        topProduct.rating(),
                        topProduct.addedDate().getMonth(),
                        topProduct.productTags().size()
                );

        return new ProductAggregateStats(
                totalProducts,
                avgPrice,
                avgRating,
                minPrice == Double.MAX_VALUE ? 0 : minPrice,
                maxPrice == Double.MIN_VALUE ? 0 : maxPrice,
                premiumCount,
                topDto
        );
    }

    public static Collector<Product, ProductAnalyticsAccumulator, ProductAggregateStats> productStatsCollector() {
        return Collector.of(
                ProductAnalyticsAccumulator::new,
                ProductAnalyticsAccumulator::add,
                ProductAnalyticsAccumulator::combine,
                ProductAnalyticsAccumulator::finish
        );
    }

}
