package com.sdl.dxa.caching;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocalizationAwareKeyGeneratorTest {

    @Mock
    private WebRequestContext webRequestContext;

    @Mock
    private Localization localization;

    private LocalizationAwareKeyGenerator keyGenerator = new LocalizationAwareKeyGenerator();

    @Before
    public void init() {
        when(webRequestContext.getLocalization()).thenReturn(localization);
        when(localization.getId()).thenReturn("42");
        ReflectionTestUtils.setField(keyGenerator, "webRequestContext", webRequestContext);
    }

    @Test
    public void shouldDelegateGeneration_ToSimpleKeyGenerator() {
        //given
        Object expected = new LocalizationAwareCacheKey("42", SimpleKeyGenerator.generateKey("a", "b", "c"));

        //when
        Object actual = keyGenerator.generate("a", "b", "c");
        Object actual2 = keyGenerator.generate(null, null, "a", "b", "c");

        //then
        assertEquals(expected, actual);
        assertEquals(expected, actual2);
    }

}