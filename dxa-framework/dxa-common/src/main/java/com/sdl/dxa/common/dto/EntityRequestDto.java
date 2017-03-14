package com.sdl.dxa.common.dto;

import lombok.Builder;
import lombok.Value;

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

    /**
     * Default valeus for builder.
     */
    @SuppressWarnings("unused")
    public static class EntityRequestDtoBuilder {

        private String uriType = "tcm";
    }
}
