package com.reshwanth.engine.service;

import com.reshwanth.engine.model.Product;

import java.util.*;
import java.util.function.Predicate;
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
                .filter(p -> p.price() > 0)
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
                                .filter(e -> e.getValue().isPresent())
                                .collect(
                                        Collectors.toMap(
                                                Map.Entry::getKey,
                                                e -> e.getValue().get()
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

    //Use Cases Day 3
    public Set<String> findAllUniqueTags(List<Product> productList){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(Product::productTags)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

    }

    public Map<Boolean, List<Product>> partitionProductsByRating(List<Product> productList){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.partitioningBy(product -> product.rating()>= 4.5));
    }

    public Map<String, Map<String, List<Product>>> groupProductsByCategoryAndPriceRange(List<Product> productList) {
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.groupingBy(Product::category
                        , Collectors.groupingBy(product ->
                                {
                                    if(product.price() < 200) return "LOW";
                                    if(product.price() < 500) return "MEDIUM";
                                    return "HIGH";
                                }
                                )));
    }

    public Map<String, DoubleSummaryStatistics> summarizePriceByCategory(List<Product> productList){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.groupingBy(Product::category,
                        Collectors.summarizingDouble(Product::price)
                ));
    }

    public Map<String, Product> findMostExpensiveProductByCategory(List<Product> productList){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.groupingBy(Product::category,
                        Collectors.reducing((product1,product2) ->
                        {
                            if(product1.price() > product2.price())
                                return product1;
                            else
                                return product2;
                        })
                ),
                   map -> map.entrySet()
                           .stream()
      .filter(e -> e.getValue().isPresent())
                           .collect(
                                   Collectors.toMap(
                                           Map.Entry::getKey,
                                           e -> e.getValue().get()
                                   )
                           )
                ));

    }

    public Optional<Product> findFirstMatchingProduct(List<Product> productList){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(product -> product.category().equals("Electronics"))
                .filter(product -> product.price()<=500.0)
                .filter(product -> product.rating()>=4.5)
                .findFirst();
    }


    public List<Product> filterProducts(List<Product> productList, Predicate<Product> predicate){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(predicate)
                .toList();
    }

    public List<Product> filterWithCombinedPredicates(List<Product> productList, Predicate<Product> p1, Predicate<Product> p2, Predicate<Product> p3){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .peek( p -> System.out.println("Before"+p))
                .filter((p1.and(p2)).or(p3.negate()))
                .peek( p -> System.out.println("After"+p))
                .toList();
    }




}
