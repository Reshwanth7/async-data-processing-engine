package com.reshwanth.engine.service;

import com.reshwanth.engine.DTO.CategoryPriceStats;
import com.reshwanth.engine.DTO.EnrichedProductDTO;
import com.reshwanth.engine.DTO.ProductDashboardSummary;
import com.reshwanth.engine.DTO.ProductSummaryDTO;
import com.reshwanth.engine.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class ProductAnalyticsServiceTest {


    private List<Product> products;
    private Product p1, p2, p3, p4, p5, p6;

    @BeforeEach
    void setUp() {
        p1 = new Product(1, "P1", "Electronics", 150, 4.2,
                LocalDate.of(2023, 1, 10), List.of("tech", "portable"));

        p2 = new Product(2, "P2", "Electronics", 300, 4.7,
                LocalDate.of(2023, 2, 15), List.of("tech", "audio"));

        p3 = new Product(3, "P3", "Electronics", 700, 4.9,
                LocalDate.of(2022, 12, 1), List.of("premium", "tech"));

        p4 = new Product(4, "P4", "Furniture", 120, 4.1,
                LocalDate.of(2022, 5, 20), List.of("wood", "home"));

        p5 = new Product(5, "P5", "Furniture", 450, 4.6,
                LocalDate.of(2023, 3, 1), List.of("home", "premium"));

        p6 = new Product(6, "P6", "Grocery", 50, 4.8,
                LocalDate.of(2023, 1, 5), List.of("food", "organic"));

        products = List.of(p1, p2, p3, p4, p5, p6);
    }

    @Test
    void testValidRating() {
        assertDoesNotThrow(() ->
                new Product(10, "Valid", "Test", 100, 5.0,
                        LocalDate.now(), List.of("tag")));
    }

    @Test
    void testRatingBelowZeroThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product(11, "Invalid", "Test", 100, -1.0,
                        LocalDate.now(), List.of("tag")));
    }

    @Test
    void testRatingAboveFiveThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new Product(12, "Invalid", "Test", 100, 5.5,
                        LocalDate.now(), List.of("tag")));
    }
    @Test
    void testProductWithHighestRatingAndLowPrice(){

        ProductAnalyticsService ps = new ProductAnalyticsService();
        List<Product> productList = List.of(
                new Product(101, "Laptop", "Electronics", 1200.00, 4.5, LocalDate.of(2023, 1, 10), List.of("Electronics")),
                new Product(102, "Chair", "Furniture", 150.00, 4.0, LocalDate.of(2022, 11, 5),List.of("Electronics")),
                new Product(103, "Headphones", "Electronics", 200.00, 4.8, LocalDate.of(2023, 2, 20),List.of("Electronics")));
        var result = ps.findProductWithHighestRatingAndLowPrice(productList);
        assert result.isPresent();
        assertEquals(4.8, result.get().rating());

        List<Product> productListWithSameRating = List.of(
                new Product(101, "Laptop", "Electronics", 1200.00, 4.8, LocalDate.of(2023, 1, 10),List.of("Electronics")),
                new Product(102, "Chair", "Furniture", 150.00, 4.8, LocalDate.of(2022, 11, 5),List.of("Electronics")),
                new Product(103, "Headphones", "Electronics", 200.00, 4.8, LocalDate.of(2023, 2, 20),List.of("Electronics")));
        var result1 = ps.findProductWithHighestRatingAndLowPrice(productListWithSameRating);
        assert result1.isPresent();
        assertEquals(150.00, result1.get().price());

        List<Product> productListWithEmptyList = new ArrayList<>();
        var result2 = ps.findProductWithHighestRatingAndLowPrice(productListWithEmptyList);
        assertTrue(result2.isEmpty());


        var result3 = ps.findProductWithHighestRatingAndLowPrice(null);
        assertTrue(result3.isEmpty());

        List<Product> productsList = List.of(
                new Product(101, "Laptop", "Electronics", 1200.00, 4.5, LocalDate.of(2023, 1, 10),List.of("Electronics")),
                new Product(102, "Chair", "Furniture", 150.00, 3.6, LocalDate.of(2022, 11, 5),List.of("Electronics")),
                new Product(103, "Headphones", "Electronics", 200.00, 4.8, LocalDate.of(2023, 2, 20),List.of("Electronics")));

        var result4 = ps.findBestValueForMoneyProduct(productsList);
        assertTrue(result4.isPresent());
        assertEquals(103, result4.get().productId());

        List<Product> productsListLimit = List.of(
                new Product(101, "Laptop", "Electronics", 1200.00, 4.5, LocalDate.of(2023, 1, 10),List.of("Electronics")),
                new Product(102, "Chair", "Furniture", 150.00, 4.3, LocalDate.of(2022, 11, 5),List.of("Electronics")),
                new Product(103, "Headphones", "Electronics", 200.00, 4.8, LocalDate.of(2023, 2, 20),List.of("Electronics")),
                new Product(104, "Desk", "Furniture", 300.00, 3.9, LocalDate.of(2021, 9, 15),List.of("Electronics")),
                new Product(105, "Monitor", "Electronics", 400.00, 4.3, LocalDate.of(2022, 5, 1),List.of("Electronics"))
        );

        var result5 = ps.findTopThreeRatedProducts(productsListLimit);
        assertEquals(3, result5.size());
        assertTrue(result5.contains(productsListLimit.get(1)));
    }

    @Test
    void testCategoryWiseSorting_MultipleCategories() {
        ProductAnalyticsService ps = new ProductAnalyticsService();
        List<Product> productsListLimit = List.of(
                new Product(101, "Laptop", "Electronics", 1200.00, 4.3, LocalDate.of(2023, 1, 10),List.of("Electronics")),
                new Product(102, "Chair", "Furniture", 150.00, 4.3, LocalDate.of(2022, 11, 5),List.of("Electronics")),
                new Product(103, "Headphones", "Electronics", 200.00, 4.8, LocalDate.of(2023, 2, 20),List.of("Electronics")),
                new Product(104, "Desk", "Furniture", 300.00, 3.9, LocalDate.of(2021, 9, 15),List.of("Electronics")),
                new Product(105, "Monitor", "Electronics", 400.00, 4.3, LocalDate.of(2022, 5, 1),List.of("Electronics"))
        );

        Map<String, List<Product>> result = ps.groupProductsByCategoryAndSortByRating(productsListLimit);

        assertEquals(2, result.size());

        List<Product> electronics = result.get("Electronics");
        assertEquals("Headphones", electronics.get(0).productName()); // highest rating first
        assertEquals("Monitor", electronics.get(1).productName()); // highest rating first


    }

    @Test
    void testFindLatestProductAddedByCategory_WithGivenDataset() {
        ProductAnalyticsService ps = new ProductAnalyticsService();

        List<Product> productsListLimit = List.of(
                new Product(101, "Laptop", "Electronics", 1200.00, 4.3, LocalDate.of(2023, 1, 10),List.of("Electronics")),
                new Product(102, "Chair", "Furniture", 150.00, 4.3, LocalDate.of(2022, 11, 5),List.of("Electronics")),
                new Product(103, "Headphones", "Electronics", 200.00, 4.8, LocalDate.of(2023, 2, 20),List.of("Electronics")),
                new Product(104, "Desk", "Furniture", 300.00, 3.9, LocalDate.of(2021, 9, 15),List.of("Electronics")),
                new Product(105, "Monitor", "Electronics", 400.00, 4.3, LocalDate.of(2022, 5, 1),List.of("Electronics"))
        );

        Map<String, Product> result = ps.findLatestProductAddedByCategory(productsListLimit);

        assertEquals(2, result.size());

        assertEquals("Headphones", result.get("Electronics").productName());
        assertEquals("Chair", result.get("Furniture").productName());
    }

    @Test
    void testFindAverageRatingsPerCategory_WithGivenDataset() {
        ProductAnalyticsService ps = new ProductAnalyticsService();

        List<Product> productsListLimit = List.of(
                new Product(101, "Laptop", "Electronics", 1200.00, 4.3, LocalDate.of(2023, 1, 10),List.of("Electronics")),
                new Product(102, "Chair", "Furniture", 150.00, 4.3, LocalDate.of(2022, 11, 5),List.of("Electronics")),
                new Product(103, "Headphones", "Electronics", 200.00, 4.8, LocalDate.of(2023, 2, 20),List.of("Electronics")),
                new Product(104, "Desk", "Furniture", 300.00, 3.9, LocalDate.of(2021, 9, 15),List.of("Electronics")),
                new Product(105, "Monitor", "Electronics", 400.00, 4.3, LocalDate.of(2022, 5, 1),List.of("Electronics"))
        );

        Map<String, Double> result = ps.findAverageRatingsPerCategory(productsListLimit);

        assertEquals(2, result.size());

        assertEquals(4.47, result.get("Electronics"));
        assertEquals(4.10, result.get("Furniture"));
    }

