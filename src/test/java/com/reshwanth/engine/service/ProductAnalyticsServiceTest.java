package com.reshwanth.engine.service;

import com.reshwanth.engine.model.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductAnalyticsServiceTest {

    @Test
    void testProductWithHighestRatingAndLowPrice(){

        ProductAnalyticsService ps = new ProductAnalyticsService();
        List<Product> productList = List.of(
                new Product(101, "Laptop", "Electronics", 1200.00, 4.5, LocalDate.of(2023, 1, 10)),
                new Product(102, "Chair", "Furniture", 150.00, 4.0, LocalDate.of(2022, 11, 5)),
                new Product(103, "Headphones", "Electronics", 200.00, 4.8, LocalDate.of(2023, 2, 20)));
        var result = ps.findProductWithHighestRatingAndLowPrice(productList);
        assert result.isPresent();
        assertEquals(4.8, result.get().rating());

        List<Product> productListWithSameRating = List.of(
                new Product(101, "Laptop", "Electronics", 1200.00, 4.5, LocalDate.of(2023, 1, 10)),
                new Product(102, "Chair", "Furniture", 150.00, 4.8, LocalDate.of(2022, 11, 5)),
                new Product(103, "Headphones", "Electronics", 200.00, 4.8, LocalDate.of(2023, 2, 20)));
        var result1 = ps.findProductWithHighestRatingAndLowPrice(productListWithSameRating);
        assert result1.isPresent();
        assertEquals(150.00, result1.get().price());

        List<Product> productListWithEmptyList = new ArrayList<>();
        var result2 = ps.findProductWithHighestRatingAndLowPrice(productListWithEmptyList);
        assertTrue(result2.isEmpty());


        var result3 = ps.findProductWithHighestRatingAndLowPrice(null);
        assertTrue(result3.isEmpty());


    }
}
