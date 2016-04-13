package com.sdl.webapp.tridion.mapping;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.Article;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.PageTemplate;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.contentmodel.impl.TextField;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator.creator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageBuilderImplTest {

    @Test
    public void shouldMergeAllTopLevelRegionsInCaseOfConflictingRegionsMvcData() throws Exception {
        //given
        MvcData mvcData = creator(new MvcDataImpl.MvcDataImplBuilder()
                .viewName("MyRegionView")
                .build())
                .defaults(DefaultsMvcData.CORE_REGION).create();

        RegionModelSetImpl predefinedRegions = new RegionModelSetImpl();
        predefinedRegions.add(new RegionModelImpl(creator(mvcData).builder().regionName("MyRegion").build()));

        RegionModelSetImpl cpRegions = new RegionModelSetImpl();
        RegionModelImpl cpRegion = new RegionModelImpl("MyRegion");
        cpRegion.setMvcData(mvcData);
        Article article = new Article();
        cpRegion.addEntity(article);
        cpRegions.add(cpRegion);

        //when
        RegionModelSet result = PageBuilderImpl.mergeAllTopLevelRegions(predefinedRegions, cpRegions);

        //then
        assertSame(article, result.get("MyRegion").getEntities().get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPreferFirstPredefinedRegionsIfThereAreTwoConflicting() throws Exception {
        //given
        PageBuilderImpl pageBuilder = new PageBuilderImpl();
        ViewModelRegistry viewModelRegistry = mock(ViewModelRegistry.class);
        when(viewModelRegistry.getViewModelType(Matchers.any(MvcData.class))).thenReturn((Class) RegionModelImpl.class);
        ReflectionTestUtils.setField(pageBuilder, "viewModelRegistry", viewModelRegistry);

        PageTemplate pageTemplate = mock(PageTemplate.class);
        Map<String, Field> meta = new HashMap<>();
        when(pageTemplate.getMetadata()).thenReturn(meta);

        BaseField regionsMetaField = mock(BaseField.class);
        meta.put("regions", regionsMetaField);

        FieldSet fieldSet1 = getFieldSet(ImmutableMap.<String, Field>of(
                "view", getTextField("2-Column"),
                "name", getTextField("MyRegion")));
        FieldSet fieldSet2 = getFieldSet(ImmutableMap.<String, Field>of(
                "view", getTextField("3-Column"),
                "name", getTextField("MyRegion")));
        when(regionsMetaField.getEmbeddedValues()).thenReturn(Arrays.asList(fieldSet1, fieldSet2));

        //when
        RegionModelSet result = pageBuilder.createPredefinedRegions(pageTemplate);

        //then
        assertEquals("2-Column", result.get("MyRegion").getMvcData().getViewName());
    }

    private FieldSet getFieldSet(Map<String, Field> map) {
        FieldSet fieldSet1 = mock(FieldSet.class);
        when(fieldSet1.getContent()).thenReturn(map);
        return fieldSet1;
    }

    private TextField getTextField(String textValue) {
        TextField tf1 = new TextField();
        tf1.setTextValues(Collections.singletonList(textValue));
        return tf1;
    }
}