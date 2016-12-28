/*
 * Copyright (c) 2015 Radagio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.databind.builder.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Embedded;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.databind.ModelConverter;
import org.dd4t.core.databind.TridionViewModel;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.databind.DataBindFactory;
import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.builder.AbstractModelConverter;
import org.dd4t.databind.util.DataBindConstants;
import org.dd4t.databind.util.JsonUtils;
import org.dd4t.databind.util.TypeUtils;
import org.dd4t.databind.viewmodel.base.ModelFieldMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JsonModelConverter.
 *
 * @author R. Kempees
 * @since 19/11/14.
 */
public class JsonModelConverter extends AbstractModelConverter implements ModelConverter {
    private static final Logger LOG = LoggerFactory.getLogger(JsonModelConverter.class);

    private Class<? extends org.dd4t.contentmodel.Field> concreteFieldImpl;

    public JsonModelConverter () {

    }

    @Override
    public <T extends BaseViewModel> T convertSource (final Object data, final T model) throws SerializationException {

        if (!JsonUtils.isValidJsonNode(data)) {
            LOG.debug("No data or not a JsonNode - nothing to do.");
            return null;
        }

        JsonNode rawJsonData = (JsonNode) data;

        LOG.info("Conversion start.");
        this.concreteFieldImpl = JsonDataBinder.getInstance().getConcreteFieldImpl();
        if (model instanceof TridionViewModel) {
            LOG.debug("We have a Tridion view model. Setting additional properties");
            setTridionProperties((TridionViewModel) model, rawJsonData);
        }

        boolean isRootComponent = true;

        JsonNode contentFields = null;
        JsonNode metadataFields = null;
        Component.ComponentType componentType = Component.ComponentType.UNKNOWN;
        if (rawJsonData.has(DataBindConstants.COMPONENT_TYPE)) {
            componentType = Component.ComponentType.findByValue(rawJsonData.get(DataBindConstants.COMPONENT_TYPE).intValue());
        }

        if (componentType == Component.ComponentType.NORMAL) {
            if (rawJsonData.has(DataBindConstants.COMPONENT_FIELDS)) {
                contentFields = rawJsonData.get(DataBindConstants.COMPONENT_FIELDS);
            }
        } else if (componentType == Component.ComponentType.MULTIMEDIA && rawJsonData.has(DataBindConstants.MULTIMEDIA)) {
            contentFields = rawJsonData.get(DataBindConstants.MULTIMEDIA);

        }

        if (rawJsonData.has(DataBindConstants.METADATA_FIELDS)) {
            metadataFields = rawJsonData.get(DataBindConstants.METADATA_FIELDS);
        }

        if (contentFields == null && metadataFields == null) {
            isRootComponent = false;
        }

        buildModelProperties(model, rawJsonData, isRootComponent, contentFields, metadataFields, componentType);


        return model;
    }

    private <T extends BaseViewModel> void buildModelProperties (final T model, final JsonNode rawJsonData, final boolean isRootComponent, final JsonNode contentFields, final JsonNode metadataFields, final Component.ComponentType componentType) throws SerializationException {
        // TODO: mandatory but missing fields need their XPath set as well..
        final Map<String, Object> modelProperties = model.getModelProperties();

        try {
            for (Map.Entry<String, Object> entry : modelProperties.entrySet()) {
                final String fieldName = entry.getKey();
                LOG.debug("Key:{}", fieldName);

                ModelFieldMapping m = (ModelFieldMapping) entry.getValue();

                String fieldKey;
                fieldKey = getFieldKeyForModelProperty(fieldName, m);

                boolean isEmbedabble = false;
                if (!rawJsonData.has(DataBindConstants.FIELD_TYPE_KEY) && !isRootComponent) {
                    isEmbedabble = true;
                } else if (rawJsonData.has(DataBindConstants.FIELD_TYPE_KEY) && (FieldType.findByValue(rawJsonData.get(DataBindConstants.FIELD_TYPE_KEY).intValue()) == FieldType.EMBEDDED)) {
                    isEmbedabble = true;
                }

                if (componentType == Component.ComponentType.NORMAL || componentType == Component.ComponentType.UNKNOWN || m.getViewModelProperty().isMetadata()) {
                    final JsonNode currentNode = getJsonNodeToParse(fieldKey, rawJsonData, isRootComponent, isEmbedabble, contentFields, metadataFields, m);
                    // Since we are now now going from modelproperty > fetch data, the data might actually be null
                    if (currentNode != null) {
                        this.buildField(model, fieldName, currentNode, m);

                    }
                } else if (componentType == Component.ComponentType.MULTIMEDIA) {
                    if (contentFields.has(fieldName)) {
                        this.buildMultimediaField(model, fieldName, contentFields.get(fieldName), m);
                    }
                }
            }
        } catch (IllegalAccessException | IOException e) {
            LOG.error("Error setting field!", e);
        }
    }

