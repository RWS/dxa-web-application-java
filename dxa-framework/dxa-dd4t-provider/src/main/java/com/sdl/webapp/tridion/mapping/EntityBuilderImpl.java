package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.EclItem;
import com.sdl.webapp.common.api.model.entity.ExceptionEntity;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.controller.exception.NotFoundException;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.XpmUtils;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl.ComponentEntity;
import com.sdl.webapp.tridion.fields.FieldConverterRegistry;
import com.sdl.webapp.util.dd4t.FieldUtils;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Multimedia;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sdl.webapp.util.dd4t.MvcDataHelper.createMvcData;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@Service
public final class EntityBuilderImpl implements EntityBuilder {

    @Autowired
    private ViewModelRegistry viewModelRegistry;

    @Autowired
    private SemanticMapper semanticMapper;

    @Autowired
    private FieldConverterRegistry fieldConverterRegistry;

    @Autowired
    private SemanticMappingRegistry semanticMappingRegistry;

    @Autowired
    private ModelBuilderPipeline builder;

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityModel createEntity(ComponentPresentation componentPresentation, EntityModel originalEntityModel,
                                    Localization localization) throws ContentProviderException {
        final Component component = componentPresentation.getComponent();
        final String componentId = component.getId();
        log.debug("Creating entity for component: {}", componentId);

        final Map<String, Field> templateMeta = componentPresentation.getComponentTemplate().getMetadata();
        if (templateMeta == null) {
            log.warn("ComponentPresentation without template metadata, skipping: {}", componentId);
            return null;
        }

        final String viewName = FieldUtils.getStringValue(templateMeta, "view");
        if (isEmpty(viewName)) {
            log.warn("ComponentPresentation without a view, skipping: {}", componentId);
            return null;
        }

        Class<? extends AbstractEntityModel> entityClass = getEntityClass(viewName, component.getSchema().getRootElement());

        final EntityModel entity;
        if (entityClass == null) {
            entity = new ExceptionEntity(new NotFoundException("View/Entity class is not found"));
        } else {
            entity = createEntity(component, localization, entityClass, getSemanticSchema(component, localization));
        }
        fillEntityData(entity, componentPresentation);

        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityModel createEntity(final Component component, EntityModel originalEntityModel, final Localization localization)
            throws ContentProviderException {

        final SemanticSchema semanticSchema = getSemanticSchema(component, localization);

        final Class<? extends AbstractEntityModel> entityClass;

        entityClass = (Class<? extends AbstractEntityModel>) viewModelRegistry.getMappedModelTypes(semanticSchema.getFullyQualifiedNames());
        if (entityClass == null) {
            throw new ContentProviderException("Cannot create entity for component " + component);
        }

        return createEntity(component, localization, entityClass, semanticSchema);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends EntityModel> T createEntity(Component component, T originalEntityModel, Localization localization, Class<T> entityClass)
            throws ContentProviderException {
        return createEntity(component, localization, entityClass, getSemanticSchema(component, localization));
    }

    private Class<? extends AbstractEntityModel> getEntityClass(String viewName, String schemaRoot)
            throws ContentProviderException {
        Class<? extends AbstractEntityModel> entityClass;

        try {
            entityClass = (Class<? extends AbstractEntityModel>) viewModelRegistry.getViewEntityClass(viewName);
        } catch (DxaException e) {
            log.warn("Exception on getting view entity class", e);
            entityClass = (Class<? extends AbstractEntityModel>) semanticMappingRegistry.getEntityClass(schemaRoot);
        }

        if (entityClass == null) {
            log.error("Cannot determine entity type for view name: '{}'. Please make sure " +
                    "that an entry is registered for this view name in the ViewModelRegistry.", viewName);
        }

        return entityClass;
    }

    private <T extends EntityModel> T createEntity(Component component, Localization localization, Class<T> entityClass, SemanticSchema semanticSchema) throws ContentProviderException {
        final T entity;

        log.debug("Creating entity for component: {}", component.getId());
        try {
            entity = semanticMapper.createEntity(entityClass, semanticSchema.getSemanticFields(),
                    new SemanticFieldDataProviderImpl(new ComponentEntity(component), fieldConverterRegistry, this.builder));
        } catch (SemanticMappingException e) {
            throw new ContentProviderException(e);
        }

        if (AbstractEntityModel.class.isAssignableFrom(entity.getClass())) {
            ((AbstractEntityModel) entity).setId(component.getId().split("-")[1]);
        }

        processMediaItems(component, localization, entity);

        return entity;
    }

    private static SemanticSchema getSemanticSchema(Component component, Localization localization) {
        return localization.getSemanticSchemas().get(Long.parseLong(component.getSchema().getId().split("-")[1]));
    }

    private static void fillEntityData(EntityModel entity, ComponentPresentation componentPresentation) {
        if (entity instanceof AbstractEntityModel) {
            AbstractEntityModel abstractEntityModel = (AbstractEntityModel) entity;

            abstractEntityModel.setXpmMetadata(createXpmMetadata(entity, componentPresentation));
        }

        entity.setMvcData(createMvcData(componentPresentation));
        entity.setHtmlClasses(getHtmlClasses(componentPresentation));
    }

    private <T extends EntityModel> void processMediaItems(Component component, Localization localization, T entity) {
        if (entity instanceof MediaItem && component.getMultimedia() != null && !isEmpty(component.getMultimedia().getUrl())) {
            final Multimedia multimedia = component.getMultimedia();
            final MediaItem mediaItem = (MediaItem) entity;
            mediaItem.setUrl(multimedia.getUrl());
            mediaItem.setFileName(multimedia.getFileName());
            mediaItem.setFileSize(multimedia.getSize());
            mediaItem.setMimeType(multimedia.getMimeType());

            // ECL item is handled as as media item even if it maybe is not so in all cases (such as product items)
            processEclItems(component, localization, entity);
        }
    }

    private static Map<String, Object> createXpmMetadata(EntityModel entity, ComponentPresentation componentPresentation) {
        final Component component = componentPresentation.getComponent();
        final ComponentTemplate componentTemplate = componentPresentation.getComponentTemplate();

        return new XpmUtils.EntityXpmBuilder()
                .setComponentId(entity instanceof EclItem ? ((EclItem) entity).getUri() : component.getId())
                .setComponentModified(component.getRevisionDate())
                .setComponentTemplateID(componentTemplate.getId())
                .setComponentTemplateModified(componentTemplate.getRevisionDate())
                .setRepositoryPublished(componentPresentation.isDynamic())
                .buildXpm();
    }

    private static String getHtmlClasses(ComponentPresentation componentPresentation) {
        String htmlClasses = FieldUtils.getStringValue(componentPresentation.getComponentTemplate().getMetadata(), "htmlClasses");
        return !isEmpty(htmlClasses) ? htmlClasses.replaceAll("[^\\w\\- ]", "") : null;
    }

    private <T extends EntityModel> void processEclItems(Component component, Localization localization, T entity) {
        if (entity instanceof EclItem) {
            final EclItem eclItem = (EclItem) entity;
            //todo check if it's right; .NET does just eclItem.setUri(component.getEclId())
            eclItem.setUri(component.getTitle().replace("ecl:0", "ecl:" + localization.getId()));

            Map<String, FieldSet> extensionData = component.getExtensionData();
            if (extensionData != null) {
                fillItemWithEclData(eclItem, extensionData);

                fillItemWithExternalMetadata(eclItem, extensionData);
            }
        }
    }

    private static void fillItemWithEclData(EclItem eclItem, Map<String, FieldSet> extensionData) {
        FieldSet eclFieldSet = extensionData.get("ECL");
        eclItem.setDisplayTypeId(getValueFromFieldSet(eclFieldSet, "DisplayTypeId"));
        eclItem.setTemplateFragment(getValueFromFieldSet(eclFieldSet, "TemplateFragment"));
        String fileName = getValueFromFieldSet(eclFieldSet, "FileName");
        if (!isEmpty(fileName)) {
            eclItem.setFileName(fileName);
        }
        String mimeType = getValueFromFieldSet(eclFieldSet, "MimeType");
        if (!isEmpty(mimeType)) {
            eclItem.setMimeType(mimeType);
        }
    }

    private static void fillItemWithExternalMetadata(EclItem eclItem, Map<String, FieldSet> extensionData) {
        FieldSet externalEclFieldSet = extensionData.get("ECL-ExternalMetadata");
        eclItem.setExternalMetadata(normalizeExtensionMetadata(externalEclFieldSet.getContent()));
    }

    private static String getValueFromFieldSet(FieldSet eclFieldSet, String fieldName) {
        if (eclFieldSet != null) {
            Map<String, Field> fieldSetContent = eclFieldSet.getContent();
            if (fieldSetContent != null) {
                Field field = fieldSetContent.get(fieldName);
                if (field != null) {
                    return Objects.toString(field.getValues().get(0));
                }
            }
        }
        return null;
    }

    private static Map<String, Object> normalizeExtensionMetadata(Map<String, Field> extensionData) {
        return extensionData.entrySet().stream()
                .filter(entry -> !entry.getValue().getValues().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, o -> {
                    Field field = o.getValue();
                    Object value = field.getValues().get(0);
                    if (field instanceof EmbeddedField) {
                        return normalizeExtensionMetadata(((FieldSet) value).getContent());
                    }
                    return Objects.toString(value);
                }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
