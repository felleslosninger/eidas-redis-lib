/*
 * Copyright (c) 2025 by European Commission
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/page/eupl-text-11-12
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.idporten.eidas.redis.cache.metrics;


import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import no.idporten.eidas.redis.cache.RedisCache;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import io.micrometer.common.lang.NonNull;

/**
 * Bind any custom metrics for RedisCaches here
 */
public class RedisCacheMetricsBinder implements MeterBinder {

    private final List<Cache<?,?>> caches;

    public RedisCacheMetricsBinder(CacheManager cacheManager) {
        this.caches = StreamSupport.stream(cacheManager.getCacheNames().spliterator(), false)
                .map(cacheManager::getCache)
                .collect(Collectors.toList());
    }

    @Override
    public void bindTo(@NonNull MeterRegistry registry) {
        for (Cache<?,?> cache : caches) {
            if (cache instanceof RedisCache<?,?> redisCache) {
                Gauge.builder("cache.size", redisCache, c -> {
                            try {
                                List<Object> keys = new ArrayList<>();
                                c.getRedisTemplate().scan(
                                        org.springframework.data.redis.core.ScanOptions.scanOptions()
                                                .match(c.getCachePrefix() + ":*")
                                                .count(100)
                                                .build()
                                ).forEachRemaining(keys::add);
                                return keys.size();
                            } catch (Exception e) {
                                return -1;
                            }
                        })
                        .tag("cache", redisCache.getName())
                        .description("Number of entries in the Redis cache")
                        .register(registry);
            }
        }
    }
}
