package org.dd4t.providers.impl;

import org.dd4t.providers.LinkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tridion.linking.ComponentLink;
import com.tridion.linking.Link;
import com.tridion.util.TCMURI;

/**
 * Provider implementation to wrap around the ComponentLinker.
 * 
 * @author rooudsho
 *
 */
public class BrokerLinkProvider implements LinkProvider {
	private static Logger logger = LoggerFactory.getLogger(BrokerLinkProvider.class);

	@Override
	public String resolveComponent(String componentId) {
		try {
			TCMURI tcmUri = new TCMURI(componentId);
			ComponentLink clink = new ComponentLink(tcmUri.getPublicationId());
			Link link = clink.getLink(tcmUri.getItemId()); 

			if (link.isResolved()) {
				return link.getURL();
			}		
		} catch (Exception ex) {
			logger.error("Unable to resolve link "+ex.getMessage(), ex);
		}
	
		return null;
	}

	@Override
	public String resolveComponentFromPage(String componentId, String pageId) {
		TCMURI tcmUri;
		try {
			tcmUri = new TCMURI(componentId);
			
			ComponentLink clink = new ComponentLink(tcmUri.getPublicationId());
			
			Link link = clink.getLink(pageId, componentId, "tcm:0-0-0", "", "", true, false); 

			if (link.isResolved()) {
				return link.getURL();
			}
		} catch (Exception ex) {
			logger.error("Unable to resolve link "+ex.getMessage(), ex);
		}
		
		return null;
	}

}
