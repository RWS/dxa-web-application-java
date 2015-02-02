package org.dd4t.providers;

import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.PublicationDescriptor;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public interface PublicationProvider {
	int discoverPublicationId(final String url) throws SerializationException;
	String discoverPublicationUrl(final int publicationId);

	String discoverPublicationPath (int publicationId);

	String discoverImagesUrl (int publicationId);

	String discoverImagesPath (int publicationId);

	String discoverPublicationTitle (int publicationId);

	String discoverPublicationKey (int publicationId);

	PublicationDescriptor getPublicationDescriptor(int publicationId);
}
