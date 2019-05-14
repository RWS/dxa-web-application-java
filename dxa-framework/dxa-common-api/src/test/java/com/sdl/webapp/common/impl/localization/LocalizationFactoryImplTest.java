package com.sdl.webapp.common.impl.localization;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationFactory;
import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.impl.localization.LocalizationImpl.Builder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = LocalizationFactoryImplTest.SpringConfigurationContext.class)
public class LocalizationFactoryImplTest {

    @Autowired
    LocalizationFactory localizationFactory;

    @Before
    public void init() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    }

    @Test
    public void shouldAddDocsTopicSchema() throws LocalizationFactoryException {
        Localization localization = localizationFactory.createLocalization("8", "/");
        assertEquals(localization.getId(), "8");
    }

    private Builder getLocalizationBuilder() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Builder> declaredConstructor = Builder.class.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        Builder instance = declaredConstructor.newInstance();
        instance.setMediaRoot("/");

        return instance;
    }

    @Configuration
    public static class SpringConfigurationContext {
        @Bean
        public LocalizationFactory localizationFactory() {
            return new LocalizationFactoryImpl();
        }

        @Bean
        public ContentProvider contentProvider() {
            return Mockito.mock(GraphQLContentProvider.class);
        }
    }
}