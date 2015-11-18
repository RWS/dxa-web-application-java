package com.sdl.webapp.tridion;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.config.EntitySemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.MvcDataImpl;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.EclItem;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl.ComponentEntity;
import com.sdl.webapp.tridion.fields.FieldConverterRegistry;
import com.sdl.webapp.tridion.fields.FieldUtils;
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

import static org.springframework.util.StringUtils.isEmpty;

@org.springframework.stereotype.Component
final class EntityBuilderImpl implements EntityBuilder {
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

    private static class GetEntityData {
        String viewName, schemaRoot;

        public GetEntityData(String viewName, String schemaRoot) {
            this.viewName = viewName;
            this.schemaRoot = schemaRoot;
        }
    }

    private static abstract class GetEntityClass<T> {
        protected abstract Class<? extends AbstractEntityModel> get(T data) throws DxaException, ContentProviderException;

        protected Class<? extends AbstractEntityModel> onException(T data) throws ContentProviderException {
            return null;
        }
    }

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

        Class<? extends AbstractEntityModel> entityClass = getEntityClass(new GetEntityClass<GetEntityData>() {
            @Override
            protected Class<? extends AbstractEntityModel> get(GetEntityData data) throws DxaException, ContentProviderException {
                final Class<? extends AbstractEntityModel> entityClass = (Class<? extends AbstractEntityModel>) viewModelRegistry.getViewEntityClass(data.viewName);
                if (entityClass == null) {
                    throw new ContentProviderException("Cannot determine entity type for view name: '" + data.viewName +
                            "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.");
                }
                return entityClass;
            }

            @Override
            protected Class<? extends AbstractEntityModel> onException(GetEntityData data) throws ContentProviderException {
                final Class<? extends AbstractEntityModel> entityClass =
                        (Class<? extends AbstractEntityModel>) semanticMappingRegistry.getEntityClass(data.schemaRoot);
                if (entityClass == null) {
                    throw new ContentProviderException("Cannot determine entity type for view name: '" + data.viewName +
                            "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.");
                }
                return entityClass;
            }
        }, new GetEntityData(viewName, component.getSchema().getRootElement()));

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

    @Override
    public EntityModel createEntity(final Component component, EntityModel originalEntityModel, final Localization localization)
            throws ContentProviderException {

        final SemanticSchema semanticSchema = getSemanticSchema(component, localization);
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
                throw new DxaException("Cannot find the entity class in mapped model types");
            }
        } catch (DxaException e) {
            throw new ContentProviderException("Cannot determine entity type for view name: '" + semanticTypeName +
                    "'. Please make sure that an entry is registered for this view name in the ViewModelRegistry.", e);
        }

        return createEntity(component, localization, entityClass, semanticSchema);
    }


    @Override
    public EntityModel createEntity(Component component, EntityModel originalEntityModel, Localization localization, Class<AbstractEntityModel> entityClass)
            throws ContentProviderException {
        return createEntity(component, localization, entityClass, getSemanticSchema(component, localization));
    }

    private EntityModel createEntity(Component component, Localization localization,
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


    private <T> Class<? extends AbstractEntityModel> getEntityClass(GetEntityClass<T> getEntityClass, T data) throws ContentProviderException {
        try {
            return getEntityClass.get(data);
        } catch (DxaException e) {
            return getEntityClass.onException(data);
        }
    }

    private SemanticSchema getSemanticSchema(Component component, Localization localization) {
        return localization.getSemanticSchemas().get(Long.parseLong(component.getSchema().getId().split("-")[1]));
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

    private void fillItemWithExternalMetadata(EclItem eclItem, Map<String, FieldSet> extensionData) {
        FieldSet externalEclFieldSet = extensionData.get("ECL-ExternalMetadata");
        Map<String, Object> externalMetadata = new HashMap<>(externalEclFieldSet.getContent().size());
        for (Map.Entry<String, Field> entry : externalEclFieldSet.getContent().entrySet()) {
            final List<Object> values = entry.getValue().getValues();

            if (values.size() > 0) {
                externalMetadata.put(entry.getKey(), values.get(0));
            }
        }
        eclItem.setExternalMetadata(externalMetadata);
    }

    private void fillItemWithEclData(EclItem eclItem, Map<String, FieldSet> extensionData) {
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

    private MvcData createMvcData(ComponentPresentation componentPresentation) {
        // todo remove duplication from MvcDataImpl
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
        String routeValuesStrings = FieldUtils.getStringValue(templateMeta, "routeValues");
        if (routeValuesStrings != null) {
            for (String value : routeValuesStrings.split(",")) {
                final String[] parts = value.split(":");
                if (parts.length > 1 && !routeValues.containsKey(parts[0])) {
                    routeValues.put(parts[0], parts[1]);
                }
            }
        }
        mvcData.setRouteValues(routeValues);
        mvcData.setMetadata(this.getMvcMetadata(componentPresentation.getComponentTemplate()));

        return mvcData;
    }

    private String[] getControllerNameParts(Map<String, Field> templateMeta) {
        String fullName = FieldUtils.getStringValue(templateMeta, "controller");
        if (isEmpty(fullName)) {
            fullName = DEFAULT_CONTROLLER_NAME;
        }
        return splitName(fullName);
    }

    private String getActionName(Map<String, Field> templateMeta) {
        String actionName = FieldUtils.getStringValue(templateMeta, "action");
        if (isEmpty(actionName)) {
            actionName = DEFAULT_ACTION_NAME;
        }
        return actionName;
    }

    private String[] getViewNameParts(ComponentTemplate componentTemplate) {
        String fullName = FieldUtils.getStringValue(componentTemplate.getMetadata(), "view");
        if (isEmpty(fullName)) {
            fullName = componentTemplate.getTitle().replaceAll("\\[.*\\]|\\s", "");
        }
        return splitName(fullName);
    }

    private String[] getRegionNameParts(Map<String, Field> templateMeta) {
        String fullName = FieldUtils.getStringValue(templateMeta, "regionView");
        if (isEmpty(fullName)) {
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
