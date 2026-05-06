package no.idporten.eidas.redis.cache;

import lombok.extern.slf4j.Slf4j;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class EidasRedisCacheManager implements CacheManager {

    private final Map<String, Cache<?, ?>> caches = new ConcurrentHashMap<>();
    private boolean closed = false;

    public EidasRedisCacheManager(Map<String, Cache<?, ?>> functionalToCacheBean) {
        if (functionalToCacheBean != null) {
            functionalToCacheBean.forEach((functionalName, cache) -> {
                log.info("Registering cache bean for functional name: {}", functionalName);
                if(cache instanceof RedisCache<?,?> redisCache) {
                   redisCache.setCacheManager(this);
                }

            });
            caches.putAll(functionalToCacheBean);
        }
        log.info("RedisCacheManager created with {} cache bean mappings", caches.size());
    }

    public void addCache(String functionalName, Cache<?, ?> cache) {
        caches.put(functionalName, cache);
        log.info("Registered cache bean for functional name: {}", functionalName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String functionalName, Class<K> keyType, Class<V> valueType) {
        return getCache(functionalName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getCache(String functionalName) {
        Cache<?, ?> cache = caches.get(functionalName);
        if (cache == null) {
            log.warn("No cache bean registered for functional name: {}", functionalName);
            throw new IllegalStateException("Cache configuration mismatch for: " + functionalName);
        }
        log.debug("Returning cache for functional name: {}", functionalName);
        return (Cache<K, V>) cache;
    }

    @Override
    public Iterable<String> getCacheNames() {
        return caches.keySet();
    }

    @Override
    public void destroyCache(String cacheName) {
        Cache<?, ?> cache = caches.remove(cacheName);
        if (cache != null) {
            cache.close();
            log.info("Destroyed cache with name: {}", cacheName);
        } else {
            log.warn("No cache registered with name: {} to destroy", cacheName);
        }
    }

    @Override
    public void enableManagement(String cacheName, boolean enabled) {
        log.info("enableManagement is not supported in RedisCacheManager");
    }

    @Override
    public void enableStatistics(String cacheName, boolean enabled) {
        log.info("enableStatistics is not supported in RedisCacheManager");
    }

    @Override
    public void close() {
        if (!closed) {
            caches.values().forEach(Cache::close);
            caches.clear();
            closed = true;
            log.info("RedisCacheManager closed");
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        if (clazz.isAssignableFrom(this.getClass())) {
            return clazz.cast(this);
        }
        throw new UnsupportedOperationException("Unwrap to " + clazz.getName() + " is not supported");
    }

    @Override
    public CachingProvider getCachingProvider() {
        return null;
    }

    @Override
    public URI getURI() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration) throws IllegalArgumentException {
        throw new UnsupportedOperationException("createCache is not supported");
    }
}
