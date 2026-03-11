package com.reshwanth.engine.model;

import java.time.LocalDate;
import java.util.List;

public record Product(int productId, String productName, String category, double price, double rating, LocalDate addedDate,
                      List<String> productTags) {
    public Product{
        if(rating <0.0 || rating > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        }
    }
}
