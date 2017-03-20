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
@Builder
public class EntityRequestDto {

    private int publicationId;

    private int componentId;

    private int templateId;

    /**
     * DXA format of entity ID contains component and template IDs separated with "{@code -}".
     */
    private String entityId;

    private String uriType;

    private boolean resolveLink;

    /**
     * Default values for builder.
     */
    @SuppressWarnings("unused")
    public static class EntityRequestDtoBuilder {

        private String uriType = "tcm";

        private boolean resolveLink = true;

        public EntityRequestDtoBuilder entityId(@NotNull String entityId) {
            Assert.isTrue(entityId.matches("\\d+-\\d+"), "Entity ID should be of format CompId-TemplateId");
            this.entityId = entityId;
            List<String> parts = Splitter.on("-").splitToList(entityId);
            componentId(Integer.parseInt(parts.get(0)));
            templateId(Integer.parseInt(parts.get(1)));
            return this;
        }
    }
}
