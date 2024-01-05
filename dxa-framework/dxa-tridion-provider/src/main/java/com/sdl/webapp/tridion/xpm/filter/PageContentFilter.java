package com.sdl.webapp.tridion.xpm.filter;

import com.sdl.web.preview.model.PreviewSession;
import com.sdl.web.preview.util.PathUtils;
import com.sdl.web.preview.util.PreviewSessionClaims;
import com.sdl.web.preview.util.SessionHandlingException;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import com.tridion.broker.StorageException;
import com.tridion.data.CharacterData;
import com.tridion.meta.ComponentPresentationMeta;
import com.tridion.meta.PageMeta;
import com.tridion.util.TCMURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Incorporated this class from the udp library in order to update it with the Jakarta Servlet API.
 * PageContentFilter.
 */
public abstract class PageContentFilter extends ContentFilter {

    private static final Logger LOG = LoggerFactory.getLogger(PageContentFilter.class);

    /**
     * Separator used for ComponentPresentation output paths and for request defined cp's.
     */
    protected static final String SEPARATOR = "|";
    /**
     * Separator used for separating in a request defined ComponentPresentation the component TCMURI from
     * the template TCMURI.
     */
    protected static final String COMPONENT_TEMPLATE_SEPARATOR = "\\_";

    /**
     * Writes the Page content corresponding to the realPath provided. The Page file has concatenated to its name
     * the session id, so it doesn't overwrite any existing files. Changes the state of the item in the session, so it
     * includes the path of the file.
     *
     * @param fullURL The full url of the request.
     * @param realPath The real file path where the request should go.
     * @return relative path.
     */
    @Override
    public final String handleRequest(final String fullURL, final String realPath, final String rootPath) {
        return handleRequest(fullURL, realPath, rootPath, null);
    }

    /**
     * Writes the Page content corresponding to the realPath provided. The Page file has concatenated to its name
     * the session id so it doesn't overwrite any existing files. Changes the state of the item in the session so it
     * includes the path of the file.
     *
     * @param fullURL             The full url of the request.
     * @param realPath            The real file path where the request should go.
     * @param rootPath            The application root path.
     * @param virtualRelativePath The virtual relative path or {@code null} if no virtual paths are to be processed.
     *
     * @return The relative path
     */
    public final String handleRequest(final String fullURL, final String realPath, final String rootPath,
                                      String virtualRelativePath) {
        LOG.debug("Full URL: '{}'", fullURL);
        LOG.debug("Real path: '{}'", realPath);
        LOG.debug("Root path: '{}'", rootPath);
        LOG.debug("Virtual path: '{}'", virtualRelativePath);
        // check if session wrappers are available, otherwise don't write to the FS
        String realFileName = null;
        PreviewSession session = obtainPreviewSession();
        if (session == null) {
            LOG.debug("No session has been started!");
        }
        else {
            LOG.debug("The session {} was found loaded!", session.getPreviewSessionId());
            PageMeta pageMeta = getPageMetaByURL(fullURL);

            if (pageMeta == null) {
                LOG.debug("Did not found page meta for url {}.", fullURL);

                ClaimStore claimStore = AmbientDataContext.getCurrentClaimStore();
                if (claimStore != null) {
                    claimStore.put(PreviewSessionClaims.SESSION_PAGE_FULL_PATH, null);
                }

                if (hasRequestDefinedComponentPresentations()) {
                    try {
                        LOG.debug("Handling component presentations defined in this request");
                        handleRequestDefinedComponentPresentations(session);
                    }
                    catch (StorageException e) {
                        LOG.error("StorageException while processing request", e);
                    }
                }
                return null;
            }
            else {
                String outputFilePath = PathUtils.buildOutputPath(realPath, session.getPreviewSessionId());
                try {
                    CharacterData cdata = getCharacterDataForPage(pageMeta);

                    if (cdata == null) {
                        LOG.debug("Did not found page content for url {}.", fullURL);
                    }
                    else {
                        LOG.debug("Got page content for publicationId: {} and pageId: {}.", cdata.getPublicationId(),
                                cdata.getId());
                        realFileName = outputFilePath.substring(rootPath.length()).replace('\\', '/');
                        handleCharacterData(session, outputFilePath, cdata);
                    }

                    if (hasRequestDefinedComponentPresentations()) {
                        LOG.debug("Handling component presentations defined in this request");
                        handleRequestDefinedComponentPresentations(session);
                    }
                    else {
                        LOG.debug("Handling page related component presentations");
                        handleComponentPresentations(session, pageMeta.getComponentPresentationMetas());
                    }
                }
                catch (StorageException e) {
                    LOG.error("StorageException while processing request", e);
                    realFileName = null;
                }
                catch (SessionHandlingException e) {
                    LOG.error("SessionHandlingException while processing request", e);
                    new File(outputFilePath).delete();
                    realFileName = null;
                }
                LOG.debug("Real file name (before processing virtual path): '{}'", realFileName);
                LOG.debug("Virtual relative path: '{}'", virtualRelativePath);
                realFileName = (realFileName != null && virtualRelativePath != null)
                        ? virtualRelativePath + realFileName
                        : realFileName;
            }
        }
        LOG.debug("Real file name: '{}'", realFileName);
        return realFileName;
    }

