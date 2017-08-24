package com.sdl.dxa.common.dto;

import com.google.common.base.Splitter;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Data transfer object (DTO) for page requests.
 */
@Value
@Builder(toBuilder = true, builderMethodName = "hiddenBuilder")
public class EntityRequestDto {

    protected static final String COMPONENT_TEMPLATE_IDS_FORMAT = "%s-%s";

    private int publicationId;

    private int componentId;

    private int templateId;

    /**
     * DXA format of entity ID contains component and template IDs separated with "{@code -}".
     */
    private String entityId;

    private String uriType;

    private boolean resolveLink;

    public static EntityRequestDtoBuilder builder(String publicationId, String entityId) {
        return builder(Integer.valueOf(publicationId), entityId);
    }

    public static EntityRequestDtoBuilder builder(int publicationId, String entityId) {
        return hiddenBuilder().publicationId(publicationId).entityId(entityId);
    }

    public static EntityRequestDtoBuilder builder(int publicationId, int componentId) {
        return hiddenBuilder().publicationId(publicationId).componentId(componentId);
    }

    public static EntityRequestDtoBuilder builder(int publicationId, int componentId, int templateId) {
        return hiddenBuilder().publicationId(publicationId).entityId(String.format(COMPONENT_TEMPLATE_IDS_FORMAT, componentId, templateId));
    }

    private static EntityRequestDtoBuilder hiddenBuilder() {
        return new EntityRequestDtoBuilder();
    }

    /**
     * Default values for builder.
     */
    @SuppressWarnings("unused")
    public static class EntityRequestDtoBuilder {

        private String uriType = "tcm";

        private boolean resolveLink = true;

        private EntityRequestDtoBuilder() {
        }

        public EntityRequestDtoBuilder componentId(int componentId) {
            return entityId(String.format(COMPONENT_TEMPLATE_IDS_FORMAT, componentId, templateId));
        }

        public EntityRequestDtoBuilder templateId(int templateId) {
            return entityId(String.format(COMPONENT_TEMPLATE_IDS_FORMAT, componentId, templateId));
        }

        public EntityRequestDtoBuilder entityId(@NotNull String entityId) {
            Assert.isTrue(entityId.matches("\\d+-\\d+"), "Entity ID should be of format CompId-TemplateId");
            List<String> parts = Splitter.on("-").splitToList(entityId);
            int componentId = Integer.parseInt(parts.get(0));
            int templateId = Integer.parseInt(parts.get(1));

            this.entityId = templateId == 0 ? parts.get(0) : entityId;
            this.componentId = componentId;
            this.templateId = templateId;
            return this;
        }
    }
}
