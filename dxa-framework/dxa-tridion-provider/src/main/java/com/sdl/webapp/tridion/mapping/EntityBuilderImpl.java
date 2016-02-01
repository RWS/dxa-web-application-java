package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.EclItem;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.api.model.entity.ViewNotFoundEntityError;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl.ComponentEntity;
import com.sdl.webapp.tridion.fields.FieldConverterRegistry;
import com.sdl.webapp.util.dd4t.FieldUtils;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Multimedia;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@org.springframework.stereotype.Component
/**
 * <p>EntityBuilderImpl class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public final class EntityBuilderImpl implements EntityBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(EntityBuilderImpl.class);

    private static final String DEFAULT_AREA_NAME = "Core";
    private static final String DEFAULT_CONTROLLER_NAME = "Entity";
    private static final String DEFAULT_ACTION_NAME = "Entity";
    private static final String DEFAULT_REGION_NAME = "Main";

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

    private static SemanticSchema getSemanticSchema(Component component, Localization localization) {
        return localization.getSemanticSchemas().get(Long.parseLong(component.getSchema().getId().split("-")[1]));
    }

    private static void fillItemWithExternalMetadata(EclItem eclItem, Map<String, FieldSet> extensionData) {
        FieldSet externalEclFieldSet = extensionData.get("ECL-ExternalMetadata");
        Map<String, Object> externalMetadata = new HashMap<>(externalEclFieldSet.getContent().size());
        for (Map.Entry<String, Field> entry : externalEclFieldSet.getContent().entrySet()) {
            final List<Object> values = entry.getValue().getValues();

            if (!values.isEmpty()) {
                externalMetadata.put(entry.getKey(), values.get(0));
            }
        }
        eclItem.setExternalMetadata(externalMetadata);
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

    private static void createEntityData(AbstractEntityModel entity, ComponentPresentation componentPresentation) {
        final Component component = componentPresentation.getComponent();
        final ComponentTemplate componentTemplate = componentPresentation.getComponentTemplate();

        Map<String, String> xpmMetaData = new HashMap<>();

        if (entity instanceof EclItem) {
            xpmMetaData.put("ComponentID", ((EclItem) entity).getUri());
        } else {
            xpmMetaData.put("ComponentID", component.getId());
        }
        xpmMetaData.put("ComponentModified",
                ISODateTimeFormat.dateHourMinuteSecond().print(component.getRevisionDate()));
        xpmMetaData.put("ComponentTemplateID", componentTemplate.getId());
        xpmMetaData.put("ComponentTemplateModified",
                ISODateTimeFormat.dateHourMinuteSecond().print(componentTemplate.getRevisionDate()));

        xpmMetaData.put("IsRepositoryPublished", String.valueOf(componentPresentation.isDynamic()));

        entity.setXpmMetadata(xpmMetaData);

    }

    private static String[] getControllerNameParts(Map<String, Field> templateMeta) {
        String fullName = FieldUtils.getStringValue(templateMeta, "controller");
        if (isEmpty(fullName)) {
            fullName = DEFAULT_CONTROLLER_NAME;
        }
        return splitName(fullName);
    }

    private static String getActionName(Map<String, Field> templateMeta) {
        String actionName = FieldUtils.getStringValue(templateMeta, "action");
        if (isEmpty(actionName)) {
            actionName = DEFAULT_ACTION_NAME;
        }
        return actionName;
    }

    private static String[] getViewNameParts(ComponentTemplate componentTemplate) {
        String fullName = FieldUtils.getStringValue(componentTemplate.getMetadata(), "view");
        if (isEmpty(fullName)) {
            fullName = componentTemplate.getTitle().replaceAll("\\[.*\\]|\\s", "");
        }
        return splitName(fullName);
    }

    private static String[] getRegionNameParts(Map<String, Field> templateMeta) {
        String fullName = FieldUtils.getStringValue(templateMeta, "regionView");
        if (isEmpty(fullName)) {
            fullName = DEFAULT_REGION_NAME;
        }
        return splitName(fullName);
    }

    private static String[] splitName(String name) {
        final String[] parts = name.split(":");
        return parts.length > 1 ? parts : new String[]{DEFAULT_AREA_NAME, name};
    }

    private static Map<String, Object> getMvcMetadata(ComponentTemplate componentTemplate) {

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

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityModel createEntity(ComponentPresentation componentPresentation, EntityModel originalEntityModel,
                                    Localization localization) throws ContentProviderException {
        final Component component = componentPresentation.getComponent();
        final String componentId = component.getId();
        LOG.debug("Creating entity for component: {}", componentId);

        final Map<String, Field> templateMeta = componentPresentation.getComponentTemplate().getMetadata();
        if (templateMeta == null) {
            LOG.warn("ComponentPresentation without template metadata, skipping: {}", componentId);
            return null;
        }

        final String viewName = FieldUtils.getStringValue(templateMeta, "view");
        if (isEmpty(viewName)) {
            LOG.warn("ComponentPresentation without a view, skipping: {}", componentId);
            return null;
        }


        Class<? extends AbstractEntityModel> entityClass = getEntityClass(viewName, component.getSchema().getRootElement());

        final SemanticSchema semanticSchema = getSemanticSchema(component, localization);
        final AbstractEntityModel entity = (AbstractEntityModel) createEntity(component, localization, entityClass, semanticSchema);

        createEntityData(entity, componentPresentation);
        entity.setMvcData(createMvcData(componentPresentation));

        String htmlClasses = FieldUtils.getStringValue(componentPresentation.getComponentTemplate().getMetadata(), "htmlClasses");
        if (!isEmpty(htmlClasses)) {
            entity.setHtmlClasses(htmlClasses.replaceAll("[^\\w\\- ]", ""));
        }

        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityModel createEntity(final Component component, EntityModel originalEntityModel, final Localization localization)
            throws ContentProviderException {

        final SemanticSchema semanticSchema = getSemanticSchema(component, localization);

        final Class<? extends AbstractEntityModel> entityClass =
                (Class<? extends AbstractEntityModel>) viewModelRegistry.getMappedModelTypes(semanticSchema.getFullyQualifiedNames());

        return createEntity(component, localization, entityClass, semanticSchema);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityModel createEntity(Component component, EntityModel originalEntityModel, Localization localization, Class<AbstractEntityModel> entityClass)
            throws ContentProviderException {
        return createEntity(component, localization, entityClass, getSemanticSchema(component, localization));
    }

    private EntityModel createEntity(Component component,
                                     Localization localization,
                                     Class<? extends AbstractEntityModel> entityClass,
                                     SemanticSchema semanticSchema) throws ContentProviderException {
        final AbstractEntityModel entity;

        LOG.debug("Creating entity for component: {}", component.getId());
        try {
            entity = semanticMapper.createEntity(entityClass, semanticSchema.getSemanticFields(),
                    new SemanticFieldDataProviderImpl(new ComponentEntity(component), fieldConverterRegistry, this.builder));
        } catch (SemanticMappingException e) {
            throw new ContentProviderException(e);
        }

        entity.setId(component.getId().split("-")[1]);

        processMediaItems(component, localization, entity);

        return entity;
    }

    private Class<? extends AbstractEntityModel> getEntityClass(String viewName, String schemaRoot)
            throws ContentProviderException {
        Class<? extends AbstractEntityModel> entityClass;

        try {
            entityClass = (Class<? extends AbstractEntityModel>) viewModelRegistry.getViewEntityClass(viewName);
        } catch (DxaException e) {
            entityClass = (Class<? extends AbstractEntityModel>) semanticMappingRegistry.getEntityClass(schemaRoot);
        }

        if (entityClass == null) {
            LOG.error("Cannot determine entity type for view name: '" + viewName +
                    "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.");
            return ViewNotFoundEntityError.class;
        }

        return entityClass;
    }

    private void processMediaItems(Component component, Localization localization, AbstractEntityModel entity) {
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

    private void processEclItems(Component component, Localization localization, AbstractEntityModel entity) {
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

    private MvcData createMvcData(ComponentPresentation componentPresentation) {
        final ComponentTemplate componentTemplate = componentPresentation.getComponentTemplate();
        final Map<String, Field> templateMeta = componentTemplate.getMetadata();

        final String[] controllerNameParts = getControllerNameParts(templateMeta);
        final String[] viewNameParts = getViewNameParts(componentTemplate);
        final String[] regionNameParts = getRegionNameParts(templateMeta);

        final String actionName = getActionName(templateMeta);

        final Map<String, Object> mvcMetadata = EntityBuilderImpl.getMvcMetadata(componentPresentation.getComponentTemplate());

        final Map<String, String> routeValues = new HashMap<>();

        String routeValuesStrings = FieldUtils.getStringValue(templateMeta, "routeValues");
        if (routeValuesStrings != null) {
            for (String value : routeValuesStrings.split(",")) {
                final String[] parts = value.split(":");
                if (parts.length > 1 && !routeValues.containsKey(parts[0])) {
                    routeValues.put(parts[0], parts[1]);
                }
            }
        }

        return MvcDataImpl.newBuilder()
                .controllerAreaName(controllerNameParts[0])
                .controllerName(controllerNameParts[1])
                .areaName(viewNameParts[0])
                .viewName(viewNameParts[1])
                .regionAreaName(regionNameParts[0])
                .regionName(regionNameParts[1])
                .actionName(actionName)
                .metadata(mvcMetadata)
                .routeValues(routeValues)
                .build();
    }
}
