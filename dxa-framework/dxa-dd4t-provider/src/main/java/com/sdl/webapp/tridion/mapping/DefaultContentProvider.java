package com.sdl.webapp.tridion.mapping;

import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.web.api.dynamic.DynamicMetaRetriever;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.query.ComponentMetadata;
import com.sdl.webapp.common.api.model.query.ComponentMetadata.MetaEntry;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import com.sdl.webapp.common.util.LocalizationUtils.TryFindPage;
import com.sdl.webapp.common.util.TcmUtils;
import lombok.extern.slf4j.Slf4j;
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
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Collections.singletonList;


/**
 * Implementation of {@link AbstractDefaultContentProvider} that uses DD4T to provide content.
 */
@Slf4j
@Service
public class DefaultContentProvider extends AbstractDefaultContentProvider {

    private static final Object LOCK = new Object();

    private final PageFactory dd4tPageFactory;

    private final ComponentPresentationFactory dd4tComponentPresentationFactory;

    private final ModelBuilderPipeline modelBuilderPipeline;

    @Autowired
    public DefaultContentProvider(WebRequestContext webRequestContext,
                                  LinkResolver linkResolver,
                                  WebApplicationContext webApplicationContext,
                                  DynamicMetaRetriever dynamicMetaRetriever,
                                  BinaryContentRetriever binaryContentRetriever,
                                  PageFactory dd4tPageFactory,
                                  ComponentPresentationFactory dd4tComponentPresentationFactory,
                                  ModelBuilderPipeline modelBuilderPipeline) {
        super(webRequestContext, linkResolver, webApplicationContext, dynamicMetaRetriever, binaryContentRetriever);
        this.dd4tPageFactory = dd4tPageFactory;
        this.dd4tComponentPresentationFactory = dd4tComponentPresentationFactory;
        this.modelBuilderPipeline = modelBuilderPipeline;
    }

    @Override
    protected TryFindPage<PageModel> _loadPageCallback(Localization localization) {
        return (path, publicationId) -> {
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

            return modelBuilderPipeline.createPageModel(genericPage, localization, DefaultContentProvider.this);
        };
    }

    @Override
    protected EntityModel _getEntityModel(String tcmUri, Localization localization) throws ContentProviderException, DxaException {
        String[] idParts = tcmUri.split("-");
        if (idParts.length != 2) {
            throw new IllegalArgumentException(String.format("Invalid Entity Identifier '%s'. Must be in format ComponentID-TemplateID.", tcmUri));
        }

        String componentUri = TcmUtils.buildTcmUri(localization.getId(), idParts[0]);
        String templateUri = TcmUtils.buildTemplateTcmUri(localization.getId(), idParts[1]);

        try {
            final ComponentPresentation componentPresentation;
            synchronized (LOCK) {
                componentPresentation = this.dd4tComponentPresentationFactory.getComponentPresentation(componentUri, templateUri);
            }
            return modelBuilderPipeline.createEntityModel(componentPresentation, localization);
        } catch (FactoryException e) {
            throw new DxaItemNotFoundException(tcmUri, e);
        } catch (ContentProviderException e) {
            throw new DxaException("Problem building entity model", e);
        }
    }

    @NotNull
    @Override
    protected <T extends EntityModel> List<T> _requestEntities(Class<T> entityClass, Localization localization, SimpleBrokerQuery query) throws ContentProviderException {
        List<T> result = new ArrayList<>();

        List<ComponentMetadata> components = executeQuery(query);
        for (ComponentMetadata metadata : components) {
            @NotNull Component component = constructComponentFromMetadata(metadata);
            result.add(modelBuilderPipeline.createEntityModel(component, localization, entityClass));
        }
        return result;
    }

    @NotNull
    private static Component constructComponentFromMetadata(ComponentMetadata metadata) {
        ComponentImpl component = new ComponentImpl();
        component.setId(TcmUtils.buildTcmUri(metadata.getPublicationId(), metadata.getId()));
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

    @NotNull
    private static PublicationImpl getPublicationFromMetadata(ComponentMetadata metadata) {
        PublicationImpl publication = new PublicationImpl();
        publication.setId(metadata.getPublicationId());
        return publication;
    }

    private static Schema getSchemaFromMetadata(ComponentMetadata metadata) {
        SchemaImpl schema = new SchemaImpl();
        schema.setId(TcmUtils.buildTcmUri(metadata.getPublicationId(), metadata.getSchemaId()));
        PublicationImpl publication = getPublicationFromMetadata(metadata);
        schema.setPublication(publication);
        return schema;
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
}
