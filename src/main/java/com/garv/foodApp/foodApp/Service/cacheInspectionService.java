package com.garv.foodApp.foodApp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class cacheInspectionService {

    // Inject Spring's CacheManager to interact with caches
    @Autowired
    private CacheManager cacheManager;

    /**
     * Print the contents of a cache by its name
     *
     * @param cacheName The name of the cache to inspect
     */
    public void printCache(String cacheName) {
        // Get the cache instance from the CacheManager
        Cache cache = cacheManager.getCache(cacheName);

        if (cache != null) {
            System.out.println("Cache Content:");
            // Print the underlying native cache object (e.g., ConcurrentMap)
            System.out.println(Objects.requireNonNull(cache.getNativeCache()));
        } else {
            // If cache not found, print a message
            System.out.println("Cache not found");
        }
    }
}
