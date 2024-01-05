package com.sdl.webapp.tridion.xpm.filter;

import com.sdl.odata.client.api.ODataClientQuery;
import com.sdl.web.api.broker.WebComponentPresentationFactoryImpl;
import com.sdl.web.api.dynamic.DynamicMetaRetriever;
import com.sdl.web.api.dynamic.DynamicMetaRetrieverImpl;
import com.sdl.web.api.meta.WebComponentPresentationMetaFactory;
import com.sdl.web.api.meta.WebComponentPresentationMetaFactoryImpl;
import com.sdl.web.content.client.impl.ContentClient;
import com.sdl.web.content.client.impl.ContentClientProvider;
import com.sdl.web.content.client.impl.ODataV2ClientQuery;
import com.sdl.web.preview.client.model.PageContent;
import com.sdl.web.preview.client.session.ClientSessionManagerHolder;
import com.sdl.web.preview.client.session.MachineNameProvider;
import com.sdl.web.preview.client.util.ClientSessionContentHandler;
import com.sdl.web.preview.client.util.ComponentPresentationTypeEnum;
import com.sdl.web.preview.client.util.PreviewFileLocationResolver;
import com.sdl.web.preview.model.PreviewSession;
import com.sdl.web.preview.model.PreviewSessionItem;
import com.sdl.web.preview.session.CommonSessionManager;
import com.sdl.web.preview.util.SessionContentHandler;
import com.sdl.web.preview.util.SessionHandlingException;
import com.tridion.broker.StorageException;
import com.tridion.configuration.ConfigurationException;
import com.tridion.data.CharacterData;
import com.tridion.data.CharacterDataString;
import com.tridion.dcp.ComponentPresentation;
import com.tridion.meta.ComponentPresentationMeta;
import com.tridion.meta.PageMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sdl.web.preview.session.SessionItemState.LOADED;
import static com.sdl.web.preview.util.ObjectKeyUtil.buildObjectKey;
import static com.sdl.web.preview.util.PathUtils.buildOutputPath;
import static com.sdl.web.preview.util.PreviewSessionClaims.REQUEST_COMPONENT_PRESENTATION_ROOT_PATH;
import static com.sdl.web.preview.util.PreviewSessionClaims.SESSION_COMPONENT_PRESENTATION_FULL_PATHS;
import static com.sdl.web.preview.util.PreviewSessionClaims.SESSION_PAGE_FULL_PATH;
import static com.sdl.web.util.ContentServiceQueryConstants.QUERY_PARAM_ITEM_SKIP_EXTENSION_CHECK;
import static com.sdl.web.util.ContentServiceQueryConstants.QUERY_PARAM_PAGE_ID;
import static com.sdl.web.util.ContentServiceQueryConstants.QUERY_PARAM_PUBLICATION_ID;
import static com.sdl.web.util.ContentServiceQueryConstants.QUERY_PARAM_RENDER_CONTENT;

/**
 * Incorporated this class from the udp library in order to update it with the Jakarta Servlet API.
 * Client implementation of the {@link PageContentFilter}.
 */
