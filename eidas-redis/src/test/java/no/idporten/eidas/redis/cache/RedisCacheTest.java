package no.idporten.eidas.redis.cache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class RedisCacheTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisCache<String, String> cache;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        cache = new RedisCache<>("test", 300, redisTemplate);
    }

    @Test
    public void testConstructorWithPositiveTtl() {
        RedisCache<String, String> c = new RedisCache<>("prefix", 60, redisTemplate);
        assertEquals("prefix", c.getName());
    }

    @Test
    public void testConstructorWithZeroTtlSetsMinusOne() {
        RedisCache<String, String> c = new RedisCache<>("prefix", 0, redisTemplate);
        assertEquals("prefix", c.getName());
    }

    @Test
    public void testConstructorWithNegativeTtlSetsMinusOne() {
        RedisCache<String, String> c = new RedisCache<>("prefix", -5, redisTemplate);
        assertEquals("prefix", c.getName());
    }

    @Test
    public void testGet() {
        when(valueOperations.get("test:key1")).thenReturn("value1");
        assertEquals("value1", cache.get("key1"));
    }

    @Test
    public void testGetReturnsNull() {
        when(valueOperations.get("test:missing")).thenReturn(null);
        assertNull(cache.get("missing"));
    }

    @Test
    public void testPutWithTtl() {
        cache.put("key1", "value1");
        verify(valueOperations).set("test:key1", "value1", 300, TimeUnit.SECONDS);
    }

    @Test
    public void testPutWithoutTtl() {
        RedisCache<String, String> noTtlCache = new RedisCache<>("test", -1, redisTemplate);
        noTtlCache.put("key1", "value1");
        verify(valueOperations).set("test:key1", "value1");
    }

    @Test
    public void testContainsKey() {
        when(redisTemplate.hasKey("test:key1")).thenReturn(true);
        assertTrue(cache.containsKey("key1"));
    }

    @Test
    public void testContainsKeyNotFound() {
        when(redisTemplate.hasKey("test:key1")).thenReturn(false);
        assertFalse(cache.containsKey("key1"));
    }

    @Test
    public void testGetAndPut() {
        when(valueOperations.get("test:key1")).thenReturn("oldValue");
        String old = cache.getAndPut("key1", "newValue");
        assertEquals("oldValue", old);
        verify(valueOperations).set("test:key1", "newValue", 300, TimeUnit.SECONDS);
    }

    @Test
    public void testPutIfAbsentWhenAbsent() {
        when(valueOperations.get("test:key1")).thenReturn(null);
        assertTrue(cache.putIfAbsent("key1", "value1"));
        verify(valueOperations).set("test:key1", "value1", 300, TimeUnit.SECONDS);
    }

    @Test
    public void testPutIfAbsentWhenPresent() {
        when(valueOperations.get("test:key1")).thenReturn("existing");
        assertFalse(cache.putIfAbsent("key1", "value1"));
    }

    @Test
    public void testRemove() {
        assertTrue(cache.remove("key1"));
        verify(redisTemplate).delete("test:key1");
    }

    @Test
    public void testRemoveWithValue() {
        assertTrue(cache.remove("key1", "value1"));
        verify(redisTemplate).delete("test:key1");
    }

    @Test
    public void testGetAndRemove() {
        when(valueOperations.get("test:key1")).thenReturn("value1");
        assertEquals("value1", cache.getAndRemove("key1"));
        verify(redisTemplate).delete("test:key1");
    }


    @Test
    public void testPutAll() {
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("k1", "v1");
        entries.put("k2", "v2");
        cache.putAll(entries);
        verify(valueOperations).set("test:k1", "v1", 300, TimeUnit.SECONDS);
        verify(valueOperations).set("test:k2", "v2", 300, TimeUnit.SECONDS);
    }


}
