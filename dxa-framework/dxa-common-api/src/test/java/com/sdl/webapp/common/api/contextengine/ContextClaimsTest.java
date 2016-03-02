package com.sdl.webapp.common.api.contextengine;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ContextClaimsTest {

    private static final ImmutableMap<String, Object> CLAIMS = ImmutableMap.<String, Object>builder()
            .put(getClaimName("boolean"), true)
            .put(getClaimName("string"), "string")
            .put(getClaimName("integer"), 12345)
            .put(getClaimName("long"), 12345L)
            .put(getClaimName("double"), 123.45d)
            .put(getClaimName("set"), new HashSet<String>() {{
                add("str1");
                add("str2");
            }})
            .put(getClaimName("list"), new ArrayList<String>() {{
                add("str1");
                add("str2");
            }})
            .build();

    private static String getClaimName(String propertyName) {
        return TestClaims.TEST_CLAIMS_ASPECT + '.' + propertyName;
    }

    @Test
    public void shouldGetClaimName() throws Exception {
        //given
        TestClaims claims = new TestClaims();

        //when
        String width = claims.getClaimName("width");
        String osVersion = claims.getClaimName("os.version");

        //then
        assertEquals(getClaimName("width"), width);
        assertEquals(getClaimName("os.version"), osVersion);
    }


    @Test
    public void shouldGetClaimValue() throws Exception {
        //given
        TestClaims claims = new TestClaims();
        claims.setClaims(CLAIMS);

        //when
        Boolean aBoolean = claims.getClaimValue("boolean", Boolean.class);
        String string = claims.getClaimValue("string", String.class);
        Integer integer = claims.getClaimValue("integer", Integer.class);
        Long aLong = claims.getClaimValue("long", Long.class);
        Double aDouble = claims.getClaimValue("double", Double.class);

        //then
        assertEquals(true, aBoolean);
        assertEquals("string", string);
        assertTrue(Objects.equals(12345, integer));
        assertTrue(Objects.equals(12345L, aLong));
        assertTrue(Objects.equals(123.45d, aDouble));
    }

    @Test
    public void shouldGetClaimValues() throws Exception {
        //given
        TestClaims claims = new TestClaims();
        claims.setClaims(CLAIMS);

        //when
        List<String> set = claims.getClaimValues("set", String.class);
        List<String> list = claims.getClaimValues("list", String.class);

        //then
        Iterator<String> setIter = set.iterator();
        assertEquals("str1", setIter.next());
        assertEquals("str2", setIter.next());
        assertFalse(setIter.hasNext());

        Iterator<String> listIter = list.iterator();
        assertEquals("str1", listIter.next());
        assertEquals("str2", listIter.next());
        assertFalse(listIter.hasNext());
    }

    private static class TestClaims extends ContextClaims {

        public static final String TEST_CLAIMS_ASPECT = "test";

        @Override
        protected String getAspectName() {
            return TEST_CLAIMS_ASPECT;
        }
    }
}