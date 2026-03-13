package com.reshwanth.engine.service;

import com.reshwanth.engine.DTO.CategoryPriceStats;
import com.reshwanth.engine.DTO.EnrichedProductDTO;
import com.reshwanth.engine.DTO.ProductDashboardSummary;
import com.reshwanth.engine.DTO.ProductSummaryDTO;
import com.reshwanth.engine.model.*;

import java.util.*;
import java.util.function.Function;
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
                .filter((p1.and(p2)).or(p3.negate()))
                .toList();
    }

    //Day 4
    public List<ProductSummaryDTO> productSummaryDTOList(List<Product> productList){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(product -> product.price() >= 100)
                .filter(product -> product.rating() >= 4.0)
                .filter(product -> !"Grocery".equals(product.category()))
                .filter(product -> product.productTags() != null && !product.productTags().isEmpty())
                .filter(product -> product.addedDate() != null)
                .sorted(Comparator.comparing(Product::rating).reversed()
                        .thenComparing(Product::price)
                        .thenComparing(Product::productName))
                .limit(5)
                .map(product -> new ProductSummaryDTO(product.productId(),product.productName(),product.category()
                ,product.price(), product.rating(), product.addedDate().getMonth(),product.productTags().size()))
                .toList();
    }

    public Map<String, Map<String, CategoryPriceStats>> groupByCategoryOfCategoryStats(List<Product> productList){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(product -> product.category() != null && !product.category().isEmpty())
                .filter(product -> product.price()>=0)
                .filter(product -> product.addedDate() != null)
                .collect(Collectors.collectingAndThen(Collectors.groupingBy(
                        Product::category,
                        Collectors.groupingBy(ProductAnalyticsService::priceCategory,
                       Collectors.summarizingDouble(Product::price)
                        )),
                       map -> map.entrySet()
                               .stream()
                               .collect(
                                       Collectors.toMap(
                                               Map.Entry::getKey,
                                                e ->e.getValue().entrySet()
                                                        .stream()
                                                        .collect(
                                                                Collectors.toMap(
                                                                        Map.Entry::getKey,
                                                                        d -> new CategoryPriceStats(d.getValue().getCount(),d.getValue().getMin(),
                                                                                d.getValue().getMax(),d.getValue().getAverage())
                                                                )
                                                        )
                                               )
                                       )
                               )

                );

    }




    public List<EnrichedProductDTO> enrichedProductDTOList(List<Product> productList){

        Predicate<Product> isElectronics = p -> "Electronics".equals(p.category());
        Predicate<Product> isHighRating = p -> p.rating() >= 4.5;
        Predicate<Product> isPremium = p -> p.rating() >= 4.5 && p.price() >= 300 && !"Grocery".equals(p.category());
        Predicate<Product> isHighPrice = p -> p.price() >= 500;

        Function<Product, Double> discountedPriceFn = p -> {
            boolean electronicsDiscount = isElectronics.and(isHighPrice).test(p);
            boolean ratingDiscount = isHighRating.test(p);

            if (electronicsDiscount) {
                return p.price() - (p.price() * 10 / 100);
            }
            if (ratingDiscount) {
                return p.price() - (p.price() * 5 / 100);
            }
            return p.price();
        };

        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(p -> p.productName() != null && !p.productName().isEmpty())
                .filter(p -> p.price() >= 0)
                .filter(p -> p.rating() >= 0)
                .filter(p -> p.addedDate() != null)
                .filter(p -> p.productTags() != null && !p.productTags().isEmpty())
                .map(p -> new EnrichedProductDTO(
                        p.productId(),
                        p.productName(),
                        p.category(),
                        p.price(),
                        p.rating(),
                        (isElectronics.and(isHighPrice)).or(isHighRating).test(p),
                        discountedPriceFn.apply(p),
                        priceCategory(p),
                        p.addedDate().getMonth(),
                        p.productTags().size(),
                        isPremium.test(p)
                ))
                .sorted(
                        Comparator.comparing(EnrichedProductDTO::isPremiumProduct)
                                .thenComparing(EnrichedProductDTO::rating).reversed()
                                .thenComparing(EnrichedProductDTO::discountedPrice)
                )
                .toList();
    }

    public ProductDashboardSummary getProductDashBoard(List<Product> productList){
        List<Product> filteredList = filteredProductList(productList);
        long countOfProducts = filteredList.size();
        double averagePrice = filteredList
                .stream()
                .collect(Collectors.averagingDouble(Product::price));
        double averageRating = filteredList
                .stream()
                .collect(Collectors.averagingDouble(Product::rating));
        Map<String, Long> categoryCounts = filteredList
                .stream()
                .collect(Collectors.groupingBy(Product::category,Collectors.counting()));
        Map<String, Long> priceBucketCounts = filteredList
                .stream()
                .collect(Collectors.groupingBy(ProductAnalyticsService::priceCategory,Collectors.counting()));

        List<ProductSummaryDTO> topRatedProducts = filteredList
                .stream()
                .sorted(Comparator.comparing(Product::rating).reversed()
                        .thenComparing(Product::price))
                .limit(3)
                .map(product -> new ProductSummaryDTO(product.productId(),product.productName(),product.category()
                        ,product.price(), product.rating(), product.addedDate().getMonth(),product.productTags().size()))
                .toList();

        long premiumProductCount = filteredList
                .stream()
                .filter(product -> product.rating()>= 4.5 && product.price()>=300 && !"Grocery".equals(product.category()))
                .count();
        String mostCommonCategory = categoryCounts.isEmpty() ? null : categoryCounts.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);

        return new ProductDashboardSummary(countOfProducts,averagePrice,averageRating,categoryCounts,priceBucketCounts,topRatedProducts,premiumProductCount,mostCommonCategory);

    }

    public List<Product> filteredProductList(List<Product> productList){
        return Optional.ofNullable(productList)
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(p -> p.productName() != null && !p.productName().isEmpty())
                .filter(p -> p.price() >= 0)
                .filter(p -> p.rating() >= 0)
                .filter(p -> p.addedDate() != null)
                .filter(p -> p.productTags() != null && !p.productTags().isEmpty())
                .toList();
    }

    public static String priceCategory(Product product) {
        return Optional.ofNullable(product)
                .map(p -> {
                    if (p.price() < 200) return "LOW";
                    if (p.price() <= 500) return "MEDIUM";
                    return "HIGH";
                })
                .orElse(null);
    }
}
