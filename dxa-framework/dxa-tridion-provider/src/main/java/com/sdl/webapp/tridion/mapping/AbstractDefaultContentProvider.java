package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.entity.DynamicList;
import com.sdl.webapp.common.api.model.query.ComponentMetadata;
import com.sdl.webapp.common.api.model.query.ComponentMetadata.MetaEntry;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import com.sdl.webapp.common.util.ImageUtils;
import com.sdl.webapp.common.util.LocalizationUtils;
import com.sdl.webapp.common.util.LocalizationUtils.TryFindPage;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Schema;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.contentmodel.impl.ComponentImpl;
import org.dd4t.contentmodel.impl.DateField;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.FieldSetImpl;
import org.dd4t.contentmodel.impl.NumericField;
import org.dd4t.contentmodel.impl.PublicationImpl;
import org.dd4t.contentmodel.impl.SchemaImpl;
import org.dd4t.contentmodel.impl.TextField;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.factories.PageFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import static com.sdl.webapp.common.util.FileUtils.isFileOlderThan;
import static com.sdl.webapp.common.util.FileUtils.parentFolderExists;
import static com.sdl.webapp.common.util.LocalizationUtils.findPageByPath;
import static com.sdl.webapp.common.util.TcmUtils.buildTcmUri;
import static com.sdl.webapp.common.util.TcmUtils.buildTemplateTcmUri;
import static java.util.Collections.singletonList;


/**
 * Implementation of {@link ContentProvider} that uses DD4T to provide content.
 */
@Slf4j
public abstract class AbstractDefaultContentProvider implements ContentProvider {

    private static final Object LOCK = new Object();


    private static final String STATIC_FILES_DIR = "BinaryData";

    private static final Pattern SYSTEM_VERSION_PATTERN = Pattern.compile("/system/v\\d+\\.\\d+/");

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    @Autowired
    private PageFactory dd4tPageFactory;

    @Autowired
    private ComponentPresentationFactory dd4tComponentPresentationFactory;

    @Autowired
    private ModelBuilderPipeline modelBuilderPipeline;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private WebRequestContext webRequestContext;

    protected static boolean isToBeRefreshed(File file, long time) throws ContentProviderException {
        if (isFileOlderThan(file, time)) {
            if (!parentFolderExists(file, true)) {
                throw new ContentProviderException("Failed to create parent directory for file: " + file);
            }
            return true;
        }
        return false;
    }

    private static String removeVersionNumber(String path) {
        return SYSTEM_VERSION_PATTERN.matcher(path).replaceFirst("/system/");
    }

    @NotNull
    private static Component constructComponentFromMetadata(ComponentMetadata metadata) {
        ComponentImpl component = new ComponentImpl();
        component.setId(buildTcmUri(metadata.getPublicationId(), metadata.getId()));
        component.setLastPublishedDate(new DateTime(metadata.getLastPublicationDate()));
        component.setRevisionDate(new DateTime(metadata.getModificationDate()));
        component.setTitle(metadata.getTitle());

        component.setPublication(getPublicationFromMetadata(metadata));

        component.setSchema(getSchemaFromMetadata(metadata));

        Map<String, Field> metaFields = new HashMap<>();
        metaFields.put("standardMeta", getStandardMeta(metadata));
        component.setMetadata(metaFields);

        return component;
    }

    private static Schema getSchemaFromMetadata(ComponentMetadata metadata) {
        SchemaImpl schema = new SchemaImpl();
        schema.setId(buildTcmUri(metadata.getPublicationId(), metadata.getSchemaId()));
        PublicationImpl publication = getPublicationFromMetadata(metadata);
        schema.setPublication(publication);
        return schema;
    }

    @NotNull
    private static PublicationImpl getPublicationFromMetadata(ComponentMetadata metadata) {
        PublicationImpl publication = new PublicationImpl();
        publication.setId(metadata.getPublicationId());
        return publication;
    }