public class ClientPageContentFilter extends PageContentFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ClientPageContentFilter.class);

    private String machineName;

    private PreviewFileLocationResolver locationResolver;
    private DynamicMetaRetriever metaRetriever;

    private CommonSessionManager sessionManager = ClientSessionManagerHolder.getCommonSessionManager();
    private SessionContentHandler sessionContentHandler = ClientSessionContentHandler.getInstance();

    @Override
    protected void doInit() throws SessionHandlingException {
        machineName = MachineNameProvider.getMachineName();
        try {
            locationResolver = PreviewFileLocationResolver.getInstance();
        }
        catch (ConfigurationException e) {
            throw new SessionHandlingException("Could not initialize filter.", e);
        }
    }

    @Override
    protected PreviewSession obtainPreviewSession() {
        if (!sessionContentHandler.checkIfSessionWrappersAreAvailable()) {
            return null;
        }
        return sessionManager.getCurrentSession();
    }

    @Override
    protected void handleComponentPresentations(PreviewSession session, List<ComponentPresentationMeta> cpMetas)
            throws StorageException {
        List<String> componentPresentations = new ArrayList<>();
        for (ComponentPresentationMeta cpMeta : cpMetas) {
            LOG.debug("Found ComponentPresentationMeta: pubId: {} compId: {} cpType: {}", cpMeta.getPublicationId(),
                    cpMeta.getComponentId(), cpMeta.getContentType());
            ComponentPresentation componentPresentation = new WebComponentPresentationFactoryImpl(
                    cpMeta.getPublicationId()).getComponentPresentation(cpMeta.getPublicationId(),
                    cpMeta.getComponentId(), cpMeta.getTemplateId());
            if (componentPresentation == null) {
                continue;
            }
            LOG.debug("Found component presentation with content length: {} for meta: {}.",
                    cpMeta, componentPresentation.getContent().length());
            String cpRoot = getClaimValue(REQUEST_COMPONENT_PRESENTATION_ROOT_PATH, null, String.class);
            String location = locationResolver.getFileLocation(componentPresentation,
                    ComponentPresentationTypeEnum.getComponentPresentationType(cpMeta.getContentType()), cpRoot);
            String sessionLocation = buildOutputPath(location, session.getPreviewSessionId());
            LOG.debug("Storing CP on session location: {}", sessionLocation);

            try {
                sessionContentHandler.updateComponentPresentationIfRequired(
                        componentPresentation.getContent().getBytes(),
                        sessionManager.getSessionItem(session, buildObjectKey(componentPresentation), machineName),
                        sessionLocation,
                        LOADED.getIntValue());

                componentPresentations.add(sessionLocation);
            }
            catch (SessionHandlingException e) {
                LOG.error("Unable to store item state", e);
            }
        }

        if (!componentPresentations.isEmpty()) {
            putToClaimStoreIfAvailable(SESSION_COMPONENT_PRESENTATION_FULL_PATHS,
                    componentPresentations.stream().collect(Collectors.joining(SEPARATOR)));
        }
    }

    @Override
    protected ComponentPresentationMeta getComponentPresentationMeta(Integer publicationId, Integer componentId,
                                                                     Integer templateId) throws StorageException {
        WebComponentPresentationMetaFactory componentPresentationMetaFactory =
                new WebComponentPresentationMetaFactoryImpl(publicationId);
        ComponentPresentationMeta meta = componentPresentationMetaFactory.getMeta(componentId, templateId);
        LOG.debug("Found component presentation metadata: {} for pub: {}, component: {}, template: {}.",
                meta, publicationId, componentId, templateId);
        return meta;
    }

    @Override
    protected PageMeta getPageMetaByURL(String url) {
        if (metaRetriever == null) {
            metaRetriever = new DynamicMetaRetrieverImpl();
        }
        return metaRetriever.getPageMetaByURL(url);
    }

    @Override
    protected CharacterData getCharacterDataForPage(PageMeta meta) throws StorageException {
        LOG.debug("Getting page contents from content-service");

        ODataClientQuery query = new ODataV2ClientQuery.Builder()
                .withEntityType(com.sdl.web.content.client.odata.v2.edm.PageContent.class)
                .withEntityParameterMap(QUERY_PARAM_PAGE_ID, "" + meta.getId())
                .withEntityParameterMap(QUERY_PARAM_PUBLICATION_ID, "" + meta.getPublicationId())
                .withEntityParameterMap(QUERY_PARAM_RENDER_CONTENT, "false")
                .withEntityParameterMap(QUERY_PARAM_ITEM_SKIP_EXTENSION_CHECK, "true")
                .build();
        com.sdl.web.content.client.odata.v2.edm.PageContent pageContent =
                (com.sdl.web.content.client.odata.v2.edm.PageContent) getContentClient()
                        .getEntity(com.sdl.web.content.client.odata.v2.edm.PageContent.class.getName(), query);

        if (pageContent != null) {
            return new CharacterDataString(
                    pageContent.getNamespaceId(),
                    pageContent.getPublicationId(),
                    pageContent.getPageId(),
                    pageContent.getContent(),
                    pageContent.getCharSet());
        }
        LOG.debug("Could not create characterData, pageContent was null");
        return null;
    }

    @Override
    protected void handleCharacterData(PreviewSession session, String outputFilePath, CharacterData cData)
            throws SessionHandlingException {

        PageContent content = new PageContent();
        content.setPublicationId(cData.getPublicationId());
        content.setPageId(cData.getId());
        content.setCharSet(cData.getCharsetName());

        try {
            content.setContent(cData.getString());
        }
        catch (IOException e) {
            LOG.error("The operation could not be performed.", e);
        }
        String pageContentKey = buildObjectKey(content);

        PreviewSessionItem itemCData = sessionManager.getSessionItem(session, buildObjectKey(cData), machineName);
        PreviewSessionItem itemPageContent = sessionManager.getSessionItem(session, pageContentKey, machineName);

        sessionContentHandler.updateCharacterDataIfRequired(outputFilePath, cData, itemCData, itemPageContent);
        putToClaimStoreIfAvailable(SESSION_PAGE_FULL_PATH, outputFilePath);
    }

    protected ContentClient getContentClient() {
        return ContentClientProvider.getInstance().getContentClient();
    }
}
