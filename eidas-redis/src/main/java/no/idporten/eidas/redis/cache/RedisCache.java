package no.idporten.eidas.redis.cache;

import eu.eidas.auth.commons.cache.ConcurrentCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class RedisCache<K, V> implements Cache<K, V>, ConcurrentCacheService {
    protected String cachePrefix;

    private final RedisTemplate<String, V> redisTemplate;

    @Override
    public V get(K key) {
        return get(key);
    }

    private V get(String key) {
        try {
            return redisTemplate.opsForValue().get(keyWithPrefix(key));
        } catch (RedisConnectionFailureException | QueryTimeoutException e) {
            log.error("Failed to get {} object from cache: {}", key, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean containsKey(K key) {
        try {
            return redisTemplate.hasKey(keyWithPrefix(key));
        } catch (RedisConnectionFailureException | QueryTimeoutException e) {
            log.error("Failed to check presence of key in cache: {}", key, e.getMessage());
            throw e;
        }
    }

    @Override
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(K key, V value) {
        try {
            redisTemplate.opsForValue().set(keyWithPrefix(key), value);
        } catch (RedisConnectionFailureException | QueryTimeoutException e) {
            log.error("Failed to set {} object in cache: {}", key, e.getMessage());
            throw e;
        }
    }

    @Override
    public V getAndPut(K key, V value) {
        try {
            V oldValue = this.get(key);
            put(key, value);
            return oldValue;
        } catch (Exception e) {
            log.error("Failed to replace {} object in cache: {}", key, e.getMessage());
            throw e;
        }
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> set) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        try {
            map.forEach(this::put);
        } catch (Exception e) {
            log.error("Failed to put all entries into cache", e);
            throw e;
        }
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(keyWithPrefix(key), value);
        } catch (Exception e) {
            log.error("Failed to set {} if absent in cache: {}", key, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean remove(K key) {
        try {
            redisTemplate.delete(keyWithPrefix(key));
            return true;
        } catch (Exception e) {
            log.error("Failed to remove {} from cache: {}", key, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean remove(K key, V value) {
        try {
            redisTemplate.delete(keyWithPrefix(key));
            return true;
        } catch (Exception e) {
            log.error("Failed to remove {} from cache: {}", key, e.getMessage());
            throw e;
        }
    }

    @Override
    public V getAndRemove(K key) {
        try {
            V value = this.get(key);  // Retrieve the current value
            this.remove(key);         // Attempt to remove the key
            return value;        // Return the value that was removed
        } catch (Exception e) {
            log.error("Failed to remove and get {} from cache: {}", key, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean replace(K key, V value, V newValue) {
        try {
            if (this.containsKey(key) && this.get(key).equals(value)) {
                this.put(key, newValue);
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            log.error("Failed to replace {} in cache: {}", key, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean replace(K key, V value) {
        this.getAndPut(key, value);
        return true;
    }

    @Override
    public V getAndReplace(K key, V value) {
        return this.getAndPut(key, value);
    }

    @Override
    public void removeAll(Set<? extends K> set) {
        try {
            set.forEach(this::remove);
        } catch (Exception e) {
            log.error("Failed to remove all specified keys from cache", e);
            throw e;
        }
    }

    @Override
    public void removeAll() {
        try {
            this.clear();
        } catch (Exception e) {
            log.error("Failed to remove all specified keys from cache", e);
            throw e;
        }
    }

    @Override
    public void clear() {
        try {
            // Fetch all keys that match the prefix pattern
            Set<String> keys = redisTemplate.keys(cachePrefix + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }

        } catch (Exception e) {
            log.error("Failed to clear cache", e);
            throw e;
        }
    }

    @Override
    public <C extends Configuration<K, V>> C getConfiguration(Class<C> aClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... objects) throws EntryProcessorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> set, EntryProcessor<K, V, T> entryProcessor, Object... objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CacheManager getCacheManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosed() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        throw new UnsupportedOperationException();
    }


    @Override
    public Cache getConfiguredCache() {
        return this;
    }

    private String keyWithPrefix(K key) {
        return keyWithPrefix(key);
    }

    private String keyWithPrefix(String key) {
        return String.format("%s:%s", cachePrefix, key);
    }


    public String getCachePrefix() {
        return cachePrefix;
    }

    public void setCachePrefix(String cachePrefix) {
        this.cachePrefix = cachePrefix;
    }
}
