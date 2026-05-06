package no.idporten.eidas.redis.cache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.cache.Cache;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class EidasRedisCacheManagerTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private EidasRedisCacheManager cacheManager;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Map<String, Cache<?, ?>> cacheBeanMapping = new HashMap<>();
        RedisCache<String, Object> implCache = new RedisCache<>("implCache", 300, redisTemplate);
        cacheBeanMapping.put("functionalCache", implCache);
        cacheManager = new EidasRedisCacheManager(cacheBeanMapping);
    }

    @Test
    public void testGetCacheReturnsRegisteredBean() {
        Cache<String, String> cache = cacheManager.getCache("functionalCache");
        assertNotNull(cache);
        assertEquals("implCache", cache.getName());
    }

    @Test
    public void testGetCacheReturnsSameInstance() {
        Cache<String, String> first = cacheManager.getCache("functionalCache");
        Cache<String, String> second = cacheManager.getCache("functionalCache");
        assertSame(first, second);
    }

    @Test
    public void testGetCacheWithTypes() {
        Cache<String, String> cache = cacheManager.getCache("functionalCache", String.class, String.class);
        assertNotNull(cache);
        assertEquals("implCache", cache.getName());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetCacheUnknownName() {
        cacheManager.getCache("unknownCache");
    }

    @Test
    public void testGetCacheNames() {
        Iterable<String> names = cacheManager.getCacheNames();
        assertNotNull(names);
        assertTrue(names.iterator().hasNext());
        assertEquals("functionalCache", names.iterator().next());
    }

    @Test
    public void testDestroyCache() {
        cacheManager.destroyCache("functionalCache");
        // After destroy, getting the cache should throw
        try {
            cacheManager.getCache("functionalCache");
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testDestroyCacheNonExisting() {
        // Should not throw
        cacheManager.destroyCache("nonExisting");
    }

    @Test
    public void testClose() {
        assertFalse(cacheManager.isClosed());
        cacheManager.close();
        assertTrue(cacheManager.isClosed());
    }

    @Test
    public void testCloseIdempotent() {
        cacheManager.close();
        cacheManager.close();
        assertTrue(cacheManager.isClosed());
    }

    @Test
    public void testIsClosed() {
        assertFalse(cacheManager.isClosed());
    }

    @Test
    public void testUnwrapSelf() {
        EidasRedisCacheManager unwrapped = cacheManager.unwrap(EidasRedisCacheManager.class);
        assertSame(cacheManager, unwrapped);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnwrapUnsupported() {
        cacheManager.unwrap(String.class);
    }

    @Test
    public void testGetCachingProvider() {
        assertNull(cacheManager.getCachingProvider());
    }

    @Test
    public void testGetURI() {
        assertNull(cacheManager.getURI());
    }

    @Test
    public void testGetClassLoader() {
        assertNull(cacheManager.getClassLoader());
    }

    @Test
    public void testGetProperties() {
        assertNull(cacheManager.getProperties());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateCacheUnsupported() {
        cacheManager.createCache("test", null);
    }

    @Test
    public void testEnableManagement() {
        // Should not throw
        cacheManager.enableManagement("test", true);
    }

    @Test
    public void testEnableStatistics() {
        // Should not throw
        cacheManager.enableStatistics("test", true);
    }

    @Test
    public void testConstructorWithNullMapping() {
        try (EidasRedisCacheManager mgr = new EidasRedisCacheManager(null)) {
            assertNotNull(mgr.getCacheNames());
            assertFalse(mgr.getCacheNames().iterator().hasNext());
        }
    }

    @Test
    public void testAddCache() {
        RedisCache<String, Object> newCache = new RedisCache<>("newPrefix", 600, redisTemplate);
        cacheManager.addCache("newFunctionalCache", newCache);

        Cache<String, String> retrieved = cacheManager.getCache("newFunctionalCache");
        assertNotNull(retrieved);
        assertEquals("newPrefix", retrieved.getName());
    }

    @Test
    public void testMultipleCacheBeans() {
        Map<String, Cache<?, ?>> cacheBeanMapping = new HashMap<>();
        cacheBeanMapping.put("cache1", new RedisCache<>("prefix1", 100, redisTemplate));
        cacheBeanMapping.put("cache2", new RedisCache<>("prefix2", 200, redisTemplate));

        EidasRedisCacheManager mgr = new EidasRedisCacheManager(cacheBeanMapping);

        Cache<String, String> c1 = mgr.getCache("cache1");
        Cache<String, String> c2 = mgr.getCache("cache2");

        assertNotNull(c1);
        assertNotNull(c2);
        assertEquals("prefix1", c1.getName());
        assertEquals("prefix2", c2.getName());
    }
}
