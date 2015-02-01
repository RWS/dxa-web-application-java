package org.dd4t.providers.rs;

import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.providers.PublicationProvider;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class BrokerPublicationProvider extends BaseBrokerProvider implements PublicationProvider {
	@Override public int discoverPublicationId (final String url) throws SerializationException {
		return 0;
	}

	@Override public String discoverPublicationUrl (final int publicationId) {
		return null;
	}
}