// ---------------------------------------------------------
    //  USE CASE 1 — UNIQUE TAGS EXTRACTION
    // ---------------------------------------------------------

    @Test
    void testUniqueTagsExtraction() {
        var service = new ProductAnalyticsService();
        Set<String> tags = service.findAllUniqueTags(products);

        assertEquals(Set.of(
                "tech", "portable", "audio", "premium",
                "wood", "home", "food", "organic"
        ), tags);
    }

    @Test
    void testUniqueTagsEmptyList() {
        var service = new ProductAnalyticsService();
        assertTrue(service.findAllUniqueTags(List.of()).isEmpty());
    }

    @Test
    void testUniqueTagsNullList() {
        var service = new ProductAnalyticsService();
        assertTrue(service.findAllUniqueTags(null).isEmpty());
    }

    // ---------------------------------------------------------
    //  USE CASE 2 — PARTITION BY RATING
    // ---------------------------------------------------------

    @Test
    void testPartitionByRating() {
        var service = new ProductAnalyticsService();
        var result = service.partitionProductsByRating(products);

        assertEquals(List.of(p2, p3, p5, p6), result.get(true));
        assertEquals(List.of(p1, p4), result.get(false));
    }

    // ---------------------------------------------------------
    //  USE CASE 3 — MULTI-LEVEL GROUPING
    // ---------------------------------------------------------

    @Test
    void testMultiLevelGrouping() {
        var service = new ProductAnalyticsService();
        var result = service.groupProductsByCategoryAndPriceRange(products);

        assertEquals(List.of(p1), result.get("Electronics").get("LOW"));
        assertEquals(List.of(p2), result.get("Electronics").get("MEDIUM"));
        assertEquals(List.of(p3), result.get("Electronics").get("HIGH"));
    }

    // ---------------------------------------------------------
    //  USE CASE 4 — SUMMARY STATISTICS
    // ---------------------------------------------------------

    @Test
    void testSummaryStatistics() {
        var service = new ProductAnalyticsService();
        var stats = service.summarizePriceByCategory(products);

        var electronics = stats.get("Electronics");
        assertEquals(3, electronics.getCount());
        assertEquals(150, electronics.getMin());
        assertEquals(700, electronics.getMax());
        assertEquals(1150, electronics.getSum());
    }

    // ---------------------------------------------------------
    //  USE CASE 5 — MOST EXPENSIVE PRODUCT PER CATEGORY
    // ---------------------------------------------------------

    @Test
    void testMostExpensiveProduct() {
        var service = new ProductAnalyticsService();
        var result = service.findMostExpensiveProductByCategory(products);

        assertEquals(p3, result.get("Electronics"));
        assertEquals(p5, result.get("Furniture"));
        assertEquals(p6, result.get("Grocery"));
    }

    // ---------------------------------------------------------
    //  USE CASE 6 — FIRST MATCHING PRODUCT
    // ---------------------------------------------------------

    @Test
    void testFindFirstMatchingProduct() {
        var service = new ProductAnalyticsService();
        var result = service.findFirstMatchingProduct(products);

        assertTrue(result.isPresent());
        assertEquals(p2, result.get());
    }

    // ---------------------------------------------------------
    //  USE CASE 7 — PREDICATE-BASED FILTERING
    // ---------------------------------------------------------

    @Test
    void testPredicateFiltering() {
        var service = new ProductAnalyticsService();

        Predicate<Product> priceBelow300 = p -> p.price() < 300;

        var result = service.filterProducts(products, priceBelow300);

        assertEquals(List.of(p1, p4, p6), result);
    }

    // ---------------------------------------------------------
    //  USE CASE 8 — COMBINED PREDICATES
    // ---------------------------------------------------------

    @Test
    void testCombinedPredicates() {
        var service = new ProductAnalyticsService();

        Predicate<Product> l1 = p -> p.price() < 500;
        Predicate<Product> l2 = p -> p.rating() > 4.0;
        Predicate<Product> l3 = p -> p.productTags().contains("premium");

        var result = service.filterWithCombinedPredicates(products, l1, l2, l3);

        // (p1 AND p2) OR (NOT p3)
        // premium products excluded unless they satisfy p1 AND p2
        assertTrue(result.contains(p1)); // p1: price<500 & rating>4.0
        assertTrue(result.contains(p2));
        assertTrue(result.contains(p4));
        assertTrue(result.contains(p6));

        // p3 (premium) but price<500 AND rating>4.0 → p5 included
        assertTrue(result.contains(p5));

        // p3 (premium) but price>500 → p3 excluded
        assertFalse(result.contains(p3));
    }

    //Day 4
    @Test
    void testFiltersAndSortingAndLimit() {
        var service = new ProductAnalyticsService();
        List<Product> products = List.of(
                new Product(1, "A", "Electronics", 150, 4.5, LocalDate.of(2024, 1, 10), List.of("tag1")),
                new Product(2, "B", "Grocery", 200, 5.0, LocalDate.of(2024, 2, 10), List.of("tag1")),
                new Product(3, "C", "Furniture", 300, 4.0, LocalDate.of(2024, 3, 10), List.of("tag1")),
                new Product(4, "D", "Electronics", 500, 4.8, LocalDate.of(2024, 4, 10), List.of("tag1")),
                new Product(5, "E", "Electronics", 600, 4.9, LocalDate.of(2024, 5, 10), List.of("tag1")),
                new Product(6, "F", "Electronics", 700, 4.7, LocalDate.of(2024, 6, 10), List.of("tag1"))
        );

        List<ProductSummaryDTO> result = service.productSummaryDTOList(products);

        assertEquals(5, result.size());
        assertEquals("E", result.get(0).productName()); // highest rating
        assertEquals("D", result.get(1).productName());
    }

    @Test
    void testNullListReturnsEmpty() {
        var service = new ProductAnalyticsService();
        assertTrue(service.productSummaryDTOList(null).isEmpty());
    }

    @Test
    void testNullTagsAreFilteredOut() {
        var service = new ProductAnalyticsService();
        List<Product> products = List.of(
                new Product(1, "A", "Electronics", 150, 4.5, LocalDate.now(), null)
        );

        assertTrue(service.productSummaryDTOList(products).isEmpty());
    }


    @Test
    void testGroupingAndStats() {
        var service = new ProductAnalyticsService();
        List<Product> products = List.of(
                new Product(1, "A", "Electronics", 100, 4.0, LocalDate.now(), List.of("t")),
                new Product(2, "B", "Electronics", 300, 4.0, LocalDate.now(), List.of("t")),
                new Product(3, "C", "Furniture", 600, 4.0, LocalDate.now(), List.of("t"))
        );

        Map<String, Map<String, CategoryPriceStats>> result =
                service.groupByCategoryOfCategoryStats(products);

        assertEquals(2, result.size());
        assertEquals(2, result.get("Electronics").size());
        assertEquals(1, result.get("Furniture").size());

        CategoryPriceStats high = result.get("Furniture").get("HIGH");
        assertEquals(1, high.count());
        assertEquals(600, high.minPrice());
    }

    @Test
    void testInvalidProductsAreSkipped() {
        var service = new ProductAnalyticsService();
        List<Product> products = List.of(
                new Product(1, "A", null, 100, 4.0, LocalDate.now(), List.of("t")),
                new Product(2, "B", "Electronics", -10, 4.0, LocalDate.now(), List.of("t"))
        );

        assertTrue(service.groupByCategoryOfCategoryStats(products).isEmpty());
    }

    @Test
    void testDiscountLogic() {
        var service = new ProductAnalyticsService();
        Product p1 = new Product(1, "A", "Electronics", 600, 4.0, LocalDate.now(), List.of("t"));
        Product p2 = new Product(2, "B", "Furniture", 400, 4.6, LocalDate.now(), List.of("t"));

        List<EnrichedProductDTO> result = service.enrichedProductDTOList(List.of(p1, p2));

        EnrichedProductDTO e1 = result.stream().filter(e -> e.productId() == 1).findFirst().get();
        EnrichedProductDTO e2 = result.stream().filter(e -> e.productId() == 2).findFirst().get();

        assertEquals(540, e1.discountedPrice()); // 10%
        assertEquals(380, e2.discountedPrice()); // 5%
    }

    @Test
    void testPremiumProductLogic() {
        var service = new ProductAnalyticsService();
        Product p = new Product(1, "A", "Electronics", 400, 4.6, LocalDate.now(), List.of("t"));

        EnrichedProductDTO dto = service.enrichedProductDTOList(List.of(p)).get(0);

        assertTrue(dto.isPremiumProduct());
    }

    @Test
    void testSortingOrder() {
        var service = new ProductAnalyticsService();
        Product p1 = new Product(1, "A", "Electronics", 600, 4.6, LocalDate.now(), List.of("t"));
        Product p2 = new Product(2, "B", "Electronics", 200, 4.9, LocalDate.now(), List.of("t"));

        List<EnrichedProductDTO> result = service.enrichedProductDTOList(List.of(p1, p2));

        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0).productId()); // premium first
    }

    @Test
    void testDashboardSummary() {
        var service = new ProductAnalyticsService();
        List<Product> products = List.of(
                new Product(1, "A", "Electronics", 600, 4.6, LocalDate.now(), List.of("t")),
                new Product(2, "B", "Furniture", 300, 4.0, LocalDate.now(), List.of("t")),
                new Product(3, "C", "Electronics", 150, 3.5, LocalDate.now(), List.of("t"))
        );

        ProductDashboardSummary summary = service.getProductDashBoard(products);

        assertEquals(3, summary.totalProducts());
        assertEquals(2, summary.categoryCounts().get("Electronics"));
        assertEquals(1, summary.priceBucketCounts().get("LOW"));
        assertEquals(1, summary.premiumProductCount());
        assertEquals("Electronics", summary.mostCommonCategory());
        assertEquals(3, summary.topRatedProducts().size());
    }

    @Test
    void testEmptyInput() {
        var service = new ProductAnalyticsService();
        ProductDashboardSummary summary = service.getProductDashBoard(null);

        assertEquals(0, summary.totalProducts());
        assertTrue(summary.categoryCounts().isEmpty());
        assertTrue(summary.priceBucketCounts().isEmpty());
        assertTrue(summary.topRatedProducts().isEmpty());
        assertNull(summary.mostCommonCategory());
    }
}
