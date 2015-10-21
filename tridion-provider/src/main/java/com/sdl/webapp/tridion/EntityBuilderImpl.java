package com.sdl.webapp.tridion;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.SemanticMapper;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.config.EntitySemantics;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.MvcDataImpl;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.EclItem;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.tridion.fieldconverters.FieldConverterRegistry;

import com.sdl.webapp.tridion.fieldconverters.FieldUtils;
import org.dd4t.contentmodel.*;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@org.springframework.stereotype.Component
final class EntityBuilderImpl implements EntityBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(EntityBuilderImpl.class);

    private static final String DEFAULT_AREA_NAME = "Core";
    private static final String DEFAULT_CONTROLLER_NAME = "Entity";
    private static final String DEFAULT_ACTION_NAME = "Entity";
    private static final String DEFAULT_REGION_NAME = "Main";

    private final ViewModelRegistry viewModelRegistry;

    private final SemanticMapper semanticMapper;

    private final FieldConverterRegistry fieldConverterRegistry;

    private final SemanticMappingRegistry semanticMappingRegistry;

    private final ModelBuilderPipeline builder;

    @Autowired
    EntityBuilderImpl(ViewModelRegistry viewModelRegistry, SemanticMapper semanticMapper,
                      FieldConverterRegistry fieldConverterRegistry, SemanticMappingRegistry semanticMappingRegistry, ModelBuilderPipeline builder) {
        this.viewModelRegistry = viewModelRegistry;
        this.semanticMapper = semanticMapper;
        this.fieldConverterRegistry = fieldConverterRegistry;
        this.semanticMappingRegistry = semanticMappingRegistry;
        this.builder = builder;
    }

    @Override
    public EntityModel createEntity(ComponentPresentation componentPresentation, EntityModel originalEntityModel, Localization localization)
            throws ContentProviderException {
        final Component component = componentPresentation.getComponent();
        final String componentId = component.getId();
        LOG.debug("Creating entity for component: {}", componentId);

        final Map<String, Field> templateMeta = componentPresentation.getComponentTemplate().getMetadata();
        if (templateMeta == null) {
            LOG.warn("ComponentPresentation without template metadata, skipping: {}", componentId);
            return null;
        }

        final String viewName = FieldUtils.getStringValue(templateMeta, "view");
        if (Strings.isNullOrEmpty(viewName)) {
            LOG.warn("ComponentPresentation without a view, skipping: {}", componentId);
            return null;
        }

        Class<? extends EntityModel> entityClass;
        try {
            entityClass = (Class<? extends EntityModel>) viewModelRegistry.getViewEntityClass(viewName);
            if (entityClass == null) {
                throw new ContentProviderException("Cannot determine entity type for view name: '" + viewName +
                        "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.");
            }
        } catch (DxaException e) {

            // Get entity class through semantic mapping registry instead using implicit mapping
            //
            entityClass = this.semanticMappingRegistry.getEntityClass(component.getSchema().getRootElement());
            if (entityClass == null) {
                throw new ContentProviderException("Cannot determine entity type for view name: '" + viewName +
                        "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.", e);
            }
        }


        final SemanticSchema semanticSchema = localization.getSemanticSchemas()
                .get(Long.parseLong(component.getSchema().getId().split("-")[1]));

        final AbstractEntityModel entity;
        try {
            entity = semanticMapper.createEntity((Class<? extends AbstractEntityModel>) entityClass, semanticSchema.getSemanticFields(),
                    new DD4TSemanticFieldDataProvider(component, fieldConverterRegistry, this.builder));
        } catch (SemanticMappingException e) {
            throw new ContentProviderException(e);
        }

        entity.setId(componentId.split("-")[1]);

        // Special handling for media items
        if (entity instanceof MediaItem && component.getMultimedia() != null &&
                !Strings.isNullOrEmpty(component.getMultimedia().getUrl())) {
            final Multimedia multimedia = component.getMultimedia();
            final MediaItem mediaItem = (MediaItem) entity;
            mediaItem.setUrl(multimedia.getUrl());
            mediaItem.setFileName(multimedia.getFileName());
            mediaItem.setFileSize(multimedia.getSize());
            mediaItem.setMimeType(multimedia.getMimeType());

            // ECL item is handled as as media item even if it maybe is not so in all cases (such as product items)
            //
            if (entity instanceof EclItem) {
                fillEclItem(component, localization, (EclItem) entity);
            }
        }

        createEntityData(entity, componentPresentation);
        entity.setMvcData(createMvcData(componentPresentation));

        String htmlClasses = FieldUtils.getStringValue(componentPresentation.getComponentTemplate().getMetadata(), "htmlClasses");
        if (!Strings.isNullOrEmpty(htmlClasses)) {
            entity.setHtmlClasses(htmlClasses.replaceAll("[^\\w\\-\\ ]", ""));
        }

        return entity;
    }

    @Override
    public EntityModel createEntity(Component component, EntityModel originalEntityModel, Localization localization, Class<AbstractEntityModel> entityClass)
            throws ContentProviderException {
        final SemanticSchema semanticSchema = localization.getSemanticSchemas().get(Long.parseLong(component.getSchema().getId().split("-")[1]));
        return createEntity(component, localization, entityClass, semanticSchema);
    }

    @Override
    public EntityModel createEntity(Component component, EntityModel originalEntityModel, Localization localization)
            throws ContentProviderException {

        final SemanticSchema semanticSchema = localization.getSemanticSchemas().get(Long.parseLong(component.getSchema().getId().split("-")[1]));
        String semanticTypeName = semanticSchema.getRootElement();
        //Try to find the fully qualified name:
        for (EntitySemantics es : semanticSchema.getEntitySemantics()) {
            if (es.getEntityName().equals(semanticTypeName)) {
                //TODO: TW, the vocabulary.getVocab() is null, using id
                semanticTypeName = String.format("%s:%s", es.getVocabulary().getId(), semanticTypeName);
                break;
            }
        }

        final Class<? extends AbstractEntityModel> entityClass;
        try {
            entityClass = (Class<? extends AbstractEntityModel>) viewModelRegistry.getMappedModelTypes(semanticTypeName);
            if (entityClass == null) {
                throw new ContentProviderException("Cannot determine entity type for view name: '" + semanticTypeName +
                        "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.");
            }
        } catch (DxaException e) {
            throw new ContentProviderException("Cannot determine entity type for view name: '" + semanticTypeName +
                    "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.", e);
        }

        return createEntity(component, localization, entityClass, semanticSchema);
    }

    private EntityModel createEntity(Component component, Localization localization, Class<? extends AbstractEntityModel> entityClass, SemanticSchema semanticSchema)
            throws ContentProviderException {

        final String componentId = component.getId();
        LOG.debug("Creating entity for component: {}", componentId);
        final AbstractEntityModel entity;
        try {
            entity = semanticMapper.createEntity(entityClass, semanticSchema.getSemanticFields(),
                    new DD4TSemanticFieldDataProvider(component, fieldConverterRegistry, this.builder));
        } catch (SemanticMappingException e) {
            throw new ContentProviderException(e);
        }

        entity.setId(componentId.split("-")[1]);

        // Special handling for media items
        //
        if (entity instanceof MediaItem && component.getMultimedia() != null &&
                !Strings.isNullOrEmpty(component.getMultimedia().getUrl())) {
            final Multimedia multimedia = component.getMultimedia();
            final MediaItem mediaItem = (MediaItem) entity;
            mediaItem.setUrl(multimedia.getUrl());
            mediaItem.setFileName(multimedia.getFileName());
            mediaItem.setFileSize(multimedia.getSize());
            mediaItem.setMimeType(multimedia.getMimeType());

        }

        // ECL item is handled as as media item even if it maybe is not so in all cases (such as product items)
        if (entity instanceof EclItem) {
            fillEclItem(component, localization, (EclItem) entity);
        }

        return entity;
    }

    private void fillEclItem(Component component, Localization localization, EclItem entity) {
        final EclItem eclItem = entity;
        //todo check if it's right; .NET does just eclItem.setUri(component.getEclId())
        eclItem.setUri(component.getTitle().replace("ecl:0", "ecl:" + localization.getId()));

        Map<String, FieldSet> extensionData = component.getExtensionData();
        if (extensionData != null) {
            fillItemWithEclData(eclItem, extensionData);

            fillItemWithExternalMetadata(eclItem, extensionData);
        }
    }

    private void fillItemWithExternalMetadata(EclItem eclItem, Map<String, FieldSet> extensionData) {
        FieldSet externalEclFieldSet = extensionData.get("ECL-ExternalMetadata");
        Map<String, Object> externalMetadata = new HashMap<>(externalEclFieldSet.getContent().size());
        for (Map.Entry<String, Field> entry : externalEclFieldSet.getContent().entrySet()) {
            externalMetadata.put(entry.getKey(), entry.getValue().getValues().get(0));
        }
        eclItem.setExternalMetadata(externalMetadata);
    }

    private void fillItemWithEclData(EclItem eclItem, Map<String, FieldSet> extensionData) {
        FieldSet eclFieldSet = extensionData.get("ECL");
        eclItem.setDisplayTypeId(getValueFromFieldSet(eclFieldSet, "DisplayTypeId"));
        eclItem.setTemplateFragment(getValueFromFieldSet(eclFieldSet, "TemplateFragment"));
        String fileName = getValueFromFieldSet(eclFieldSet, "FileName");
        if (!StringUtils.isEmpty(fileName)) {
            eclItem.setFileName(fileName);
        }
        String mimeType = getValueFromFieldSet(eclFieldSet, "MimeType");
        if (!StringUtils.isEmpty(mimeType)) {
            eclItem.setMimeType(mimeType);
        }
    }

    private String getValueFromFieldSet(FieldSet eclFieldSet, String fieldName) {
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

    private void createEntityData(AbstractEntityModel entity, ComponentPresentation componentPresentation) {
        final Component component = componentPresentation.getComponent();
        final ComponentTemplate componentTemplate = componentPresentation.getComponentTemplate();

        ImmutableMap.Builder<String, String> xpmMetaDataBuilder = ImmutableMap.builder();

        if (entity instanceof EclItem) {
            xpmMetaDataBuilder.put("ComponentID", ((EclItem) entity).getUri());
        } else {
            xpmMetaDataBuilder.put("ComponentID", component.getId());
        }
        xpmMetaDataBuilder.put("ComponentModified",
                ISODateTimeFormat.dateHourMinuteSecond().print(component.getRevisionDate()));
        xpmMetaDataBuilder.put("ComponentTemplateID", componentTemplate.getId());
        xpmMetaDataBuilder.put("ComponentTemplateModified",
                ISODateTimeFormat.dateHourMinuteSecond().print(componentTemplate.getRevisionDate()));

        entity.setXpmMetadata(xpmMetaDataBuilder.build());

    }

    private MvcData createMvcData(ComponentPresentation componentPresentation) {

        final MvcDataImpl mvcData = new MvcDataImpl();

        final ComponentTemplate componentTemplate = componentPresentation.getComponentTemplate();
        final Map<String, Field> templateMeta = componentTemplate.getMetadata();

        final String[] controllerNameParts = getControllerNameParts(templateMeta);
        mvcData.setControllerAreaName(controllerNameParts[0]);
        mvcData.setControllerName(controllerNameParts[1]);
        mvcData.setActionName(getActionName(templateMeta));

        final String[] viewNameParts = getViewNameParts(componentTemplate);
        mvcData.setAreaName(viewNameParts[0]);
        mvcData.setViewName(viewNameParts[1]);

        final String[] regionNameParts = getRegionNameParts(templateMeta);
        mvcData.setRegionAreaName(regionNameParts[0]);
        mvcData.setRegionName(regionNameParts[1]);

        final Map<String, String> routeValues = new HashMap<>();
        for (String value : Strings.nullToEmpty(FieldUtils.getStringValue(templateMeta, "routeValues")).split(",")) {
            final String[] parts = value.split(":");
            if (parts.length > 1 && !routeValues.containsKey(parts[0])) {
                routeValues.put(parts[0], parts[1]);
            }
        }
        mvcData.setRouteValues(routeValues);
        mvcData.setMetadata(this.getMvcMetadata(componentPresentation.getComponentTemplate()));

        return mvcData;
    }

    private String[] getControllerNameParts(Map<String, Field> templateMeta) {
        String fullName = FieldUtils.getStringValue(templateMeta, "controller");
        if (Strings.isNullOrEmpty(fullName)) {
            fullName = DEFAULT_CONTROLLER_NAME;
        }
        return splitName(fullName);
    }

    private String getActionName(Map<String, Field> templateMeta) {
        String actionName = FieldUtils.getStringValue(templateMeta, "action");
        if (Strings.isNullOrEmpty(actionName)) {
            actionName = DEFAULT_ACTION_NAME;
        }
        return actionName;
    }

    private String[] getViewNameParts(ComponentTemplate componentTemplate) {
        String fullName = FieldUtils.getStringValue(componentTemplate.getMetadata(), "view");
        if (Strings.isNullOrEmpty(fullName)) {
            fullName = componentTemplate.getTitle().replaceAll("\\[.*\\]|\\s", "");
        }
        return splitName(fullName);
    }

    private String[] getRegionNameParts(Map<String, Field> templateMeta) {
        String fullName = FieldUtils.getStringValue(templateMeta, "regionView");
        if (Strings.isNullOrEmpty(fullName)) {
            fullName = DEFAULT_REGION_NAME;
        }
        return splitName(fullName);
    }

    private String[] splitName(String name) {
        final String[] parts = name.split(":");
        return parts.length > 1 ? parts : new String[]{DEFAULT_AREA_NAME, name};
    }

    private Map<String, Object> getMvcMetadata(ComponentTemplate componentTemplate) {

        // TODO: Move this code into a generic MvcDataHelper class

        Map<String, Object> metadata = new HashMap<>();
        Map<String, Field> metadataFields = componentTemplate.getMetadata();

        for (Map.Entry<String, Field> entry : metadataFields.entrySet()) {
            String fieldName = entry.getKey();
            if (fieldName.equals("view") ||
                    fieldName.equals("regionView") ||
                    fieldName.equals("controller") ||
                    fieldName.equals("action") ||
                    fieldName.equals("routeValues")) {
                continue;
            }
            Field field = entry.getValue();
            if (field.getValues().size() > 0) {
                metadata.put(fieldName, field.getValues().get(0).toString()); // Assume single-value text fields for template metadata
            }
        }
        return metadata;
    }
}