    /**
     * Searches for the Json node to set on the model field in the Json data.
     *
     * @param entityFieldName The annotated model property. Used to search the Json node
     * @param rawJsonData     The Json data representing a node inside a child node of a component. Used for
     *                        embedded fields and component link fields
     * @param isRootComponent A flag to check whether the current Json node is the component node. If it is
     *                        the case, then a choice is made whether to fetch the metadata or the normal content
     *                        node.
     * @param contentFields   The content node
     * @param metadataFields  The metadata node
     * @param m               The current model field that is parsing at the moment
     * @return the Json node found under the entityFieldName key or null
     */
    private static JsonNode getJsonNodeToParse (final String entityFieldName, final JsonNode rawJsonData, final boolean isRootComponent, final boolean isEmbeddable, final JsonNode contentFields, final JsonNode metadataFields, final ModelFieldMapping m) {

        final JsonNode currentNode;
        if (isRootComponent) {
            if (m.getViewModelProperty().isMetadata()) {
                currentNode = metadataFields;
            } else {
                currentNode = contentFields;
            }
        } else {
            currentNode = rawJsonData;
        }

        if (currentNode != null) {
            if (isRootComponent || isEmbeddable) {
                return currentNode.get(entityFieldName);
            }
            return currentNode;
        }
        return null;
    }

    // TODO: we shouldnt need to have a separate method for this. The Json should be
    // constructed in such a way that it's 1:1 mappable, meaning a FieldType has to be there
    private <T extends BaseViewModel> void buildMultimediaField (final T model, final String fieldName, final JsonNode currentField, final ModelFieldMapping modelFieldMapping) throws IllegalAccessException {
        final Field modelField = modelFieldMapping.getField();
        modelField.setAccessible(true);
        setXPathForXpm(model, fieldName, currentField, modelField);

        Class<?> fieldTypeOfFieldToSet = TypeUtils.determineTypeOfField(modelField);

        // no multivalued fields here, Sir
        // TODO: see if we're going to set BinaryData here
        if (fieldTypeOfFieldToSet == String.class) {
            modelField.set(model, currentField.textValue());
        } else if (fieldTypeOfFieldToSet == int.class || fieldTypeOfFieldToSet == Integer.class) {
            modelField.set(model, currentField.intValue());
        }
    }

    private <T extends BaseViewModel> void buildField (final T model, final String fieldName, final JsonNode currentField, final ModelFieldMapping modelFieldMapping) throws IllegalAccessException, SerializationException, IOException {

        final Field modelField = modelFieldMapping.getField();
        modelField.setAccessible(true);

        FieldType tridionDataFieldType = FieldType.UNKNOWN;
        if (currentField.has(DataBindConstants.FIELD_TYPE_KEY)) {
            tridionDataFieldType = FieldType.findByValue(currentField.get(DataBindConstants.FIELD_TYPE_KEY).intValue());
        }
        LOG.debug("Tridion field type: {}", tridionDataFieldType);

        Class<?> fieldTypeOfFieldToSet = TypeUtils.determineTypeOfField(modelField);

        boolean modelFieldIsRegularEmbeddedType = FieldSet.class.isAssignableFrom(fieldTypeOfFieldToSet) || Embedded.class.isAssignableFrom(fieldTypeOfFieldToSet);

        final List<JsonNode> nodeList = new ArrayList<>();
        if (tridionDataFieldType.equals(FieldType.COMPONENTLINK) || tridionDataFieldType.equals(FieldType.MULTIMEDIALINK)) {
            fillLinkedComponentValues(currentField, nodeList);
        } else if (tridionDataFieldType == FieldType.EMBEDDED && !modelFieldIsRegularEmbeddedType) {

            handleEmbeddedContent(currentField, nodeList);
        } else if (tridionDataFieldType == FieldType.UNKNOWN) {
            // we're in the embedded scenario where there is no field type
            // This is where the serializer passes when it's trying to deserialize the actual field in an embedded
            // component.

            // add more info, like the embeddedschema info, XPM info here
            // Best thing to do may be to just add required values to
            // an embedded base class

            if (currentField.has(fieldName)) {
                nodeList.add(currentField.get(fieldName));
            }
        } else {
            nodeList.add(currentField);
        }

        if (nodeList.isEmpty()) {
            LOG.debug("Nothing to do.");
            return;
        }


        setXPathForXpm(model, fieldName, currentField, modelField);

        deserializeAndBuildModels(model, fieldName, modelField, tridionDataFieldType, nodeList);
    }

