package org.dd4t.providers.impl;

import com.tridion.meta.BinaryMeta;
import com.tridion.meta.PageMeta;
import com.tridion.meta.PublicationMeta;
import org.dd4t.contentmodel.PublicationDescriptor;
import org.dd4t.caching.CacheElement;
import org.dd4t.caching.CacheType;
import org.dd4t.providers.BaseBrokerProvider;
import org.dd4t.providers.PublicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractPublicationProvider
 *
 * This class duplicates quite a bit of code, but there's no way around it
 * in terms of dependency differences between 2013 SP 1 and Web 8
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

    protected PublicationMeta getPublicationMeta (final int publicationId) {

        final String key = getKey(CacheType.PUBLICATION_META, Integer.toString(publicationId));
        final CacheElement<PublicationMeta> cacheElement = cacheProvider.loadPayloadFromLocalCache(key);

        PublicationMeta publicationMeta;

        if (cacheElement.isExpired()) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (cacheElement) {
                if (cacheElement.isExpired()) {

                    publicationMeta = loadPublicationMetaByConcreteFactory(publicationId);
                    if (publicationMeta != null) {
                        cacheElement.setPayload(publicationMeta);
                        cacheProvider.storeInItemCache(key, cacheElement);
                        cacheElement.setExpired(false);
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

    protected abstract BinaryMeta loadBinaryMetaByConcreteFactory(final String url);
}
