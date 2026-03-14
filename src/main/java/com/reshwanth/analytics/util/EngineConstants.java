package com.reshwanth.analytics.util;

import java.util.List;

public final class EngineConstants {

    private EngineConstants() {
    }

    public static final class Categories {
        public static final String ELECTRONICS = "Electronics";
        public static final String GROCERY = "Grocery";
        public static final String TIMEOUT = "Timeout";
        public static final String SAMPLE_DESCRIPTION_PREFIX = "Sample Description ";

        private Categories() {
        }
    }

    public static final class PriceBuckets {
        public static final String LOW = "LOW";
        public static final String MEDIUM = "MEDIUM";
        public static final String HIGH = "HIGH";
        public static final double LOW_PRICE_UPPER_BOUND = 200.0;
        public static final double MEDIUM_PRICE_UPPER_BOUND = 500.0;

        private PriceBuckets() {
        }
    }

    public static final class Thresholds {
        public static final double MIN_VALID_VALUE = 0.0;
        public static final double SUMMARY_MIN_PRICE = 100.0;
        public static final double SUMMARY_MIN_RATING = 4.0;
        public static final double HIGH_RATING = 4.5;
        public static final double PREMIUM_MIN_PRICE = 300.0;
        public static final double HIGH_PRICE = 500.0;
        public static final double ELECTRONICS_DISCOUNT_PERCENT = 10.0;
        public static final double RATING_DISCOUNT_PERCENT = 5.0;

        private Thresholds() {
        }
    }

    public static final class Async {
        public static final int FIXED_THREAD_POOL_SIZE = 10;
        public static final long FETCH_TIMEOUT_MILLIS = 600L;
        public static final long ENRICH_TIMEOUT_MILLIS = 800L;
        public static final String TIMEOUT_ERROR_METADATA = "Timeout/Error";

        private Async() {
        }
    }

    public static final class ExternalService {
        public static final long FETCH_DETAILS_DELAY_MILLIS = 500L;
        public static final long FETCH_PRICE_DELAY_MILLIS = 300L;
        public static final long FETCH_RATING_DELAY_MILLIS = 200L;
        public static final long FETCH_TAGS_DELAY_MILLIS = 400L;
        public static final long FETCH_METADATA_DELAY_MILLIS = 250L;
        public static final String SAMPLE_PRODUCT_PREFIX = "Sample Product ";
        public static final double DEFAULT_PRICE = 200.00;
        public static final double MAX_PRICE = 500.00;
        public static final double DEFAULT_RATING = 4.9;
        public static final double MAX_RATING = 5.0;
        public static final double ENRICHED_DEFAULT_PRICE = 99.99;
        public static final double ENRICHED_DEFAULT_RATING = 4.7;
        public static final List<String> DEFAULT_TAGS = List.of("tag1", "tag2");
        public static final List<String> ENRICHED_TAGS = List.of("electronics", "new");
        public static final String METADATA_MESSAGE = "Imported from external metadata service";

        private ExternalService() {
        }
    }

    public static final class Limits {
        public static final int TOP_RATED_PRODUCT_COUNT = 3;
        public static final int SUMMARY_PRODUCT_COUNT = 5;

        private Limits() {
        }
    }
}