    private <T extends BaseViewModel> void setXPathForXpm (final T model, final String fieldName, final JsonNode currentField, final Field modelField) {
        if (model instanceof TridionViewModel && currentField != null && currentField.has(DataBindConstants.XPATH)) {
            boolean isMultiValued = false;
            if (modelField.getType().equals(List.class)) {
                isMultiValued = true;
            }
            String xpath = currentField.get(DataBindConstants.XPATH).asText();

            ((TridionViewModel) model).addXpmEntry(fieldName, xpath, isMultiValued);
        }
    }

    private <T extends BaseViewModel> void deserializeAndBuildModels (final T model, final String fieldName, final Field modelField, final FieldType tridionDataFieldType, final List<JsonNode> nodeList) throws SerializationException, IllegalAccessException, IOException {
        if (modelField.getType().equals(List.class)) {
            final Type parametrizedType = TypeUtils.getRuntimeTypeOfTypeParameter(modelField.getGenericType());

            if (TypeUtils.classIsViewModel((Class<?>) parametrizedType) || DataBindFactory.classHasViewModelDerivatives(((Class<?>) parametrizedType).getCanonicalName())) {
                for (JsonNode node : nodeList) {


                    if (!node.has(DataBindConstants.ROOT_ELEMENT_NAME)) {
                        checkTypeAndBuildModel(model, fieldName, node, modelField, (Class<T>) parametrizedType);
                    } else {
                        LOG.debug("not handling a schemaNode.");
                    }
                }

            } else {
                for (JsonNode node : nodeList) {
                    if (!node.has(DataBindConstants.ROOT_ELEMENT_NAME)) {
                        deserializeGeneric(model, node, modelField, tridionDataFieldType);
                    } else {
                        LOG.debug("not handling a schemaNode.");
                    }
                }
            }

        } else if (TypeUtils.classIsViewModel(modelField.getType()) || DataBindFactory.classHasViewModelDerivatives(modelField.getType().getCanonicalName())) {
            final Class<T> modelClassToUse = (Class<T>) modelField.getType();
            checkTypeAndBuildModel(model, fieldName, nodeList.get(0), modelField, modelClassToUse);
        } else {
            deserializeGeneric(model, nodeList.get(0), modelField, tridionDataFieldType);
        }
    }

    private static void handleEmbeddedContent (final JsonNode currentField, final List<JsonNode> nodeList) {
        final JsonNode embeddedNode = currentField.get(DataBindConstants.EMBEDDED_VALUES_NODE);
        // This is a fix for when we are already in an embedded node. The Json unfortunately
        // keeps sibling nodes in this child, which has FieldType embedded, while we're actually already in
        // that node's Values

        final JsonNode schemaNode = currentField.get(DataBindConstants.EMBEDDED_SCHEMA_FIELD_NAME);
        if (embeddedNode != null) {
            final Iterator<JsonNode> embeddedIterator = embeddedNode.elements();
            while (embeddedIterator.hasNext()) {
                addEmbeddedNodeAndSchemaInfo(nodeList, schemaNode, embeddedIterator);
            }
        } else {
            final Iterator<JsonNode> currentFieldElements = currentField.elements();
            while (currentFieldElements.hasNext()) {

                addEmbeddedNodeAndSchemaInfo(nodeList, schemaNode, currentFieldElements);
            }
        }
    }

    private static void addEmbeddedNodeAndSchemaInfo (final List<JsonNode> nodeList, final JsonNode schemaNode, final Iterator<JsonNode> embeddedIterator) {
        ObjectNode embeddedValue = (ObjectNode) embeddedIterator.next();

        if (schemaNode != null && !embeddedValue.has(DataBindConstants.EMBEDDED_SCHEMA_FIELD_NAME)) {
            embeddedValue.set(DataBindConstants.EMBEDDED_SCHEMA_FIELD_NAME, schemaNode);
        }
        nodeList.add(embeddedValue);
    }

    private static void fillLinkedComponentValues (final JsonNode currentField, final List<JsonNode> nodeList) {
        // Get the actual values from the values
        // if the Model's field is List, grab all embedded values
        // if it's a normal class (ComponentImpl or similar), just get the first

        final Iterator<JsonNode> nodes = currentField.get(DataBindConstants.LINKED_COMPONENT_VALUES_NODE).elements();
        while (nodes.hasNext()) {
            nodeList.add(nodes.next());
        }
    }

    /**
     * Deserializes in a Strongly Typed Model.
     *
     * @param model           the model to build
     * @param fieldName       the current field name
     * @param currentField    the current Json node
     * @param modelField      the model property
     * @param modelClassToUse the Model class
     * @param <T>             the model class extending from BaseViewModel
     * @throws SerializationException serialization issues
     * @throws IllegalAccessException Class instantiation issues
     */
    private static <T extends BaseViewModel> void checkTypeAndBuildModel (final T model, final String fieldName, final JsonNode currentField, final Field modelField, final Class<T> modelClassToUse) throws SerializationException, IllegalAccessException {
        if (!model.getClass().equals(modelField.getType())) {
            LOG.debug("Building a model or Component for field:{}, type: {}", fieldName, modelField.getType().getName());
            final BaseViewModel strongModel = buildModelForField(currentField, modelClassToUse);

            if (modelField.getType().equals(List.class)) {
                addToListTypeField(model, modelField, strongModel);
            } else {
                modelField.set(model, strongModel);
            }
        } else {
            LOG.error("Type for field type: {} is the same as the type for this view model: {}. This is NOT supported because of infinite loops. Work around this by creating a separate field type.", model.getClass().getCanonicalName(), modelField.getType().getCanonicalName());
        }
    }

