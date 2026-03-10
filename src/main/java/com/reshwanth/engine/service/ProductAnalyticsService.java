package com.reshwanth.engine.service;

import com.reshwanth.engine.model.Product;

import java.util.*;
import java.util.stream.Collectors;

public class ProductAnalyticsService {

    //Day 1
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

    //Day2

    public Optional<Product> findBestValueForMoneyProduct(List<Product> productsList){
       return Optional.ofNullable(productsList)
                .orElseGet(Collections::emptyList)
                .stream()
                .max(Comparator.comparingDouble((Product p) -> p.rating() / p.price())
                        .thenComparing(Product::addedDate));

    }

    public List<Product> findTopThreeRatedProducts(List<Product> productList){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .sorted(Comparator.comparing(Product::rating).reversed()
                        .thenComparing(Product::price))
                .limit(3)
                .collect(Collectors.toList());
    }

    public Map<String, List<Product>> groupProductsByCategoryAndSortByRating(List<Product> productList){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.groupingBy(
                        Product::category,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list ->list.stream()
                                        .sorted(Comparator.comparing(Product::rating).reversed()
                                                .thenComparing(Product::price))
                                        .toList()
                        )
                ));
    }


    public Map<String, Product> findLatestProductAddedByCategory(List<Product> productList){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(
                        Product::category,
                        Collectors.maxBy((p1,p2) -> p1.addedDate().compareTo(p2.addedDate()))
                        ),
                        map->map.entrySet()
                                .stream()
                                .collect(
                                        Collectors.toMap(
                                                Map.Entry::getKey,
                                                e -> e.getValue().orElse(null)
                                        )
                                )



                ));

    }

    public Map<String, Double> findAverageRatingsPerCategory(List<Product> productList){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(
                        Product::category,
                        Collectors.averagingDouble(Product::rating)),
                        map -> map.entrySet()
                                .stream()
                                .collect(
                                        Collectors.toMap(
                                                Map.Entry::getKey,
                                                e -> Math.round(e.getValue() * 100.0) / 100.0
                                        )
                                )
                ));

    }

}
