package com.reshwanth.engine.service;

import com.reshwanth.engine.model.Product;

import java.util.*;
import java.util.stream.Collectors;

public class ProductAnalyticsService {

    public Optional<Product> findProductWithHighestRatingAndLowPrice(List<Product> productsList){
        System.out.println("Comparator running...");
        return Optional.ofNullable(productsList)
                .orElseGet(Collections::emptyList)
                .stream()
                .max((product1, product2) -> {
                    System.out.println("Comparing: " + product1.productName() + " vs " + product2.productName());
                    System.out.println("Ratings: " + product1.rating() + " vs " + product2.rating());
                    int ratingComparison = Double.compare(product1.rating(), product2.rating());
                    return ratingComparison == 0
                            ? Double.compare(product2.price(), product1.price())
                            : ratingComparison;
                });
    }




    public Map<String, List<Product>> groupProductsByCategory(List<Product> productsList) {

        Map<String, List<Product>> grouped = Optional.ofNullable(productsList)
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.groupingBy(
                        Product::category,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparingDouble(Product::price))
                                        .toList()
                        )
                ));

        return Collections.unmodifiableMap(grouped);
    }

    public Map<String, Double> averagePriceByCategory(List<Product> productsList) {

        return Optional.ofNullable(productsList)
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(
                                Product::category,
                                Collectors.averagingDouble(Product::price)
                        ),
                        map -> map.entrySet()
                                .stream()
                                .collect(Collectors.toUnmodifiableMap(
                                        Map.Entry::getKey,
                                        e -> Math.round(e.getValue() * 100.0) / 100.0
                                ))
                ));
    }

}
