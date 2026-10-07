package com.example.siparis_sistemi.service;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    // Kullanıcı adlarına (veya IP'lere) karşılık kovaları hafızada güvenli tutacağımız yapı
    private final ConcurrentHashMap<String, Bucket> cache = new ConcurrentHashMap<>();
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(5) 
                .refillIntervally(5, Duration.ofMinutes(15)) 
                .build();
                
        return Bucket.builder().addLimit(limit).build();
    }
    // Kullanıcının kovasında jeton bitmiş mi? (Yani engellenmiş mi?)
    public boolean isBlocked(String key) {
        Bucket bucket = cache.computeIfAbsent(key, k -> createNewBucket());
        return bucket.getAvailableTokens() == 0;
    }
    public void loginFailed(String key) {
        Bucket bucket = cache.computeIfAbsent(key, k -> createNewBucket());
        bucket.tryConsume(1); // 1 hakkı yaktık
    }
    public void loginSucceeded(String key) {
        cache.remove(key);
    }
}
