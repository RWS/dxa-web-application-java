package com.sdl.webapp.common.api.contextengine;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

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
        Boolean aBoolean = claims.getSingleClaim("boolean", Boolean.class);
        String string = claims.getSingleClaim("string", String.class);
        Integer integer = claims.getSingleClaim("integer", Integer.class);
        Long aLong = claims.getSingleClaim("long", Long.class);
        Double aDouble = claims.getSingleClaim("double", Double.class);

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
        List<String> set = claims.getClaimsList("set", String.class);
        List<String> list = claims.getClaimsList("list", String.class);

        //then
        assertThat(set, hasItems("str1", "str2"));
        assertThat(set, hasSize(2));

        assertThat(list, hasItems("str1", "str2"));
        assertThat(list, hasSize(2));
    }

    @Test
    public void shouldConvertFromDoubleToInteger() {
        //given
        int expected = 123;
        TestClaims claims = new TestClaims();
        claims.setClaims(CLAIMS);

        //when
        Integer result = claims.getSingleClaim("double", Integer.class);

        //then
        assertNotNull(result);
        assertTrue(expected == result);
    }

    @Test
    public void shouldConvertFromIntegerToDouble() {
        //given
        double expected = 12345.0;
        TestClaims claims = new TestClaims();
        claims.setClaims(CLAIMS);

        //when
        Double result = claims.getSingleClaim("integer", Double.class);

        //then
        assertNotNull(result);
        assertTrue(expected == result);
    }

    @Test
    public void shouldConvertFromIntegerToShort() {
        //given
        short expected = 12345;
        TestClaims claims = new TestClaims();
        claims.setClaims(CLAIMS);

        //when
        Short result = claims.getSingleClaim("integer", Short.class);

        //then
        assertNotNull(result);
        assertTrue(expected == result);
    }

    @Test
    public void shouldConvertFromDoubleToByte() {
        //given
        byte expected = 123;
        TestClaims claims = new TestClaims();
        claims.setClaims(CLAIMS);

        //when
        Byte result = claims.getSingleClaim("double", Byte.class);

        //then
        assertNotNull(result);
        assertTrue(expected == result);
    }

    @Test
    public void shouldConvertFromDoubleToLong() {
        //given
        long expected = 123;
        TestClaims claims = new TestClaims();
        claims.setClaims(CLAIMS);

        //when
        Long result = claims.getSingleClaim("double", Long.class);

        //then
        assertNotNull(result);
        assertTrue(expected == result);
    }

    @Test
    public void shouldConvertFromDoubleToFloat() {
        //given
        Float expected = 123.45f;
        TestClaims claims = new TestClaims();
        claims.setClaims(CLAIMS);

        //when
        Float result = claims.getSingleClaim("double", Float.class);

        //then
        assertNotNull(result);
        assertTrue(expected.equals(result));
    }

    @Test
    public void shouldReturnNullIfValueIsNotInClaims() {
        //given
        TestClaims claims = new TestClaims();
        claims.setClaims(CLAIMS);

        //when
        Integer na = claims.getSingleClaim("na", Integer.class);

        //then
        assertNull(na);
    }

    @Test
    public void shouldReturnNullIfValueIsNotConvertible() {
        //given
        TestClaims claims = new TestClaims();
        claims.setClaims(CLAIMS);

        //when
        Boolean na = claims.getSingleClaim("double", Boolean.class);

        //then
        assertNull(na);
    }

    @Test
    public void shouldConvertAndCallToString() {
        //given
        TestClaims claims = new TestClaims();
        claims.setClaims(CLAIMS);

        //when
        String result = claims.getSingleClaim("double", String.class);

        //then
        assertEquals("123.45", result);
    }

    private static class TestClaims extends ContextClaims {

        public static final String TEST_CLAIMS_ASPECT = "test";

        @Override
        protected String getAspectName() {
            return TEST_CLAIMS_ASPECT;
        }
    }
}