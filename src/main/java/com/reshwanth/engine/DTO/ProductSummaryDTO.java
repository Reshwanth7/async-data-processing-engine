package com.reshwanth.engine.dto;


import java.time.Month;


public record ProductSummaryDTO(int productId, String productName, String category, double price, double rating, Month addedMonth,
                                double tagCount) {

}
