/*
 * Copyright (c) 2019 by European Commission
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
package no.idporten.eidas.redis.cache.metadata;

import eu.eidas.auth.engine.metadata.EidasMetadataParametersI;
import eu.eidas.auth.engine.metadata.IClearableCachingService;
import eu.eidas.auth.engine.metadata.IMetadataCachingService;
import no.idporten.eidas.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisTemplate;

import javax.cache.Cache;

/**
 * Abstract class that implements getter and setter for {@link EidasMetadataParametersI}
 */
public abstract class AbstractMetadataCaching<K,V> extends RedisCache<K,V> implements IMetadataCachingService, IClearableCachingService {

    protected AbstractMetadataCaching(String prefix, RedisTemplate<String, V> redisTemplate) {
        super(prefix, redisTemplate);
    }

    /**
     * Getter for the map of url, {@link EidasMetadataParametersI} pairs.
     *
     * @return the instance of the map
     */
    protected abstract Cache<String, EidasMetadataParametersI> getCache();

    /**
     * Gets the instance of {@link EidasMetadataParametersI}
     * related to url that is in {@link AbstractMetadataCaching#getCache()}.
     *
     * @param url the URL that contains the eIDAS Metadata
     * @return the instance of {@link EidasMetadataParametersI} that contains the metadata parameters
     */
    @Override
    public final EidasMetadataParametersI getEidasMetadataParameters(String url) {
        if(getCache()!=null){
            return getCache().get(url);
        }
        return null;
    }

    /**
     * Puts the instance of {@link EidasMetadataParametersI} received as parameter
     * in {@link AbstractMetadataCaching#getCache()} with the key of the map being url
     *
     * @param url the URL to be used as key in the map
     * @param eidasMetadataParameters the instance of {@link EidasMetadataParametersI} to be put as value in the map
     *
     */
    @Override
    public final void putEidasMetadataParameters(String url, EidasMetadataParametersI eidasMetadataParameters) {
        if( getCache() != null){
            if(eidasMetadataParameters == null) {
                getCache().remove(url);
            } else {
                getCache().put(url, eidasMetadataParameters);
            }
        }
    }

    /**
     * Allows for procedural clearing of the cache
     * not needed in dev-node as in-memory collections get  cleared by reloading application context
     * yet this is not the responsibility of the interface
     */
    @Override
    public void clearCachingService() {
        if( getCache() != null) {
            getCache().clear();
        }
    }
}
