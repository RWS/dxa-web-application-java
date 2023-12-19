package com.sdl.webapp.common.util;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static com.sdl.webapp.common.util.CollectionUtils.getByCompoundKeyOrAlternative;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollectionUtilsTest {

    @Test
    public void shouldReturnAlternative_IfKeyIsNullOrEmpty() {
        assertEquals("1", getByCompoundKeyOrAlternative(null, null, "1", String.class));
        assertEquals("1", getByCompoundKeyOrAlternative("", null, "1", String.class));
    }

    @Test
    public void shouldFindDifferentValuesInMap() {
        //given
        //noinspection ConstantConditions
        ImmutableMap<String, Object> map = ImmutableMap.of(
                "Test1", "Test1_v",
                "Test2", ImmutableMap.of(
                        "_Test2_1", "_Test2_1_v",
                        "_Test2_2", ImmutableMap.of(
                                "_Test2_2_1", "__Test2_2_1_v",
                                "Test_Map", new HashMap<String, String>() {{
                                    put("Test_null", null);
                                    put("Test_InMap", "Value");
                                }}
                        )
                ),
                "Test3", "Test3_v",
                "Test_Class", String.class);


        assertEquals("Test1_v", getByCompoundKeyOrAlternative("Test1", map, "Alt1", String.class));
        assertEquals("Test3_v", getByCompoundKeyOrAlternative("Test3", map, "Alt3", String.class));
        assertEquals("Alt_Unknown", getByCompoundKeyOrAlternative("Unknown", map, "Alt_Unknown", String.class));
        assertEquals(String.class, getByCompoundKeyOrAlternative("Test_Class", map, Integer.class, Class.class));
        assertEquals(Integer.class, getByCompoundKeyOrAlternative("Test_Class_Unknown", map, Integer.class, Class.class));

        assertEquals(Integer.class, getByCompoundKeyOrAlternative("Test3", map, Integer.class, Class.class));

        assertEquals("Alt_Null", getByCompoundKeyOrAlternative("Test2/_Test2_2/Test_Map/Test_null", map, "Alt_Null", String.class));
        assertEquals("Value", getByCompoundKeyOrAlternative("Test2/_Test2_2/Test_Map/Test_InMap", map, "Alt", String.class));


        assertEquals("_Test2_1_v", getByCompoundKeyOrAlternative("Test2/_Test2_1", map, "", String.class));
        assertEquals("__Test2_2_1_v", getByCompoundKeyOrAlternative("Test2/_Test2_2/_Test2_2_1", map, "", String.class));

        //slashes shouldn't matter at the beginning nor at the end
        assertEquals("__Test2_2_1_v", getByCompoundKeyOrAlternative("/Test2/_Test2_2/_Test2_2_1/", map, "", String.class));
        assertEquals("__Test2_2_1_v", getByCompoundKeyOrAlternative("Test2/_Test2_2/_Test2_2_1/", map, "", String.class));
        assertEquals("__Test2_2_1_v", getByCompoundKeyOrAlternative("/Test2/_Test2_2/_Test2_2_1", map, "", String.class));
    }
}