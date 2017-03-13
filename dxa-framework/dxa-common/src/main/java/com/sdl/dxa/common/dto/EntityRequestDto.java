package com.sdl.dxa.common.dto;

import lombok.Value;

/**
 * Data transfer object (DTO) for page requests.
 */
@Value(staticConstructor = "build")
public class EntityRequestDto {

    private int publicationId;

    private int componentId;

    private int templateId;
}
