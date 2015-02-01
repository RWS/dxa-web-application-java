package org.dd4t.core.resolvers.impl;

import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.resolvers.PublicationResolver;
import org.dd4t.core.util.HttpUtils;
import org.dd4t.providers.PublicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class UrlPublicationResolver implements PublicationResolver {
	private static final Logger LOG = LoggerFactory.getLogger(UrlPublicationResolver.class);
	private PublicationProvider publicationProvider;


	/**
	 * Gets the Publication TCMURI item id for the current request
	 *
	 * @return int representing the SDL Tridion Publication item id
	 */
	@Override public int getPublicationId () {
		try {
			return publicationProvider.discoverPublicationId(HttpUtils.getCurrentURL(HttpUtils.currentRequest()));
		} catch (SerializationException e) {
			LOG.error(e.getLocalizedMessage(),e);
		}
		return -1;
	}

	/**
	 * Gets the Publication Url property as defined in Tridion Publication metadata corresponding to the current request
	 *
	 * @return String representing the SDL Tridion Publication Url metadata property
	 */
	@Override public String getPublicationUrl () {
		return publicationProvider.discoverPublicationUrl(getPublicationId());
	}

	/**
	 * Gets the Images URL property as defined in Tridion Publication metadata corresponding to the current request
	 *
	 * @return String representing the SDL Tridion Images URL metadata property
	 */
	@Override public String getImagesUrl () {
		return null;
	}

	/**
	 * Gets the Page URL in the current Publication corresponding to the given generic URL
	 *
	 * @param url String representing the generic URL (i.e. URL path without PublicationUrl prefix)
	 * @return String representing the current Publication URL followed by the given URL
	 */
	@Override public String getLocalPageUrl (final String url) {
		return null;
	}

	/**
	 * Gets the Binary URL in the current Publication corresponding to the given generic URL
	 *
	 * @param url String representing the generic URL (i.e. URL path without PublicationUrl prefix)
	 * @return String representing the current Publication URL followed by the given URL
	 */
	@Override public String getLocalBinaryUrl (final String url) {
		return null;
	}

	public void setPublicationProvider (final PublicationProvider publicationProvider) {
		this.publicationProvider = publicationProvider;
	}
}
