package com.reshwanth.engine.util;

import com.reshwanth.engine.model.Product;

import java.time.LocalDate;
import java.util.List;

public class ProductDataLoader {

     public static List<Product> loadProducts() {
        return List.of(
                new Product(101, "Laptop", "Electronics", 1200.00, 4.5, LocalDate.of(2023, 1, 10),List.of("Electronics")),
                new Product(102, "Chair", "Furniture", 150.00, 4.0, LocalDate.of(2022, 11, 5),List.of("Electronics")),
                new Product(103, "Headphones", "Electronics", 200.00, 4.8, LocalDate.of(2023, 2, 20),List.of("Electronics")),
                new Product(104, "Desk", "Furniture", 300.00, 3.9, LocalDate.of(2021, 9, 15),List.of("Electronics")),
                new Product(105, "Monitor", "Electronics", 400.00, 4.3, LocalDate.of(2022, 5, 1),List.of("Electronics"))
        );
    }
}