    protected void putToClaimStoreIfAvailable(URI claimURI, Object valueToPut) {
        ClaimStore claimStore = AmbientDataContext.getCurrentClaimStore();
        if (claimStore != null) {
            claimStore.put(claimURI, valueToPut);
        }
    }

    /**
     * Handles component presentations from the passed list of metas.
     *
     * @param s The currently active session
     * @param cpMetas The list of metas of cps to be handled.
     * @throws StorageException If unable to retrieve the component presentations
     */
    protected abstract void handleComponentPresentations(PreviewSession s, List<ComponentPresentationMeta> cpMetas)
            throws StorageException;

    /**
     * Handles the ComponentPresentations specifically defined in this request.
     * @param session The currently active session
     * @throws StorageException If unable to retrieve the component presentations
     */
    private void handleRequestDefinedComponentPresentations(PreviewSession session) throws StorageException {
        String definedCPs = getClaimValue(PreviewSessionClaims.REQUEST_COMPONENT_PRESENTATIONS, null, String.class);
        LOG.debug("Found CP's specifically defined in this request: {}.", definedCPs);

        if (definedCPs == null) {
            LOG.debug("Ignoring CP's specifically defined in this request because the corresponding claim is not set.");
            return;
        }
        String[] cps = definedCPs.split("\\" + SEPARATOR);

        List<ComponentPresentationMeta> cpmList = new ArrayList<>();
        for (String cp : cps) {
            LOG.debug("Analyzing request specifically defined CP: {}.", cp);
            // separate the component id from the template id
            String[] parts = cp.split(COMPONENT_TEMPLATE_SEPARATOR);
            if (parts.length != 2) {
                LOG.debug("Ignoring specifically defined CP because it's format is not like " +
                          "componentTCMURI_templateTCMURI: {}.", cp);
                continue;
            }
            Integer pubId = null;
            Integer componentId = null;
            Integer templateId = null;

            try {
                TCMURI componentURI = new TCMURI(parts[0]);
                pubId = componentURI.getPublicationId();
                componentId = componentURI.getItemId();

                TCMURI templateURI = new TCMURI(parts[1]);
                templateId = templateURI.getItemId();
            }
            catch (ParseException e) {
                LOG.debug("Ignoring specifically defined CP because template TCMURI cannot be parsed: " +
                          "{}.", e.getMessage());
            }
            ComponentPresentationMeta meta = getComponentPresentationMeta(pubId, componentId, templateId);
            if (meta != null) {
                cpmList.add(meta);
            }
        }
        handleComponentPresentations(session, cpmList);
    }

    protected abstract ComponentPresentationMeta getComponentPresentationMeta(Integer pubId, Integer componentId,
                                                                              Integer tmplId) throws StorageException;

    /**
     * Tells if in the current request we specifically want to treat component presentations in a different than
     * the default Tridion way.
     *
     * @return True if we need to look only at the cp's specifically defined by this request (by a cartridge from ADF).
     */
    protected final boolean hasRequestDefinedComponentPresentations() {
        // let's get the current claimStore
        final ClaimStore claimStore = AmbientDataContext.getCurrentClaimStore();
        return claimStore != null && claimStore.get(PreviewSessionClaims.REQUEST_COMPONENT_PRESENTATIONS) != null;
    }

    protected abstract PageMeta getPageMetaByURL(String fullURL);

    protected abstract CharacterData getCharacterDataForPage(PageMeta pageMeta) throws StorageException;

    protected abstract void handleCharacterData(PreviewSession session, String outputFilePath,
                                                CharacterData cdata) throws SessionHandlingException;

    protected final String getClaimValue(URI claimUri, String defValue, Class<String> type) {
        ClaimStore claimStore = getClaimStore();
        String result;
        if (claimStore != null && claimStore.contains(claimUri)) {
            result = claimStore.get(claimUri, type);
        }
        else {
            result = defValue;
        }
        return result;
    }
}