    private static <T extends BaseViewModel> BaseViewModel buildModelForField (final JsonNode currentField, final Class<T> modelClassToUse) throws SerializationException {

        if (Modifier.isAbstract(modelClassToUse.getModifiers()) || Modifier.isInterface(modelClassToUse.getModifiers())) {

            // Get root element name
            final String rootElementName = getRootElementNameFromComponentOrEmbeddedField(currentField);
            if (StringUtils.isNotEmpty(rootElementName)) {
                // attempt get a concrete class for this interface

                final Class<? extends BaseViewModel> concreteClass = DataBindFactory.getModelClassesForInterfaceOrAbstractField(modelClassToUse.getCanonicalName(), rootElementName);
                if (concreteClass == null) {
                    LOG.error("Attempt to find a concrete model class for interface or abstract class: {} failed miserably as there was no registered class for root element name: '{}' Will return null.", modelClassToUse.getCanonicalName(), rootElementName);
                    return null;
                }
                LOG.debug("Building: {}", concreteClass.getCanonicalName());
                return getBaseViewModel(currentField, concreteClass);
            } else {
                LOG.error("Attempt to find a concrete model class for interface or abstract class: {} failed miserably as a root element name could not be found. Will return null.", modelClassToUse.getCanonicalName());
                return null;
            }

        } else {

            return getBaseViewModel(currentField, modelClassToUse);
        }
    }

    private static String getRootElementNameFromComponentOrEmbeddedField (final JsonNode currentField) {
        final String rootElementName = DataBindFactory.getRootElementName(currentField);

        if (StringUtils.isNotEmpty(rootElementName)) {
            return rootElementName;
        }

        if (currentField.has(DataBindConstants.EMBEDDED_SCHEMA_FIELD_NAME)) {
            final JsonNode schemaNode = currentField.get(DataBindConstants.EMBEDDED_SCHEMA_FIELD_NAME);

            if (schemaNode.hasNonNull(DataBindConstants.ROOT_ELEMENT_NAME)) {
                String nodeTypeName = schemaNode.get(DataBindConstants.ROOT_ELEMENT_NAME).textValue();
                LOG.debug("RootElementName is: {}", nodeTypeName);
                return nodeTypeName;
            }
        }
        return null;
    }

    private static <T extends BaseViewModel> BaseViewModel getBaseViewModel (final JsonNode currentField, final Class<T> modelClassToUse) throws SerializationException {
        final BaseViewModel strongModel = DataBindFactory.buildModel(currentField, modelClassToUse, "");
        final ViewModel viewModelParameters = modelClassToUse.getAnnotation(ViewModel.class);
        if (viewModelParameters.setRawData()) {
            strongModel.setRawData(currentField.toString());
        }
        return strongModel;
    }

    private <T extends BaseViewModel> void deserializeGeneric (final T model, final JsonNode currentField, final Field f, final FieldType fieldType) throws IOException, IllegalAccessException, SerializationException {
        LOG.debug("Field Type: " + f.getType().getCanonicalName());

        if (currentField.has(DataBindConstants.COMPONENT_TYPE)) {
            LOG.debug("Building a linked Component or Multimedia component");
            final Component component = JsonDataBinder.getInstance().buildComponent(currentField, JsonDataBinder.getInstance().getConcreteComponentImpl());
            setFieldValue(model, f, component, fieldType);
        } else {
            final org.dd4t.contentmodel.Field renderedField = JsonUtils.renderComponentField(currentField, this.concreteFieldImpl);
            LOG.trace("Rendered Field is: {} ", renderedField.toString());
            LOG.debug("Field Type is: {}", f.getType().toString());
            setFieldValue(model, f, renderedField, fieldType);
        }
    }

    private void setTridionProperties (final TridionViewModel model, final JsonNode rawComponent) {
        model.setLastPublishDate(JsonUtils.getDateFromField(DataBindConstants.LAST_PUBLISHED_DATE, rawComponent));
        model.setLastModified(JsonUtils.getDateFromField(DataBindConstants.LAST_MODIFIED_DATE, rawComponent));
        model.setTcmUri(JsonUtils.getTcmUriFromField(DataBindConstants.ID, rawComponent));
    }
}
