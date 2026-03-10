package com.reshwanth.engine.service;

import com.reshwanth.engine.model.Product;

import java.util.*;
import java.util.stream.Collectors;

public class ProductAnalyticsService {

    public Optional<Product> findProductWithHighestRatingAndLowPrice(List<Product> productsList){
        return Optional.ofNullable(productsList)
                .orElseGet(Collections::emptyList)
                .stream()
                .max(Comparator.comparing(Product::rating).reversed() //4.8,4.5,4.0
                        .thenComparing(Product::price) // 150,200,1200
                        .reversed());//4.0,4.5,4.8 -- if rating same then price will also reverse
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
