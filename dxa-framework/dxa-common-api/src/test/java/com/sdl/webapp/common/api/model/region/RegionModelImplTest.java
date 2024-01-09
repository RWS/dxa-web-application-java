package com.sdl.webapp.common.api.model.region;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.TestEntity;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.common.api.xpm.ComponentType;
import com.sdl.webapp.common.api.xpm.XpmRegion;
import com.sdl.webapp.common.api.xpm.XpmRegionConfig;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import org.hamcrest.collection.IsIterableContainingInOrder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.sdl.webapp.common.api.model.TestEntity.entity;
import static com.sdl.webapp.common.api.model.TestEntity.feedItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RegionModelImplTest.RegionModelImplTestContextConfiguration.class)
public class RegionModelImplTest {

    @Test
    public void shouldAddEntity() throws Exception {
        //given
        RegionModelImpl model = new RegionModelImpl("name");
        TestEntity entity = mock(TestEntity.class);

        //when
        model.addEntity(entity);

        //then
        assertTrue(model.getEntities().contains(entity));
    }

    @Test
    public void shouldGetEntity() throws Exception {
        //given
        RegionModelImpl model = new RegionModelImpl("name");
        final TestEntity testEntity = mock(TestEntity.class);
        lenient().doCallRealMethod().when(testEntity).setId(anyString());
        lenient().doCallRealMethod().when(testEntity).getId();
        testEntity.setId("123");
        model.setEntities(new ArrayList<EntityModel>() {{
            add(testEntity);
        }});

        //when
        EntityModel result = model.getEntity("123");
        EntityModel result2 = model.getEntity("not exists");

        //then
        assertEquals(testEntity, result);
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

    @Test
    public void shouldNotAllowToSetXpmMetadataToNull() throws DxaException {
        Assertions.assertThrows(NullPointerException.class, () -> {
            // Given
            RegionModelImpl regionModel = new RegionModelImpl("name");

            // When
            regionModel.setXpmMetadata(null);

            // Then NPE
        });
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

    @Test
    public void shouldCollectionRegionsAndEntitiesAsFeedItems() throws DxaException {
        //given
        FeedItem feedItem1 = feedItem("1");
        FeedItem feedItem2 = feedItem("2");
        FeedItem feedItem3 = feedItem("3");

        RegionModelImpl regionModel = new RegionModelImpl("name");
        RegionModelSetImpl subRegions = new RegionModelSetImpl();
        RegionModelImpl subRegion = new RegionModelImpl("name2");
        subRegions.add(subRegion);
        regionModel.setRegions(subRegions);
        subRegion.addEntity(entity(feedItem1));

        regionModel.setEntities(Lists.newArrayList(entity(feedItem2), entity(feedItem3), new TestEntity.TestEntityNoFeed()));

        //when
        List<FeedItem> feedItems = regionModel.extractFeedItems();

        //then
        assertThat(feedItems, IsIterableContainingInOrder.contains(feedItem1, feedItem2, feedItem3));
    }

    @Test
    public void shouldInitRegionModelSet() throws DxaException {
        //given 
        RegionModelImpl regionModel = new RegionModelImpl("name");

        //when
        RegionModelSet regions = regionModel.getRegions();

        //then
        assertNotNull(regions);

        //when
        regionModel.setRegions(null);
        regions = regionModel.getRegions();

        //then
        assertNull(regions);
    }

    @Test
    public void shouldTrigger_AllRegionsAndEntities_ToFilterEntities() throws DxaException {
        //given 
        RegionModelImpl region = new RegionModelImpl("name");

        EntityModel entity1 = mock(EntityModel.class);
        region.addEntity(entity1);

        EntityModel entity2 = mock(EntityModel.class);
        region.addEntity(entity2);

        RegionModelSetImpl regions = new RegionModelSetImpl();
        region.setRegions(regions);
        RegionModelImpl regionModel1 = spy(new RegionModelImpl("1"));
        regions.add(regionModel1);
        RegionModelImpl regionModel2 = spy(new RegionModelImpl("2"));
        regions.add(regionModel2);

        ConditionalEntityEvaluator evaluator = mock(ConditionalEntityEvaluator.class);
        List<ConditionalEntityEvaluator> evaluators = Collections.singletonList(evaluator);

        lenient().when(evaluator.includeEntity(same(entity1))).thenReturn(true);
        lenient().when(evaluator.includeEntity(same(entity2))).thenReturn(false);

        //when
        assertEquals(2, region.getEntities().size());
        region.filterConditionalEntities(evaluators);

        //then
        verify(regionModel1).filterConditionalEntities(eq(evaluators));
        verify(regionModel2).filterConditionalEntities(eq(evaluators));
        assertEquals(1, region.getEntities().size());
    }

    @Profile("test")
    @Configuration
    public static class RegionModelImplTestContextConfiguration {

        @Bean
        public ApplicationContextHolder applicationContextHolder() {
            return new ApplicationContextHolder();
        }

        @Bean
        public XpmRegionConfig xpmRegionConfig() {
            XpmRegionConfig xpmRegionConfig = mock(XpmRegionConfig.class);
            XpmRegion xpmRegion = mock(XpmRegion.class);
            ComponentType componentType = mock(ComponentType.class);
            lenient().when(componentType.getSchemaId()).thenReturn("123");
            lenient().when(componentType.getTemplateId()).thenReturn("234");
            lenient().when(xpmRegion.getComponentTypes()).thenReturn(Collections.singletonList(componentType));

            lenient().when(xpmRegionConfig.getXpmRegion(anyString(), any())).thenReturn(xpmRegion);
            return xpmRegionConfig;
        }
    }
}