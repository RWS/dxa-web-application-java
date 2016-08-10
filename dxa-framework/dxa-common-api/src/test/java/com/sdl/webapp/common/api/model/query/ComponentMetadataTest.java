package com.sdl.webapp.common.api.model.query;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ComponentMetadataTest {

    @Test
    public void shouldReturnCustomMeta() {
        //given
        Date date = new Date();
        Integer integer = 42;
        String str = "42";
        ComponentMetadata metadata = ComponentMetadata.builder().custom(
                ImmutableMap.<String, Object>of("date", date, "number", integer, "string", str)).build();

        //when
        @Nullable Date resultDate = metadata.getCustom("date", Date.class);
        @Nullable Integer resultNumber = metadata.getCustom("number", Integer.class);
        @Nullable String resultStr = metadata.getCustom("string", String.class);
        @Nullable Object typeUp = metadata.getCustom("string", Object.class);
        @Nullable String wrongType1 = metadata.getCustom("date", String.class);
        @Nullable Object notExist = metadata.getCustom("not exist", Object.class);

        //then
        assertEquals(date, resultDate);
        assertEquals(integer, resultNumber);
        assertEquals(str, resultStr);
        assertEquals(str, typeUp);
        assertNull(wrongType1);
        assertNull(notExist);
    }

}