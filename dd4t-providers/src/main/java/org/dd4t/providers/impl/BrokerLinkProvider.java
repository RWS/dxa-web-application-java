package org.dd4t.providers.impl;

import com.tridion.linking.ComponentLink;
import com.tridion.linking.Link;
import com.tridion.util.TCMURI;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.providers.LinkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider implementation to wrap around the ComponentLinker.
 *
 * @author rooudsho, Mihai Cadariu
 */
public class BrokerLinkProvider implements LinkProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerLinkProvider.class);

	/**
	 * Returns a link URL to the given Component TcmUri, if exists. Otherwise, returns null.
	 *
	 * @param targetComponentURI String representing the TcmUri of the Component to resolve a link to
	 * @return String representing the URL of the link; or null, if the Component is not linked to
	 */
	@Override
	public String resolveComponent(String targetComponentURI) {
		try {
			TCMURI componentURI = new TCMURI(targetComponentURI);
			ComponentLink componentLink = new ComponentLink(componentURI.getPublicationId());
			Link link = componentLink.getLink(componentURI.getItemId());

			if (link.isResolved()) {
				return link.getURL();
			}
		} catch (Exception ex) {
			LOG.error("Unable to resolve link to " + targetComponentURI + ": " + ex.getMessage(), ex);
		}

		return null;
	}

	/**
	 * Returns a link URL to the given Component TcmUri, when also specifying the source Page TcmUri.
	 *
	 * @param targetComponentURI String representing the TcmUri of the Component to resolve a link to
	 * @param sourcePageURI      String representing the TcmUri of the source Page (the current page)
	 * @return String representing the URL of the link; or null, if the Component is not linked to
	 */
	@Override
	public String resolveComponentFromPage(String targetComponentURI, String sourcePageURI) {
		try {
			TCMURI componentURI = new TCMURI(targetComponentURI);
			ComponentLink componentLink = new ComponentLink(componentURI.getPublicationId());
			Link link = componentLink.getLink(sourcePageURI, targetComponentURI, "tcm:0-0-0", "", "", true, false);

			if (link.isResolved()) {
				return link.getURL();
			}
		} catch (Exception ex) {
			LOG.error("Unable to resolve link to " + targetComponentURI + ": " + ex.getMessage(), ex);
		}

		return null;
	}

	/**
	 * TODO
	 * @param targetComponentUri String representing the TcmUri of the Component to resolve a link to
	 * @param componentTemplateUri The Component Presentation to exclude
	 * @return
	 * @throws ItemNotFoundException
	 * @throws SerializationException
	 */
	@Override public String resolveComponent (final String targetComponentUri, final String componentTemplateUri) throws ItemNotFoundException, SerializationException {
		return null;
	}
}
