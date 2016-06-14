package com.sdl.webapp.common.api.model.region;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.Article;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.common.api.xpm.ComponentType;
import com.sdl.webapp.common.api.xpm.XpmRegion;
import com.sdl.webapp.common.api.xpm.XpmRegionConfig;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class RegionModelImplTest {

    @Test
    public void shouldAddEntity() throws Exception {
        //given
        RegionModelImpl model = new RegionModelImpl("name");
        Article entity = new Article();

        //when
        model.addEntity(entity);

        //then
        assertTrue(model.getEntities().contains(entity));
    }

    @Test
    public void shouldGetEntity() throws Exception {
        //given
        RegionModelImpl model = new RegionModelImpl("name");
        final Article article = new Article();
        article.setId("123");
        model.setEntities(new ArrayList<EntityModel>() {{
            add(article);
        }});

        //when
        EntityModel result = model.getEntity("123");
        EntityModel result2 = model.getEntity("not exists");

        //then
        assertEquals(article, result);
        assertNull(result2);
    }

    @Test
    public void shouldSetNameOutOfRegionName() throws DxaException {
        //given
        MvcDataImpl mvcData = new MvcDataImpl.MvcDataImplBuilder().regionName("region").build();

        //when
        RegionModelImpl region = new RegionModelImpl(mvcData);

        //then
        assertEquals("region", region.getName());
        assertEquals(mvcData, region.getMvcData());
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowToSetXpmMetadataToNull() throws DxaException {
        //given
        RegionModelImpl regionModel = new RegionModelImpl("name");

        //when
        regionModel.setXpmMetadata(null);

        //then
        //NPE
    }

    @Test
    public void shouldReturnXpmMarkup() throws DxaException {
        //given
        RegionModelImpl model = new RegionModelImpl("name");
        Localization localization = mock(Localization.class);


        //when
        String xpmMarkup = model.getXpmMarkup(localization);

        //then
        assertEquals("<!-- Start Region: {title: \"name\", " +
                "allowedComponentTypes: [{schema: \"123\", template: \"234\"}], minOccurs: 0} -->", xpmMarkup);
    }

    @Test
    public void shouldAddExtensionData() throws DxaException {
        //given
        RegionModelImpl regionModel = new RegionModelImpl("name");

        //when
        regionModel.addExtensionData("key", "value");

        //then
        assertEquals("value", regionModel.getExtensionData().get("key"));
    }

    @Test
    public void shouldSetExtensionData() throws DxaException {
        //given
        RegionModelImpl regionModel = new RegionModelImpl("name");
        HashMap<String, Object> extensionData = new HashMap<String, Object>() {{
            put("key", "value");
        }};

        //when
        regionModel.setExtensionData(extensionData);

        //then
        assertEquals("value", regionModel.getExtensionData().get("key"));
    }

    @Profile("test")
    @Configuration
    public static class SpringContext {
        @Bean
        public ApplicationContextHolder applicationContextHolder() {
            return new ApplicationContextHolder();
        }

        @Bean
        public XpmRegionConfig xpmRegionConfig() {
            XpmRegionConfig xpmRegionConfig = mock(XpmRegionConfig.class);
            XpmRegion xpmRegion = mock(XpmRegion.class);
            ComponentType componentType = mock(ComponentType.class);
            when(componentType.getSchemaId()).thenReturn("123");
            when(componentType.getTemplateId()).thenReturn("234");
            when(xpmRegion.getComponentTypes()).thenReturn(Collections.singletonList(componentType));

            when(xpmRegionConfig.getXpmRegion(anyString(), Matchers.<Localization>any())).thenReturn(xpmRegion);
            return xpmRegionConfig;
        }
    }
}