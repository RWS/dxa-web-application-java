package com.sdl.webapp.common.impl;

import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.RegionBuilderCallback;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.Article;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.impl.model.ViewModelRegistryImpl;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultRegionBuilderTest {

    @Test
    public void shouldIncludeOneEntity() throws Exception {
        //given
        MvcData mvcData = new MvcDataImpl.MvcDataImplBuilder()
                .viewName("ViewName").controllerName("ControllerName").areaName("AreaName")
                .build();

        ViewModelRegistry viewModelRegistry = new ViewModelRegistryImpl();
        ReflectionTestUtils.setField(viewModelRegistry, "semanticMappingRegistry", mock(SemanticMappingRegistry.class));
        viewModelRegistry.registerViewModel(mvcData, RegionModelImpl.class);

        DefaultRegionBuilder builder = new DefaultRegionBuilder();
        ReflectionTestUtils.setField(builder, "viewModelRegistry", viewModelRegistry);

        PageModel pageModel = mock(PageModel.class);
        Localization localization = mock(Localization.class);

        List<Object> sourceList = new ArrayList<>();
        sourceList.add(new Object());
        sourceList.add(new Object());

        final EntityModel expected = getEntity("1");
        final EntityModel unexpected = getEntity("2");

        RegionBuilderCallback callback = mock(RegionBuilderCallback.class);
        when(callback.buildEntity(anyObject(), eq(localization))).thenReturn(expected, unexpected);
        when(callback.getRegionName(anyObject())).thenReturn("Region1");
        when(callback.getRegionMvcData(anyObject())).thenReturn(mvcData);

        ReflectionTestUtils.setField(builder, "conditionalEntityEvaluators", new ArrayList<ConditionalEntityEvaluator>() {{
            ConditionalEntityEvaluator evaluator = mock(ConditionalEntityEvaluator.class);
            when(evaluator.includeEntity(eq(expected))).thenReturn(true);
            when(evaluator.includeEntity(eq(unexpected))).thenReturn(false);
            add(evaluator);
        }});

        //when
        RegionModelSet regions = builder.buildRegions(pageModel, sourceList, callback, localization);

        //then
        RegionModel region = regions.get("Region1");
        assertEquals("Should contain 1 entity out of 2", 1, region.getEntities().size());
        EntityModel result = region.getEntity("1");
        assertEquals("Should include expected entity", expected, result);
    }

    private EntityModel getEntity(String id) {
        EntityModel entityModel = new Article();
        ReflectionTestUtils.setField(entityModel, "id", id);
        return entityModel;
    }

}