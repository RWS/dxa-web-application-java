package com.sdl.webapp.tridion.xpm.filter;

import com.sdl.web.ambient.client.DefaultAmbientServiceClient;
import com.sdl.web.ambient.client.api.AmbientServiceClient;
import com.sdl.web.content.client.impl.ContentClientProvider;
import com.tridion.ambientdata.AmbientDataException;
import com.tridion.ambientdata.claimstore.ClaimStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;

/**
 * Incorporated this class from the udp library in order to update it with the Jakarta Servlet API.
 * AmbientClientFilter - Client side hook for ADF service.
 */
public class AmbientClientFilter extends AbstractAmbientDataServletFilter {

    private static final Logger LOG = LoggerFactory.getLogger(AmbientClientFilter.class);

    private AmbientServiceClient ambientService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        // If CIS is lower than 8.2, we need to un-escape quotes from the serialized claimstore.
        LOG.debug("Checking if CIS version is higher than 8.2 to skip un-escaping quotes.");
        boolean skipDeEscapeQuotes = ContentClientProvider.getInstance().getContentClient().isHigherCISVersion("8.2");
        this.ambientService = new DefaultAmbientServiceClient(skipDeEscapeQuotes);
        if (!skipDeEscapeQuotes) {
            LOG.info("Client library version is higher than Content Interaction Service.");
        }
    }

    private String deEscapeQuotes(String serializedClaimStore) {
        return serializedClaimStore.replaceAll("\\\\\\\\", "\\\\").replaceAll("\\\\\"", "\"");
    }

    @Override
    protected void initializeEngine() throws AmbientDataException {
        // Engine currently only implemented at server side
    }

    @Override
    protected ClaimStore processStartEvents(ClaimStore claimStore, boolean sessionIsNew) {
        return this.ambientService.processStartEvents(claimStore, sessionIsNew);
    }

    @Override
    protected ClaimStore processEndEvents(ClaimStore claimStore) {
        return this.ambientService.processEndEvents(claimStore);
    }
}
