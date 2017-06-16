package com.sdl.dxa.caching;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocalizationAwareKeyGeneratorTest {

    @Mock
    private WebRequestContext webRequestContext;

    @Mock
    private Localization localization;

    @Before
    public void init() {
        when(webRequestContext.getLocalization()).thenReturn(localization);
        when(localization.getId()).thenReturn("42");
    }

    @Test
    public void shouldDelegateGeneration_ToSimpleKeyGenerator() {
        //given
        Object expected = SimpleKeyGenerator.generateKey("42", "a", "b", "c");

        //when
        Object actual = new LocalizationAwareKeyGenerator(webRequestContext).generate("a", "b", "c");
        Object actual2 = new LocalizationAwareKeyGenerator(webRequestContext).generate(null, null, "a", "b", "c");

        //then
        assertEquals(expected, actual);
        assertEquals(expected, actual2);
    }

}