package org.dd4t.providers;

import com.tridion.meta.PageMeta;
import com.tridion.meta.PublicationMeta;
import org.dd4t.contentmodel.PublicationDescriptor;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.CacheType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public abstract class AbstractPublicationProvider extends BaseBrokerProvider implements PublicationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPublicationProvider.class);
    protected Class publicationDescriptor;

    @Override
    public String discoverPublicationUrl (int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }
        return publicationMeta.getPublicationUrl();
    }

    @Override
    public String discoverPublicationPath (int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }
        return publicationMeta.getPublicationPath();
    }

    @Override
    public String discoverImagesUrl (int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }
        return publicationMeta.getMultimediaUrl();
    }

    @Override
    public String discoverImagesPath (int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }
        return publicationMeta.getMultimediaPath();
    }

    @Override
    public String discoverPublicationTitle (int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }
        return publicationMeta.getTitle();
    }

    @Override
    public String discoverPublicationKey (int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }
        return publicationMeta.getKey();
    }

    //TODO: Document
    @Override
    public int discoverPublicationIdByPageUrlPath (final String url) {
        LOG.debug("Discovering Publication id for url: {}", url);
        final String key = getKey(CacheType.DISCOVER_PUBLICATION_URL, url);
        final CacheElement<Integer> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);
        Integer result = -1;

        if (cacheElement.isExpired()) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {
                    cacheElement.setExpired(false);

                    final PageMeta pageMeta = loadPageMetaByConcreteFactory(url);
                    if (pageMeta != null) {
                        result = pageMeta.getPublicationId();
                        LOG.debug("Publication Id for URL: {}, is {}", url, result);
                    } else {
                        LOG.warn("Could not resolve publication Id for URL: {}", url);
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

    protected PublicationMeta getPublicationMeta (final int publicationId) {

        final String key = getKey(CacheType.PUBLICATION_META, Integer.toString(publicationId));
        final CacheElement<PublicationMeta> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);

        PublicationMeta publicationMeta = null;

        if (cacheElement.isExpired()) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {

                    publicationMeta = loadPublicationMetaByConcreteFactory(publicationId);
                    if (publicationMeta != null) {
                        cacheElement.setExpired(false);
                        cacheElement.setPayload(publicationMeta);
                        cacheProvider.storeInItemCache(key, cacheElement);
                        LOG.debug("Stored Publication Meta with key: {} in cache", key);
                    } else {
                        LOG.warn("No Publication Meta found for publication Id: {}", publicationId);
                        // TODO: cache nulls?
                    }

                } else {
                    LOG.debug("Fetched a Publication Meta with key: {} from cache", key);
                    publicationMeta = cacheElement.getPayload();
                }
            }
        } else {
            LOG.debug("Fetched a Publication Meta with key: {} from cache", key);
            publicationMeta = cacheElement.getPayload();
        }

        if (publicationMeta == null) {
            LOG.error("Could not find Publication Meta for publication id: {}", publicationId);
            return null;
        }

        return publicationMeta;
    }

    /**
     * @param publicationId the publication Id
     * @return a Publication descriptor
     */
    @Override
    public PublicationDescriptor getPublicationDescriptor (final int publicationId) {
        final PublicationMeta publicationMeta = getPublicationMeta(publicationId);
        if (publicationMeta == null) {
            return null;
        }

        try {
            final PublicationDescriptor concretePublicationDescriptor = (PublicationDescriptor) publicationDescriptor.newInstance();
            concretePublicationDescriptor.setId(publicationMeta.getId());
            concretePublicationDescriptor.setKey(publicationMeta.getKey());
            concretePublicationDescriptor.setPublicationUrl(publicationMeta.getPublicationUrl());
            concretePublicationDescriptor.setPublicationPath(publicationMeta.getPublicationPath());
            concretePublicationDescriptor.setMultimediaUrl(publicationMeta.getMultimediaUrl());
            concretePublicationDescriptor.setMultimediaPath(publicationMeta.getMultimediaPath());
            concretePublicationDescriptor.setTitle(publicationMeta.getTitle());
            return concretePublicationDescriptor;
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }

        return null;
    }


    public void setPublicationDescriptor (final Class publicationDescriptor) {
        this.publicationDescriptor = publicationDescriptor;
    }

    public Class getPublicationDescriptor () {
        return publicationDescriptor;
    }

    protected abstract PageMeta loadPageMetaByConcreteFactory (final String url);

    protected abstract PublicationMeta loadPublicationMetaByConcreteFactory (final int publicationId);
}
