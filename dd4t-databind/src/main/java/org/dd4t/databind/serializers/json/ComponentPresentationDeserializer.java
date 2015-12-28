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

package org.dd4t.databind.serializers.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.databind.TridionViewModel;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.databind.DataBindFactory;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.dd4t.databind.util.DataBindConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * test
 *
 * @author R. Kempees
 */
public class ComponentPresentationDeserializer extends StdDeserializer<ComponentPresentation> {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentPresentationDeserializer.class);
    private Class<? extends ComponentTemplate> concreteComponentTemplateClass = null;
    private Class<? extends Component> concreteComponentClass = null;

    public ComponentPresentationDeserializer (Class<? extends ComponentPresentation> componentPresentation, Class<? extends ComponentTemplate> componentTemplateClass, Class<? extends Component> concreteComponentClass) {
        super(componentPresentation);
        this.concreteComponentTemplateClass = componentTemplateClass;
        this.concreteComponentClass = concreteComponentClass;
    }


    @Override
    public ComponentPresentation deserialize (final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        final ObjectNode root = mapper.readTree(jsonParser);
        final ComponentPresentation componentPresentation = getConcreteComponentPresentation();

        if (!isConcreteClass(componentPresentation)) {
            return null;
        }

        final Iterator<Map.Entry<String, JsonNode>> fields = root.fields();

        JsonNode rawComponentData = null;
        String viewModelName = null;

        while (fields.hasNext()) {
            final Map.Entry<String, JsonNode> element = fields.next();
            final String key = element.getKey();

            LOG.trace(element.getKey() + "  " + element.getValue().toString());

            if (key.equalsIgnoreCase(DataBindConstants.COMPONENT_NODE_NAME)) {
                LOG.debug("Fishing out Component Data");
                rawComponentData = element.getValue();
                LOG.trace("Data is: {}", rawComponentData);
            } else if (key.equalsIgnoreCase(DataBindConstants.COMPONENT_TEMPLATE_NODE_NAME)) {
                LOG.debug("Deserializing Component Template Data.");
                final JsonParser parser = element.getValue().traverse();
                final ComponentTemplate componentTemplate = JsonDataBinder.getGenericMapper().readValue(parser, this.concreteComponentTemplateClass);
                if (componentPresentation != null) {
                    componentPresentation.setComponentTemplate(componentTemplate);
                }
                viewModelName = DataBindFactory.findComponentTemplateViewName(componentTemplate);
                LOG.debug("Found view model name: " + viewModelName);
            } else if (key.equalsIgnoreCase(DataBindConstants.IS_DYNAMIC_NODE)) {
                final String isDynamic = element.getValue().asText().toLowerCase();
                setIsDynamic(componentPresentation, isDynamic);
            } else if (key.equalsIgnoreCase(DataBindConstants.ORDER_ON_PAGE_NODE)) {
                if (componentPresentation != null) {
                    componentPresentation.setOrderOnPage(element.getValue().asInt());
                }
            } else if (key.equalsIgnoreCase(DataBindConstants.RENDERED_CONTENT_NODE)) {
                if (componentPresentation != null) {
                    componentPresentation.setRenderedContent(element.getValue().asText());
                }
            }
        }

        if (rawComponentData == null) {
            LOG.error("No component data found.");
            return componentPresentation;
        }

        try {
            renderComponentData(componentPresentation, rawComponentData, viewModelName, DataBindFactory.getRootElementName(rawComponentData));
        } catch (SerializationException e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new IOException(e);
        }
        return componentPresentation;
    }

    private void renderComponentData (final ComponentPresentation componentPresentation, final JsonNode rawComponentData, final String viewModelName, final String rootElementName) throws IOException, SerializationException {
//		if ((StringUtils.isEmpty(viewModelName) && StringUtils.isEmpty(rootElementName)) || DataBindFactory.renderGenericComponentsOnly()) {
//			LOG.debug("No view name set on Component Template and no rootElementName found or only rendering to Generic Component");
        try {
            // Note: Components actually always have to be set
            // TODO: figure out a way to not have to do it.
            componentPresentation.setComponent(DataBindFactory.buildComponent(rawComponentData, this.concreteComponentClass));
        } catch (SerializationException e) {
            throw new IOException(e.getLocalizedMessage(), e);
        }
//		} else {

        final Set<String> modelNames = new HashSet<>();

        if (StringUtils.isNotEmpty(viewModelName)) {
            modelNames.add(viewModelName);
        }
        if (!rootElementName.equals(viewModelName)) {
            modelNames.add(rootElementName);
        }

        final Map<String, BaseViewModel> models = DataBindFactory.buildModels(rawComponentData, modelNames, componentPresentation.getComponentTemplate().getId());

        if (models == null || models.isEmpty()) {
            if (DataBindFactory.renderDefaultComponentsIfNoModelFound()) {
                componentPresentation.setComponent(DataBindFactory.buildComponent(rawComponentData, this.concreteComponentClass));
            } else {
                LOG.warn("No model found for CT {}, with component: {}. Fall back deserialization is also turned off.", componentPresentation.getComponentTemplate().getId(), componentPresentation.getComponent().getId());
            }

        } else {
            for (BaseViewModel model : models.values()) {
                if (model instanceof TridionViewModel && ((TridionViewModel) model).setGenericComponentOnComponentPresentation()) {
                    LOG.debug("Also setting a Component object on the CP.");
                    componentPresentation.setComponent(DataBindFactory.buildComponent(rawComponentData, this.concreteComponentClass));
                }
                if (model.setRawDataOnModel()) {
                    LOG.debug("Setting raw string data on model.");
                    model.setRawData(rawComponentData.toString());
                }
            }
            componentPresentation.setViewModel(models);
        }
//		}
    }

    private boolean isConcreteClass (final ComponentPresentation componentPresentation) {
        // This check should be good enough
        if (componentPresentation == null || componentPresentation.getClass().isInterface()) {
            LOG.error("No concrete ComponentPresentation class found! not proceeding.");
            return false;
        }
        return true;
    }

    private ComponentPresentation getConcreteComponentPresentation () {

        final String handledType = this.handledType().toString();
        LOG.debug("Type for ComponentPresentation injection: {}", handledType);

        if (ComponentPresentation.class.isAssignableFrom(this.handledType())) {
            try {
                return (ComponentPresentation) this.handledType().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LOG.error(e.getLocalizedMessage(), e);
                return null;
            }
        }
        LOG.error("Concrete type: " + this.handledType().toString() + " does not implement ComponentPresentation");
        return null;
    }

    private static void setIsDynamic (final ComponentPresentation componentPresentation, final String isDynamic) {
        if (isDynamic.equalsIgnoreCase(DataBindConstants.TRUE_STRING) || isDynamic.equalsIgnoreCase(DataBindConstants.FALSE_STRING)) {
            componentPresentation.setIsDynamic(Boolean.parseBoolean(isDynamic));
        } else {
            componentPresentation.setIsDynamic(false);
        }
    }
}
