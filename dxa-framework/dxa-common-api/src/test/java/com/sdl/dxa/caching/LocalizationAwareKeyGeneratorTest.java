package com.sdl.dxa.caching;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        WebRequestContextLocalizationIdProvider localizationIdProvider = new WebRequestContextLocalizationIdProvider();
        ReflectionTestUtils.setField(localizationIdProvider, "webRequestContext", webRequestContext);
        ReflectionTestUtils.setField(keyGenerator, "localizationIdProvider", localizationIdProvider);
    }

    @Test
    public void shouldDelegateGeneration_ToSimpleKeyGenerator() {
        //given
        LocalizationAwareCacheKey expected = new LocalizationAwareCacheKey("42", (Serializable) SimpleKeyGenerator.generateKey("a", "b", "c"));

        //when
        LocalizationAwareCacheKey actual = keyGenerator.generate("a", "b", "c");
        LocalizationAwareCacheKey actual2 = keyGenerator.generate(null, null, "a", "b", "c");

        //then
        assertEquals(expected, actual);
        assertEquals(expected, actual2);
    }

    @Test
    public void shouldWrapWithSimpleKey_IfParamIsSingle() {
        //given 

        //when
        LocalizationAwareCacheKey key = keyGenerator.generate("a");

        //then
        assertTrue(key.getKey() instanceof SimpleKey);
    }

}