    private static EmbeddedField getStandardMeta(ComponentMetadata metadata) {
        Map<String, Field> standardMetaContents = new HashMap<>();

        String dateTimeStringFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";

        for (Entry<String, MetaEntry> customEntry : metadata.getCustom().entrySet()) {
            MetaEntry data = customEntry.getValue();

            if (data != null && data.getValue() != null) {
                Object value = data.getValue();

                BaseField field;
                switch (data.getMetaType()) {
                    case DATE:
                        field = new DateField();
                        DateTime dateTime = new DateTime(value);

                        field.setDateTimeValues(singletonList(dateTime.toString(dateTimeStringFormat)));
                        break;
                    case FLOAT:
                        field = new NumericField();
                        field.setNumericValues(singletonList(new Double(value.toString())));
                        break;
                    default:
                        field = new TextField();
                        field.setTextValues(singletonList(value.toString()));
                }

                field.setName(customEntry.getKey());
                standardMetaContents.put(customEntry.getKey(), field);
            }
        }

        // The semantic mapping requires that some metadata fields exist.
        // This may not be the case so we map some component meta properties onto them if they don't exist.
        if (!standardMetaContents.containsKey("dateCreated")) {
            DateField dateField = new DateField();
            dateField.setName("dateCreated");
            dateField.setDateTimeValues(singletonList(new DateTime(metadata.getLastPublicationDate()).toString(dateTimeStringFormat)));
            standardMetaContents.put("dateCreated", dateField);
        }

        if (!standardMetaContents.containsKey("name")) {
            TextField textField = new TextField();
            textField.setName("name");
            textField.setTextValues(singletonList(metadata.getTitle()));
            standardMetaContents.put("name", textField);
        }

        EmbeddedField embeddedField = new EmbeddedField();
        List<FieldSet> embeddedValues = new ArrayList<>();
        FieldSetImpl fieldSet = new FieldSetImpl();

        fieldSet.setContent(standardMetaContents);
        embeddedValues.add(fieldSet);
        embeddedField.setEmbeddedValues(embeddedValues);

        return embeddedField;
    }

    protected abstract StaticContentFile getStaticContentFile(File file, ImageUtils.StaticContentPathInfo pathInfo, int publicationId) throws ContentProviderException, IOException;

    @Override
    @SneakyThrows(UnsupportedEncodingException.class)
    public PageModel getPageModel(String path, final Localization localization) throws ContentProviderException {
        path = UriUtils.encodePath(path, "UTF-8");
        return findPageByPath(path, localization, new TryFindPage<PageModel>() {
            @Override
            public PageModel tryFindPage(String path, int publicationId) throws ContentProviderException {
                final org.dd4t.contentmodel.Page genericPage;
                try {
                    synchronized (LOCK) {
                        if (dd4tPageFactory.isPagePublished(path, publicationId)) {
                            genericPage = dd4tPageFactory.findPageByUrl(path, publicationId);
                        } else {
                            return null;
                        }
                    }
                } catch (ItemNotFoundException e) {
                    log.debug("Page not found: [{}] {}", publicationId, path, e);
                    return null;
                } catch (FactoryException e) {
                    throw new ContentProviderException("Exception while getting page model for: [" + publicationId +
                            "] " + path, e);
                }

                PageModel pageModel = modelBuilderPipeline.createPageModel(genericPage, localization, AbstractDefaultContentProvider.this);
                if (pageModel != null) {
                    pageModel.setUrl(LocalizationUtils.stripDefaultExtension(path));
                    webRequestContext.setPage(pageModel);
                }
                return pageModel;
            }
        });
    }

    @Override
    public EntityModel getEntityModel(@NonNull String tcmUri, final Localization localization) throws DxaException {

        String[] idParts = tcmUri.split("-");
        if (idParts.length != 2) {
            throw new IllegalArgumentException(String.format("Invalid Entity Identifier '%s'. Must be in format ComponentID-TemplateID.", tcmUri));
        }

        String componentUri = buildTcmUri(localization.getId(), idParts[0]);
        String templateUri = buildTemplateTcmUri(localization.getId(), idParts[1]);

        try {
            final ComponentPresentation componentPresentation;
            synchronized (LOCK) {
                componentPresentation = this.dd4tComponentPresentationFactory.getComponentPresentation(componentUri, templateUri);
            }
            EntityModel entityModel = modelBuilderPipeline.createEntityModel(componentPresentation, localization);
            if (entityModel.getXpmMetadata() != null) {
                entityModel.getXpmMetadata().put("IsQueryBased", true);
            }
            return entityModel;

        } catch (FactoryException e) {
            throw new DxaItemNotFoundException(tcmUri);
        } catch (ContentProviderException e) {
            throw new DxaException("Problem building entity model", e);
        }
    }

