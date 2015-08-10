package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.SemanticMapper;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntity;
import com.sdl.webapp.common.api.model.entity.EclItem;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.dd4t.fieldconverters.FieldConverterRegistry;
import org.dd4t.contentmodel.*;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.sdl.webapp.dd4t.fieldconverters.FieldUtils.getStringValue;

@Component
final class EntityBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(EntityBuilder.class);

    private static final String DEFAULT_AREA_NAME = "Core";
    private static final String DEFAULT_CONTROLLER_NAME = "Entity";
    private static final String DEFAULT_ACTION_NAME = "Entity";
    private static final String DEFAULT_REGION_NAME = "Main";

    private final ViewModelRegistry viewModelRegistry;

    private final SemanticMapper semanticMapper;

    private final FieldConverterRegistry fieldConverterRegistry;

    @Autowired
    EntityBuilder(ViewModelRegistry viewModelRegistry, SemanticMapper semanticMapper,
                  FieldConverterRegistry fieldConverterRegistry) {
        this.viewModelRegistry = viewModelRegistry;
        this.semanticMapper = semanticMapper;
        this.fieldConverterRegistry = fieldConverterRegistry;
    }

    Entity createEntity(ComponentPresentation componentPresentation, Localization localization)
            throws ContentProviderException {
        final org.dd4t.contentmodel.Component component = componentPresentation.getComponent();
        final String componentId = component.getId();
        LOG.debug("Creating entity for component: {}", componentId);

        final Map<String, Field> templateMeta = componentPresentation.getComponentTemplate().getMetadata();
        if (templateMeta == null) {
            LOG.warn("ComponentPresentation without template metadata, skipping: {}", componentId);
            return null;
        }

        final String viewName = getStringValue(templateMeta, "view");
        if (Strings.isNullOrEmpty(viewName)) {
            LOG.warn("ComponentPresentation without a view, skipping: {}", componentId);
            return null;
        }

        final Class<? extends AbstractEntity> entityClass = viewModelRegistry.getViewEntityClass(viewName);
        if (entityClass == null) {
            throw new ContentProviderException("Cannot determine entity type for view name: '" + viewName +
                    "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.");
        }

        final SemanticSchema semanticSchema = localization.getSemanticSchemas()
                .get(Long.parseLong(component.getSchema().getId().split("-")[1]));

        final AbstractEntity entity;
        try {
            entity = semanticMapper.createEntity(entityClass, semanticSchema.getSemanticFields(),
                    new DD4TSemanticFieldDataProvider(component, fieldConverterRegistry));
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
            if ( entity instanceof EclItem ) {
                final EclItem eclItem = (EclItem) entity;
                eclItem.setEclUrl(component.getTitle().replace("ecl:0", "ecl:" + localization.getId()));
            }
        }

        createEntityData(entity, componentPresentation);
        entity.setMvcData(createMvcData(componentPresentation));

        return entity;
    }

    private void createEntityData(AbstractEntity entity, ComponentPresentation componentPresentation) {
        final org.dd4t.contentmodel.Component component = componentPresentation.getComponent();
        final ComponentTemplate componentTemplate = componentPresentation.getComponentTemplate();

        ImmutableMap.Builder<String, String> entityDataBuilder = ImmutableMap.builder();

        if ( entity instanceof EclItem ) {
            entityDataBuilder.put("ComponentID", ((EclItem) entity).getEclUrl());
        }
        else {
            entityDataBuilder.put("ComponentID", component.getId());
        }
        entityDataBuilder.put("ComponentModified",
                ISODateTimeFormat.dateHourMinuteSecond().print(component.getRevisionDate()));
        entityDataBuilder.put("ComponentTemplateID", componentTemplate.getId());
        entityDataBuilder.put("ComponentTemplateModified",
                ISODateTimeFormat.dateHourMinuteSecond().print(componentTemplate.getRevisionDate()));

        entity.setEntityData(entityDataBuilder.build());

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
        for (String value : Strings.nullToEmpty(getStringValue(templateMeta, "routeValues")).split(",")) {
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
        String fullName = getStringValue(templateMeta, "controller");
        if (Strings.isNullOrEmpty(fullName)) {
            fullName = DEFAULT_CONTROLLER_NAME;
        }
        return splitName(fullName);
    }

    private String getActionName(Map<String, Field> templateMeta) {
        String actionName = getStringValue(templateMeta, "action");
        if (Strings.isNullOrEmpty(actionName)) {
            actionName = DEFAULT_ACTION_NAME;
        }
        return actionName;
    }

    private String[] getViewNameParts(ComponentTemplate componentTemplate) {
        String fullName = getStringValue(componentTemplate.getMetadata(), "view");
        if (Strings.isNullOrEmpty(fullName)) {
            fullName = componentTemplate.getTitle().replaceAll("\\[.*\\]|\\s", "");
        }
        return splitName(fullName);
    }

    private String[] getRegionNameParts(Map<String, Field> templateMeta) {
        String fullName = getStringValue(templateMeta, "regionView");
        if (Strings.isNullOrEmpty(fullName)) {
            fullName = DEFAULT_REGION_NAME;
        }
        return splitName(fullName);
    }

    private String[] splitName(String name) {
        final String[] parts = name.split(":");
        return parts.length > 1 ? parts : new String[] { DEFAULT_AREA_NAME, name };
    }

    private Map<String,Object> getMvcMetadata(ComponentTemplate componentTemplate) {

        // TODO: Move this code into a generic MvcDataHelper class

        Map<String,Object> metadata = new HashMap<>();
        Map<String,Field> metadataFields = componentTemplate.getMetadata();
        for ( String fieldName : metadataFields.keySet() ) {
            if ( fieldName.equals("view") ||
                    fieldName.equals("regionView") ||
                    fieldName.equals("controller") ||
                    fieldName.equals("action") ||
                    fieldName.equals("routeValues") ) {
                continue;
            }
            Field field = metadataFields.get(fieldName);
            if ( field.getValues().size() > 0 ) {
                metadata.put(fieldName, field.getValues().get(0).toString()); // Assume single-value text fields for template metadata
            }
        }
        return metadata;
    }
}
