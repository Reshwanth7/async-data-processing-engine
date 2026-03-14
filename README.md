Modern Java Analytics Engine
A backend analytics engine built using Modern Java, showcasing advanced use of streams, collectors, custom grouping, performance‑optimized pipelines, and a full asynchronous enrichment + analytics pipeline using CompletableFuture.

This project demonstrates real backend engineering patterns, clean layering, and non‑blocking design suitable for microservices and high‑performance systems.

src/main/java/com.reshwanth.engine/
  domain/        → Core domain models (Product, Category, etc.)
  dto/           → Summary, enrichment, and analytics DTOs
  service/       → Synchronous analytics engine 
  async/         → Asynchronous enrichment + analytics pipeline 
  external/      → Simulated external services (price, rating, metadata)
  config/        → Custom executor/thread pool configuration
  util/          → Test data generators and helpers

src/test/java/com.reshwanth.engine/
  service/       → Tests for synchronous analytics
  

Tech Stack:
Java 17+
CompletableFuture for async pipelines
Streams & Collectors for analytics
Custom thread pools for concurrency control
JUnit 5 for testing
Maven for build and dependency management

Features:
Multi‑level grouping and aggregation
Custom collectors for domain‑specific analytics
Performance‑optimized stream pipelines
Parallel data processing
Full asynchronous enrichment engine
Non‑blocking analytics pipeline
Timeout and fallback handling
Clean, immutable DTO design

Async Pipeline Flow:
fetchProductAsync(productId)
      ↓
transformProductAsync(product)
      ↓
fetchEnrichmentAsync(productId)  ← runs price, rating, tags, metadata in parallel
      ↓
computeAnalyticsAsync(product, enrichment)
      ↓
combine into AsyncEnrichedAnalyticsDTO

Sample Output (AsyncEnrichedAnalyticsDTO):
{
  "productId": 101,
  "productName": "LAPTOP PRO",
  "category": "ELECTRONICS",
  "price": 999.99,
  "rating": 4.7,
  "tags": ["electronics", "new"],
  "metadata": "Imported from external metadata service",
  "popularityScore": 9.4,
  "qualityIndex": 4.6,
  "weightedRating": 4.61,
  "priceValueRatio": 0.0046,
  "processedTimestamp": "2026-03-14T11:45:00"
}

Performance Notes:
All enrichment calls run in parallel using a custom thread pool
No blocking inside the pipeline (only at the final .join() wrapper)
Stream operations are fused and optimized
Primitive streams reduce boxing overhead
Timeouts prevent slow external calls from blocking the system

How to Run:
mvn clean install
mvn test