    @Override
    public <T extends EntityModel> void populateDynamicList(DynamicList<T, SimpleBrokerQuery> dynamicList, Localization localization) throws ContentProviderException {
        SimpleBrokerQuery query = dynamicList.getQuery(localization);

        List<T> result = new ArrayList<>();

        List<ComponentMetadata> components = executeQuery(query);
        for (ComponentMetadata metadata : components) {
            @NotNull Component component = constructComponentFromMetadata(metadata);
            result.add(modelBuilderPipeline.createEntityModel(component, localization, dynamicList.getEntityType()));
        }

        dynamicList.setQueryResults(result, query.isHasMore());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Implementation of {@code StaticContentProvider} that uses DD4T to provide static content.
     * <p>
     * TODO: Should use DD4T BinaryFactory instead of calling the Tridion broker API directly.
     * </p>
     */
    @Override
    public StaticContentItem getStaticContent(final String path, String localizationId, String localizationPath)
            throws ContentProviderException {
        if (log.isTraceEnabled()) {
            log.trace("getStaticContent: {} [{}] {}", path, localizationId, localizationPath);
        }

        final String contentPath;
        if (localizationPath.length() > 1) {
            contentPath = localizationPath + removeVersionNumber(path.startsWith(localizationPath) ?
                    path.substring(localizationPath.length()) : path);
        } else {
            contentPath = removeVersionNumber(path);
        }

        final StaticContentFile staticContentFile = getStaticContentFile(contentPath, localizationId);

        //noinspection ReturnOfInnerClass
        return new StaticContentItem() {
            @Override
            public long getLastModified() {
                return staticContentFile.getFile().lastModified();
            }

            @Override
            public String getContentType() {
                return staticContentFile.getContentType();
            }

            @Override
            public InputStream getContent() throws IOException {
                return new FileInputStream(staticContentFile.getFile());
            }

            @Override
            public boolean isVersioned() {
                return path.contains("/system/");
            }
        };
    }

    /**
     * Executes the given query on a specific version of Tridion and returns a list of metadata.
     *
     * @param query query to execute
     * @return a list of metadata, never returns <code>null</code>
     */
    @Contract("_ -> !null")
    protected abstract List<ComponentMetadata> executeQuery(SimpleBrokerQuery query);

    @SneakyThrows(UnsupportedEncodingException.class)
    protected String prependFullUrlIfNeeded(String path) {
        String baseUrl = webRequestContext.getBaseUrl();
        if (path.contains(baseUrl)) {
            return path;
        }
        return UriUtils.encodePath(baseUrl + path, "UTF-8").replace("&", "%26");
    }

    private StaticContentFile getStaticContentFile(String path, String localizationId)
            throws ContentProviderException {
        String parentPath = StringUtils.join(new String[]{
                webApplicationContext.getServletContext().getRealPath("/"), STATIC_FILES_DIR, localizationId
        }, File.separator);

        final File file = new File(parentPath, path);
        log.trace("getStaticContentFile: {}", file);

        final ImageUtils.StaticContentPathInfo pathInfo = new ImageUtils.StaticContentPathInfo(path);

        final int publicationId = Integer.parseInt(localizationId);
        try {
            return getStaticContentFile(file, pathInfo, publicationId);
        } catch (IOException e) {
            throw new ContentProviderException("Exception while getting static content for: [" + publicationId + "] "
                    + path, e);
        }
    }



    protected static final class StaticContentFile {

        private final File file;

        private final String contentType;

        protected StaticContentFile(File file, String contentType) {
            this.file = file;
            this.contentType = StringUtils.isEmpty(contentType) ? DEFAULT_CONTENT_TYPE : contentType;
        }

        public File getFile() {
            return file;
        }

        public String getContentType() {
            return contentType;
        }
    }
}
