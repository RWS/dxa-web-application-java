package com.sdl.dxa.dd4t.providers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.tridion.modelservice.ModelServiceClient;
import com.sdl.dxa.tridion.modelservice.ModelServiceConfiguration;
import com.sdl.dxa.tridion.modelservice.exceptions.ItemNotFoundInModelServiceException;
import com.sdl.dxa.tridion.modelservice.exceptions.ModelServiceInternalServerErrorException;
import com.sdl.web.model.componentpresentation.ComponentPresentationImpl;
import com.tridion.dcp.ComponentPresentation;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.providers.ComponentPresentationProvider;
import org.dd4t.providers.impl.BrokerComponentPresentationProvider;

import javax.annotation.Resource;
import java.io.IOException;

import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class ModelServiceComponentPresentationProvider extends BrokerComponentPresentationProvider implements ComponentPresentationProvider {

    @Resource
    private ModelServiceClient modelServiceClient;

    @Resource
    private ModelServiceConfiguration modelServiceConfiguration;

    public void setModelServiceClient(ModelServiceClient modelServiceClient) {
        this.modelServiceClient = modelServiceClient;
    }

    public void setModelServiceConfiguration(ModelServiceConfiguration modelServiceConfiguration) {
        this.modelServiceConfiguration = modelServiceConfiguration;
    }

    @Override
    protected ComponentPresentation getComponentPresentation(int componentId, int templateId, int publicationId) throws ItemNotFoundException {
        String serviceUrl = fromUriString(modelServiceConfiguration.getEntityModelUrl())
                .queryParam("dcpType", "HIGHEST_PRIORITY")
                .build().toUriString();

        try {
            String cpContent = modelServiceClient.getForType(serviceUrl, String.class, "tcm", publicationId, componentId, templateId);
            JsonNode cp = new ObjectMapper().readTree(cpContent);
            return new ComponentPresentationImpl(
                    cp.get("NamespaceId").asInt(0),
                    cp.get("PublicationId").asInt(publicationId),
                    cp.get("ComponentId").asInt(componentId),
                    cp.get("ComponentTemplateId").asInt(templateId),
                    cp.get("Content").asText(),
                    cp.get("FileLocation").asText(),
                    cp.get("Dynamic").asBoolean());
        } catch (ItemNotFoundInModelServiceException e) {
            throw new ItemNotFoundException("Item for componentId = '" + componentId + "' and templateId = '" + +templateId + "' " +
                    "and publicationId = '" + publicationId + "' is not found in the Model Service", e);
        } catch (IOException e) {
            throw new ModelServiceInternalServerErrorException("Cannot parse content for Component Presentation, " +
                    "for [componentId = '" + componentId + "', templateId = '" + templateId + "', publicationId = '" + publicationId + "']", e);
        }
    }
}
