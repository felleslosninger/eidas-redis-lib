package no.idporten.eidas.redis.cache.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import no.idporten.eidas.redis.cache.RedisCache;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.cache.CacheManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RedisCacheMetricsBinderTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    private MeterRegistry registry;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        registry = new SimpleMeterRegistry();
    }

    @Test
    public void testBindToRegistersGaugeForEachCache() {
        RedisCache<String, String> cache1 = new RedisCache<>("cache1", 300, redisTemplate);
        RedisCache<String, String> cache2 = new RedisCache<>("cache2", 600, redisTemplate);

        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList("cache1", "cache2"));
        doReturn(cache1).when(cacheManager).getCache("cache1");
        doReturn(cache2).when(cacheManager).getCache("cache2");

        RedisCacheMetricsBinder binder = new RedisCacheMetricsBinder(cacheManager);
        binder.bindTo(registry);

        Gauge gauge1 = registry.find("cache.size").tag("cache", "cache1").gauge();
        Gauge gauge2 = registry.find("cache.size").tag("cache", "cache2").gauge();

        assertNotNull(gauge1);
        assertNotNull(gauge2);
    }

    @Test
    public void testGaugeReturnsCacheSize() {
        Set<String> keys = new HashSet<>(Arrays.asList("cache1:key1", "cache1:key2", "cache1:key3"));
        RedisCache<String, String> cache = new RedisCache<>("cache1", 300, redisTemplate);

        when(cacheManager.getCacheNames()).thenReturn(Collections.singletonList("cache1"));
        doReturn(cache).when(cacheManager).getCache("cache1");
        when(redisTemplate.keys("cache1*")).thenReturn(keys);

        RedisCacheMetricsBinder binder = new RedisCacheMetricsBinder(cacheManager);
        binder.bindTo(registry);

        Gauge gauge = registry.find("cache.size").tag("cache", "cache1").gauge();
        assertNotNull(gauge);
        assertEquals(3.0, gauge.value(), 0.0);
    }

    @Test
    public void testGaugeReturnsZeroForEmptyCache() {
        RedisCache<String, String> cache = new RedisCache<>("empty", 300, redisTemplate);

        when(cacheManager.getCacheNames()).thenReturn(Collections.singletonList("empty"));
        doReturn(cache).when(cacheManager).getCache("empty");
        when(redisTemplate.keys("empty*")).thenReturn(Collections.emptySet());

        RedisCacheMetricsBinder binder = new RedisCacheMetricsBinder(cacheManager);
        binder.bindTo(registry);

        Gauge gauge = registry.find("cache.size").tag("cache", "empty").gauge();
        assertNotNull(gauge);
        assertEquals(0.0, gauge.value(), 0.0);
    }

    @Test
    public void testGaugeReturnsMinusOneOnException() {
        RedisCache<String, String> cache = new RedisCache<>("failing", 300, redisTemplate);

        when(cacheManager.getCacheNames()).thenReturn(Collections.singletonList("failing"));
        doReturn(cache).when(cacheManager).getCache("failing");
        when(redisTemplate.keys("failing*")).thenThrow(new RuntimeException("Redis connection failed"));

        RedisCacheMetricsBinder binder = new RedisCacheMetricsBinder(cacheManager);
        binder.bindTo(registry);

        Gauge gauge = registry.find("cache.size").tag("cache", "failing").gauge();
        assertNotNull(gauge);
        assertEquals(-1.0, gauge.value(), 0.0);
    }

    @Test
    public void testNoCachesRegistered() {
        when(cacheManager.getCacheNames()).thenReturn(Collections.emptyList());

        RedisCacheMetricsBinder binder = new RedisCacheMetricsBinder(cacheManager);
        binder.bindTo(registry);

        Gauge gauge = registry.find("cache.size").gauge();
        assertNull(gauge);
    }

    @Test
    public void testGaugeHasCorrectDescription() {
        RedisCache<String, String> cache = new RedisCache<>("desc", 300, redisTemplate);

        when(cacheManager.getCacheNames()).thenReturn(Collections.singletonList("desc"));
        doReturn(cache).when(cacheManager).getCache("desc");
        when(redisTemplate.keys("desc*")).thenReturn(Collections.emptySet());

        RedisCacheMetricsBinder binder = new RedisCacheMetricsBinder(cacheManager);
        binder.bindTo(registry);

        Gauge gauge = registry.find("cache.size").tag("cache", "desc").gauge();
        assertNotNull(gauge);
        assertEquals("Number of entries in the Redis cache", gauge.getId().getDescription());
    }
}
