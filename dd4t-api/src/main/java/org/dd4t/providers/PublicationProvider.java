package org.dd4t.providers;

import org.dd4t.core.exceptions.SerializationException;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public interface PublicationProvider {
	public int discoverPublicationId(final String url) throws SerializationException;
	public String discoverPublicationUrl(final int publicationId);
}
