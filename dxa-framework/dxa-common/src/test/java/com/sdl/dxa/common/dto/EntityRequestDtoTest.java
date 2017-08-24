package com.sdl.dxa.common.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntityRequestDtoTest {

    @Test
    public void shouldSetPublicationId_AndEntityId() {
        //when
        EntityRequestDto dto = EntityRequestDto.builder(42, "1-2").build();

        //then
        assertRequestValuesSet(42, "1-2", 1, 2, dto);
    }

    @Test
    public void shouldSetPublicationId_AndComponentId() {
        //when
        EntityRequestDto dto = EntityRequestDto.builder(42, 1).build();

        //then
        assertRequestValuesSet(42, "1", 1, 0, dto);
    }

    @Test
    public void shouldSetPublicationId_AndComponentId_AndTemplateId() {
        //when
        EntityRequestDto dto = EntityRequestDto.builder(42, 1, 2).build();

        //then
        assertRequestValuesSet(42, "1-2", 1, 2, dto);
    }

    @Test
    public void shouldReplacePartsOfEntityId_IfAlreadySet() {
        //given 

        //when
        EntityRequestDto dto = EntityRequestDto.builder(42, "1-2").build();
        assertRequestValuesSet(42, "1-2", 1, 2, dto);

        dto = dto.toBuilder().componentId(3).build();
        assertRequestValuesSet(42, "3-2", 3, 2, dto);

        dto = dto.toBuilder().templateId(4).build();
        assertRequestValuesSet(42, "3-4", 3, 4, dto);
    }

    @Test
    public void shouldSetStringPublicationId() {
        //given 

        //when
        EntityRequestDto dto = EntityRequestDto.builder("42", "1-2").build();

        //then
        assertRequestValuesSet(42, "1-2", 1, 2, dto);
    }

    private void assertRequestValuesSet(int publicationId, String entityId, int componentId, int templateId, EntityRequestDto dto) {
        assertEquals(publicationId, dto.getPublicationId());
        assertEquals(entityId, dto.getEntityId());
        assertEquals(componentId, dto.getComponentId());
        assertEquals(templateId, dto.getTemplateId());
    }
}