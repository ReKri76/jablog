package io.rekri.jablog.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {

    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(Duration.ofHours(24))
            .build();

    public Bucket resolveBucket(String key) {
        return cache.get(key, this::newBucket);
    }

    private Bucket newBucket(String key) {
        Refill refill = Refill.intervally(5, Duration.ofHours(24));
        Bandwidth limit = Bandwidth.classic(5, refill);

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}