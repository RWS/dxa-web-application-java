package org.dd4t.providers.impl;

import com.tridion.broker.StorageException;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.meta.PublicationMeta;
import com.tridion.meta.PublicationMetaFactory;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.CacheType;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.factories.impl.CacheProviderFactoryImpl;
import org.dd4t.core.providers.BaseBrokerProvider;
import org.dd4t.providers.CacheProvider;
import org.dd4t.providers.PublicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class BrokerPublicationProvider extends BaseBrokerProvider implements PublicationProvider {
	private static final DynamicMetaRetriever DYNAMIC_META_RETRIEVER = new DynamicMetaRetriever();
	private static final PublicationMetaFactory PUBLICATION_META_FACTORY = new PublicationMetaFactory();
	private static final Logger LOG = LoggerFactory.getLogger(BrokerPublicationProvider.class);
	private final CacheProvider cacheProvider = CacheProviderFactoryImpl.getInstance().getCacheProvider();

	public int discoverPublicationId (final String url) throws SerializationException {
		LOG.debug("Discovering Publication id for url: {}", url);
		final String key = getKey(CacheType.DISCOVER_PUBLICATION_URL, url);
		final CacheElement<Integer> cacheElement = cacheProvider.loadFromLocalCache(key);
		Integer result = -1;

		if (cacheElement.isExpired()) {
			synchronized (cacheElement) {
				if (cacheElement.isExpired()) {
					cacheElement.setExpired(false);

					final com.tridion.meta.PageMeta pageMeta = DYNAMIC_META_RETRIEVER.getPageMetaByURL(url);
					if (pageMeta != null) {
						result = pageMeta.getPublicationId();
						LOG.debug("Publication Id for URL: {}, is {}", url,result);
					} else {
						LOG.warn("Could not resolve publication Id for URL: {}",url);
					}

					cacheElement.setPayload(result);
					cacheProvider.storeInItemCache(key, cacheElement);
					LOG.debug("Stored Publication Id with key: {} in cache", key);
				} else {
					LOG.debug("Fetched a Publication Id with key: {} from cache", key);
					result = cacheElement.getPayload();
				}
			}
		} else {
			LOG.debug("Fetched Publication Id with key: {} from cache", key);
			result = cacheElement.getPayload();
		}

		return result == null ? -1 : result;
	}

	// TODO: cache metas and expand
	@Override public String discoverPublicationUrl (int publicationId) {
		try {
			PublicationMeta publicationMeta = PUBLICATION_META_FACTORY.getMeta(publicationId);
			return publicationMeta.getPublicationUrl();
		} catch (StorageException e) {
			LOG.error(e.getLocalizedMessage(),e);
		}
		return null;
	}
}
