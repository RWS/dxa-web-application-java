package org.dd4t.core.resolvers.impl;

import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.resolvers.PublicationResolver;
import org.dd4t.core.util.HttpUtils;
import org.dd4t.core.util.PublicationDescriptor;
import org.dd4t.providers.PublicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * dd4t-2
 *
 * @author R. Kempees, Q. Slings
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
			final HttpServletRequest request = HttpUtils.currentRequest();
			return publicationProvider.discoverPublicationId(request.getRequestURL().toString());
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
	 * Gets the Publication Path property as defined in Tridion Publication metadata corresponding to the current request
	 *
	 * @return String representing the SDL Tridion Publication Path metadata property
	 */
	@Override public String getPublicationPath () {
		return publicationProvider.discoverPublicationPath(getPublicationId());
	}

	/**
	 * Gets the Images URL property as defined in Tridion Publication metadata corresponding to the current request
	 *
	 * @return String representing the SDL Tridion Images URL metadata property
	 */
	@Override public String getImagesUrl () {
		return publicationProvider.discoverImagesUrl(getPublicationId());
	}

	/**
	 * Gets the Images Path property as defined in Tridion Publication metadata corresponding to the current request
	 *
	 * @return String representing the SDL Tridion Images Path metadata property
	 */
	@Override public String getImagesPath () {
		return publicationProvider.discoverImagesPath(getPublicationId());
	}

	/**
	 * Gets the Page URL in the current Publication corresponding to the given generic URL
	 *
	 * @param url String representing the generic URL (i.e. URL path without PublicationUrl prefix)
	 * @return String representing the current Publication URL followed by the given URL
	 */
	@Override public String getLocalPageUrl (final String url) {
		String publicationUrl = publicationProvider.discoverPublicationUrl(getPublicationId());
		return url.replaceFirst(publicationUrl,"");
	}

	/**
	 * TODO: check with Q whether this is allright
	 * Gets the Binary URL in the current Publication corresponding to the given generic URL
	 *
	 * @param url String representing the generic URL (i.e. URL path without PublicationUrl prefix)
	 * @return String representing the current Publication URL followed by the given URL
	 */
	@Override public String getLocalBinaryUrl (final String url) {
		String binaryUrl = publicationProvider.discoverImagesUrl(getPublicationId());
		return url.replaceFirst(binaryUrl,"");
	}

	/**
	 * For use in the RS scenario.
	 * @return a publication descriptor
	 */
	@Override public PublicationDescriptor getPublicationDescriptor () {
		return publicationProvider.getPublicationDescriptor(getPublicationId());
	}

	public void setPublicationProvider (final PublicationProvider publicationProvider) {
		this.publicationProvider = publicationProvider;
	}
}
