/*
 * Copyright (c) 2020 by European Commission
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import javax.cache.Cache;

/**
 * implements a caching service using hashmap
 */
@Slf4j
public final class SimpleMetadataCaching extends AbstractMetadataCaching<String, EidasMetadataParametersI> {

    public SimpleMetadataCaching(java.lang.String prefix, RedisTemplate<java.lang.String, EidasMetadataParametersI> redisTemplate) {
        super(prefix, redisTemplate);
        log.info("SimpleMetadataCaching");
    }


    @Override
    protected Cache<java.lang.String, eu.eidas.auth.engine.metadata.EidasMetadataParametersI> getCache()  {
        return  this;
    }

}
