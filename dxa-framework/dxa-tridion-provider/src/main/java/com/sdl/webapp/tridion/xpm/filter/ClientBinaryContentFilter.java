package com.sdl.webapp.tridion.xpm.filter;

import com.sdl.odata.client.FunctionImportClientQuery;
import com.sdl.odata.client.api.ODataClientQuery;
import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.web.api.dynamic.BinaryContentRetrieverImpl;
import com.sdl.web.broker.serialization.item.BinaryMetaSerializer;
import com.sdl.web.content.client.dataloader.ODataClientDataLoader;
import com.sdl.web.content.client.impl.ContentClient;
import com.sdl.web.content.client.impl.ContentClientProvider;
import com.sdl.web.preview.client.session.ClientSessionManagerHolder;
import com.sdl.web.preview.client.session.MachineNameProvider;
import com.sdl.web.preview.client.util.ClientSessionContentHandler;
import com.sdl.web.preview.model.PreviewSession;
import com.sdl.web.preview.model.PreviewSessionItem;
import com.sdl.web.preview.session.CommonSessionManager;
import com.sdl.web.preview.util.SessionContentHandler;
import com.sdl.web.preview.util.SessionHandlingException;
import com.tridion.data.BinaryData;
import com.tridion.meta.BinaryMeta;
import com.tridion.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.sdl.web.preview.util.ObjectKeyUtil.buildObjectKey;
import static com.sdl.web.preview.util.PathUtils.buildOutputPath;
import static com.sdl.web.preview.util.PreviewSessionClaims.SESSION_BINARY_FULL_PATH;
import static com.sdl.web.util.ContentServiceQueryConstants.QUERY_PARAM_ITEM_TYPE;
import static com.sdl.web.util.ContentServiceQueryConstants.QUERY_PARAM_URL;

/**
 * Incorporated this class from the udp library in order to update it with the Jakarta Servlet API.
 * Client implementation of the {@link BinaryContentFilter}.
 */
public class ClientBinaryContentFilter extends BinaryContentFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ClientBinaryContentFilter.class);

    private String machineName;
    private CommonSessionManager sessionManager = ClientSessionManagerHolder.getCommonSessionManager();

    private BinaryContentRetriever retriever = new BinaryContentRetrieverImpl();
    private SessionContentHandler sessionContentHandler = ClientSessionContentHandler.getInstance();

    @Override
    protected void doInit() throws SessionHandlingException {
        machineName = MachineNameProvider.getMachineName();
    }

    @Override
    protected String handleBinaryData(PreviewSession session, BinaryData data, String realPath, String rootPath)
            throws SessionHandlingException, IOException {

        PreviewSessionItem sisBC = sessionManager.getSessionItem(session, buildObjectKey(data), machineName);
        String fullOutputPath = buildOutputPath(realPath, session.getPreviewSessionId());
        String realFileName = fullOutputPath.substring(rootPath.length()).replace('\\', '/');

        sessionContentHandler.updateSessionStateIfRequired(sisBC, data.getBytes(), fullOutputPath);
        getClaimStore().put(SESSION_BINARY_FULL_PATH, fullOutputPath);

        return realFileName;
    }

    protected PreviewSession obtainPreviewSession() {
        // check if session wrappers are available, otherwise don't write to the FS
        if (!sessionContentHandler.checkIfSessionWrappersAreAvailable()) {
            return null;
        }
        if (getClaimStore() == null) {
            return null;
        }
        return sessionManager.getCurrentSession();
    }

    protected BinaryData fetchBinaryData(String url) {
        ODataClientQuery query = new FunctionImportClientQuery.Builder()
                .withEntityType(String.class)
                .withFunctionName("GetDynamicMetaFunctionImport")
                .withFunctionParameter(QUERY_PARAM_URL, "'" + url + "'")
                .withFunctionParameter(QUERY_PARAM_ITEM_TYPE, "'BinaryMeta'").build();

        String binaryMetaResponse = (String) getContentClient().getEntity("Edm.String", query);
        if (!StringUtils.isNotEmpty(binaryMetaResponse)) {
            LOG.debug("Did not found binary meta for url {}.", url);
            return null;
        }

        BinaryMeta meta = BinaryMetaSerializer.fromJson(binaryMetaResponse, new ODataClientDataLoader());
        LOG.debug("Found binary metadata: {} for given url: {}", meta, url);
        return retriever.getBinary(meta.getPublicationId(), ((int) meta.getId()), meta.getVariantId());
    }

    @Override
    public void destroy() {
        // Cleanup session manager and hence cleanup session thread.
        sessionManager.shutDown();
    }

    protected ContentClient getContentClient() {
        return ContentClientProvider.getInstance().getContentClient();
    }
}
