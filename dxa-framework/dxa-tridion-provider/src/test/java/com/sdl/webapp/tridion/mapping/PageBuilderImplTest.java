package com.sdl.webapp.tridion.mapping;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.RegionBuilder;
import com.sdl.webapp.common.api.content.RegionBuilderCallback;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.PageTemplate;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.contentmodel.impl.TextField;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator.creator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PageBuilderImplTest {

    @InjectMocks
    private PageBuilderImpl pageBuilder;

    @Mock
    private ViewModelRegistry viewModelRegistry;

    @Mock
    private RegionBuilder regionBuilder;

    @Mock
    private Localization localization;

    @Mock
    private WebRequestContext webRequestContext;


    @Before
    public void init() {
        when(localization.getPath()).thenReturn("/");
        when(localization.getCulture()).thenReturn("nl-en");

        when(localization.getResource(eq("core.pageTitleSeparator"))).thenReturn("|");
        when(localization.getResource(eq("core.pageTitlePostfix"))).thenReturn("!");

        when(localization.getConfiguration(eq("core.cmsurl"))).thenReturn("CMS_URL");

        when(webRequestContext.getFullUrl()).thenReturn("full-url");
    }

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
        TestEntity testEntity = new TestEntity();
        cpRegion.addEntity(testEntity);
        cpRegions.add(cpRegion);

        //when
        RegionModelSet result = PageBuilderImpl.mergeAllTopLevelRegions(predefinedRegions, cpRegions);

        //then
        assertSame(testEntity, result.get("MyRegion").getEntities().get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPreferFirstPredefinedRegionsIfThereAreTwoConflicting() throws Exception {
        //given
        when(viewModelRegistry.getViewModelType(Matchers.any(MvcData.class)))
                .thenReturn((Class) RegionModelImpl.class);

        PageTemplate pageTemplate = mock(PageTemplate.class);
        Map<String, Field> meta = new HashMap<>();
        when(pageTemplate.getMetadata()).thenReturn(meta);

        BaseField regionsMetaField = mock(BaseField.class);
        meta.put("regions", regionsMetaField);

        FieldSet fieldSet1 = getFieldSet(ImmutableMap.of(
                "view", getTextField("2-Column"),
                "name", getTextField("MyRegion")));
        FieldSet fieldSet2 = getFieldSet(ImmutableMap.of(
                "view", getTextField("3-Column"),
                "name", getTextField("MyRegion")));
        when(regionsMetaField.getEmbeddedValues()).thenReturn(Arrays.asList(fieldSet1, fieldSet2));

        //when
        RegionModelSet result = pageBuilder.createPredefinedRegions(pageTemplate);

        //then
        assertEquals("2-Column", result.get("MyRegion").getMvcData().getViewName());
    }

    @Test
    public void shouldCreatePageAndSetAllMetadata() throws ContentProviderException, DxaException {
        //given
        String pageId = "tcm:1-2-3";
        String pageName = "Title";
        String pageView = "Test:TestPage";
        String htmlClasses = "css1, css2 css3";

        String titleMetaSeq = "001 titleMeta";
        String descriptionMetaStr = "descriptionMeta";

        DateTime nowPage = DateTime.now().minus(1024);
        DateTime nowPageTemplate = DateTime.now().minus(2048);
        String nowPrinted = ISODateTimeFormat.dateHourMinuteSecond().print(nowPage);
        String nowPageTemplatePrinted = ISODateTimeFormat.dateHourMinuteSecond().print(nowPageTemplate);

        String pageTemplateId = "tcm:1-10-20";

        //noinspection unchecked
        when(viewModelRegistry.getViewModelType(Matchers.any(MvcData.class)))
                .thenReturn((Class) DefaultPageModel.class);

        when(regionBuilder.buildRegions(any(PageModel.class), anyList(), any(RegionBuilderCallback.class), eq(localization)))
                .thenReturn(new RegionModelSetImpl());

        PageTemplate pageTemplate = getPageTemplate(pageTemplateId, htmlClasses, new String[]{pageView}, nowPageTemplate);

        Page genericPage = mock(Page.class);
        doReturn(pageTemplate).when(genericPage).getPageTemplate();

        Field titleMeta = getFieldWithStringValues("title", new String[]{titleMetaSeq});
        Field descriptionMeta = getFieldWithStringValues("description", new String[]{descriptionMetaStr});
        Field someField = getFieldWithStringValues("someField", new String[]{"someValue", "someValue2" });
        Field emptyField = getFieldWithStringValues("emptyField", new String[]{});
        Map<String, Field> pageMeta = new HashMap<String, Field>() {{
            put(titleMeta.getName(), titleMeta);
            put(descriptionMeta.getName(), descriptionMeta);
            put(someField.getName(), someField);
            put(emptyField.getName(), emptyField);
            // image empty
        }};
        when(genericPage.getMetadata()).thenReturn(pageMeta);

        when(genericPage.getId()).thenReturn(pageId);
        when(genericPage.getTitle()).thenReturn(pageName);
        when(genericPage.getRevisionDate()).thenReturn(nowPage);

        //no includes for this test yet
        when(localization.getIncludes(anyString())).thenReturn(Collections.emptyList());


        //when
        PageModel page = pageBuilder.createPage(genericPage, null, localization, mock(ContentProvider.class));


        //then
        BaseMatcher<MvcData> mvcDataMatcher = new BaseMatcher<MvcData>() {
            @Override
            public boolean matches(Object item) {
                MvcData mvcData = (MvcData) item;
                assertEquals("Test", mvcData.getAreaName());
                assertEquals("TestPage", mvcData.getViewName());
                assertEquals(DefaultsMvcData.CORE_PAGE.getControllerAreaName(), mvcData.getControllerAreaName());
                assertEquals(DefaultsMvcData.CORE_PAGE.getControllerName(), mvcData.getControllerName());
                assertEquals(DefaultsMvcData.CORE_PAGE.getActionName(), mvcData.getActionName());
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Should add all defaults but AreaName & ViewName are custom");
            }
        };
        verify(viewModelRegistry).getViewModelType(argThat(mvcDataMatcher));

        //TSI-2131
        assertEquals("2", page.getId());
        // It's confusing, but what DD4T calls the "title" is what is called the "name" in the view model
        assertEquals(pageName, page.getName());
        assertThat("MvcData matches with that used for view resolving", page.getMvcData(), mvcDataMatcher);
        assertEquals("css1 css2 css3", page.getHtmlClasses());

        //in this test we don't set regions yet
        assertEquals(new RegionModelSetImpl(), page.getRegions());

        Map<String, String> meta = page.getMeta();

        //region web semantic fields asserts
        assertEquals("titleMeta", meta.get("og:title"));
        assertEquals("summary", meta.get("twitter:card"));
        assertEquals("full-url", meta.get("og:url"));
        assertEquals("article", meta.get("og:type"));
        assertEquals("nl-en", meta.get("og:locale"));
        assertEquals(descriptionMetaStr, meta.get("og:description"));
        //image meta field was not set
        assertFalse(meta.containsKey("og:image"));
        //endregion

        assertEquals("someValue,someValue2", meta.get("someField"));
        assertFalse(meta.containsKey("emptyField"));

        //page title is postfixed weirdly from localization, ok?
        assertEquals("titleMeta|!", page.getTitle());

        //region XPM Metadata asserts
        Map<String, Object> xpmMetadata = page.getXpmMetadata();
        assertEquals(pageId, xpmMetadata.get("PageID"));
        assertEquals(nowPrinted, xpmMetadata.get("PageModified"));
        assertEquals(nowPageTemplatePrinted, xpmMetadata.get("PageTemplateModified"));
        assertEquals(pageTemplateId, xpmMetadata.get("PageTemplateID"));
        assertEquals("CMS_URL", xpmMetadata.get("CmsUrl"));
        //endregion
    }

    private PageTemplate getPageTemplate(String id, String htmlClasses, String[] templates, DateTime nowPageTemplate) {
        PageTemplate pageTemplate = mock(PageTemplate.class);

        Field templatesField = getFieldWithStringValues("view", templates);
        Field htmlClassesField = getFieldWithStringValues("htmlClasses", new String[]{htmlClasses});

        doReturn(new HashMap<String, Field>() {{
            put(templatesField.getName(), templatesField);
            put(htmlClassesField.getName(), htmlClassesField);
        }}).when(pageTemplate).getMetadata();

        when(pageTemplate.getId()).thenReturn(id);
        when(pageTemplate.getRevisionDate()).thenReturn(nowPageTemplate);

        return pageTemplate;
    }

    private Field getFieldWithStringValues(String name, String[] values) {
        BaseField field = mock(BaseField.class);
        when(field.getTextValues()).thenReturn(Lists.newArrayList(values));
        when(field.getValues()).thenReturn(Lists.newArrayList(values));
        when(field.getName()).thenReturn(name);
        return field;
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

    private static class TestEntity extends AbstractEntityModel {

    }
}