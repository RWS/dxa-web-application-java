package com.sdl.webapp.tridion.mapping;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.content.RegionBuilder;
import com.sdl.webapp.common.api.content.RegionBuilderCallback;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.tridion.fields.FieldConverterRegistry;
import com.sdl.webapp.tridion.fields.converters.ComponentLinkFieldConverter;
import com.sdl.webapp.tridion.fields.converters.DateFieldConverter;
import com.sdl.webapp.tridion.fields.converters.EmbeddedFieldConverter;
import com.sdl.webapp.tridion.fields.converters.ExternalLinkFieldConverter;
import com.sdl.webapp.tridion.fields.converters.KeywordFieldConverter;
import com.sdl.webapp.tridion.fields.converters.MultiLineTextFieldConverter;
import com.sdl.webapp.tridion.fields.converters.MultimediaFieldConverter;
import com.sdl.webapp.tridion.fields.converters.NumberFieldConverter;
import com.sdl.webapp.tridion.fields.converters.TextFieldConverter;
import com.sdl.webapp.tridion.fields.converters.XhtmlFieldConverter;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.contentmodel.Multimedia;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.PageTemplate;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.contentmodel.impl.KeywordImpl;
import org.dd4t.contentmodel.impl.TextField;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Mock
    private LinkResolver linkResolver;

    @Before
    public void init() throws ItemNotFoundException, SerializationException {
        when(localization.getPath()).thenReturn("/");
        when(localization.getCulture()).thenReturn("nl-en");

        when(localization.getResource(eq("core.pageTitleSeparator"))).thenReturn("|");
        when(localization.getResource(eq("core.pageTitlePostfix"))).thenReturn("!");

        when(localization.getConfiguration(eq("core.cmsurl"))).thenReturn("CMS_URL");

        when(webRequestContext.getFullUrl()).thenReturn("full-url");
        when(webRequestContext.getBaseUrl()).thenReturn("/base-url");
        when(webRequestContext.getContextPath()).thenReturn("/path");

        when(linkResolver.resolveLink(anyString(), anyString())).thenReturn("/resolved-link");

        FieldConverterRegistry fieldConverterRegistry = new FieldConverterRegistry(Lists.newArrayList(
                new ComponentLinkFieldConverter(linkResolver, webRequestContext),
                new DateFieldConverter(),
                new EmbeddedFieldConverter(null),
                new ExternalLinkFieldConverter(),
                new KeywordFieldConverter(null, webRequestContext),
                new MultiLineTextFieldConverter(),
                new MultimediaFieldConverter(linkResolver, webRequestContext),
                new NumberFieldConverter(),
                new TextFieldConverter(),
                new XhtmlFieldConverter(new DefaultRichTextProcessor(), webRequestContext)
        ));
        ReflectionTestUtils.setField(pageBuilder, "fieldConverterRegistry", fieldConverterRegistry);
    }

    @Test
    public void shouldMergeAllTopLevelRegionsInCaseOfConflictingRegionsMvcData() throws Exception {
        //given
        MvcData mvcData = creator(new MvcDataImpl.MvcDataImplBuilder()
                .viewName("MyRegionView")
                .build())
                .defaults(DefaultsMvcData.REGION).create();

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
        shouldCreatePageAndSetAllMetadata(
                new TestDataPage().title("001 titleMeta"),
                //page title is postfixed weirdly from localization, ok?
                new TestDataPage().title("titleMeta|!").ogTitle("titleMeta"));
    }

    @Test
    public void shouldCreatePageAndNotRemoveNumbersIfNotSequence() throws ContentProviderException, DxaException {
        shouldCreatePageAndSetAllMetadata(
                new TestDataPage().title("TSI1234 Page Title"),
                new TestDataPage().title("TSI1234 Page Title|!").ogTitle("TSI1234 Page Title"));
    }

    public void shouldCreatePageAndSetAllMetadata(TestDataPage initial, TestDataPage expected) throws ContentProviderException, DxaException {
        //given
        String pageId = "tcm:1-2-3";
        String pageName = "Title";
        String pageView = "Test:TestPage";
        String htmlClasses = "css1, css2 css3";

        String titleMetaSeq = initial.title;
        String descriptionMetaStr = "descriptionMeta";

        DateTime now = DateTime.now();
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


        Field titleMeta = getFieldWithValues("title", FieldType.TEXT, titleMetaSeq);
        Field descriptionMeta = getFieldWithValues("description", FieldType.TEXT, descriptionMetaStr);
        Field emptyField = getFieldWithValues("emptyField", FieldType.TEXT);
        Field metadataDefault = getFieldWithValues("metadata-default", FieldType.TEXT, "metadata-default-1", "metadata-default-2");
        Field internalLink = getFieldWithValues("internalLink", FieldType.COMPONENTLINK, "tcm:componentId");
        Field image = getFieldWithValues("image", FieldType.MULTIMEDIALINK, "tcm:imageId");
        Field multiline = getFieldWithValues("multiline", FieldType.MULTILINETEXT, "Line1\r\n", "Line2");
        Field dateTime = getFieldWithValues("date", FieldType.DATE, now.toString());
        Field number = getFieldWithValues("number", FieldType.NUMBER, "3.123");
        Field unknown = getFieldWithValues("unknown", FieldType.UNKNOWN, "unknown");
        Field embedded = getFieldWithValues("embedded", FieldType.EMBEDDED, getFieldSet(ImmutableMap.of(
                "1", getFieldWithValues("embedded1", FieldType.TEXT, "embedded1"),
                "2", getFieldWithValues("embedded2", FieldType.TEXT, "embedded2-1", "embedded2-2"),
                "3", getFieldWithValues("embedded3", FieldType.DATE, now.toString()))
        ));

        KeywordImpl k1 = new KeywordImpl();
        k1.setDescription("desc1");
        k1.setTitle("title1");
        KeywordImpl k2 = new KeywordImpl();
        k2.setTitle("title2");
        Field keyword = getFieldWithValues("keyword1", FieldType.KEYWORD, k1);
        Field keyword2 = getFieldWithValues("keyword2", FieldType.KEYWORD, k2);
        String html = "<p>richText <a href=\"#\">test</a></p>";
        RichText richTextExample = new RichText(html);
        Field richText = getFieldWithValues("richText", FieldType.XHTML, html);


        Map<String, Field> pageMeta = new HashMap<String, Field>() {{
            put(titleMeta.getName(), titleMeta);
            put(descriptionMeta.getName(), descriptionMeta);
            put(metadataDefault.getName(), metadataDefault);
            put(internalLink.getName(), internalLink);
            put(image.getName(), image);
            put(multiline.getName(), multiline);
            put(dateTime.getName(), dateTime);
            put(number.getName(), number);
            put(embedded.getName(), embedded);
            put(keyword.getName(), keyword);
            put(keyword2.getName(), keyword2);
            put(richText.getName(), richText);
            put(unknown.getName(), unknown);
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
                assertEquals(DefaultsMvcData.PAGE.getControllerAreaName(), mvcData.getControllerAreaName());
                assertEquals(DefaultsMvcData.PAGE.getControllerName(), mvcData.getControllerName());
                assertEquals(DefaultsMvcData.PAGE.getActionName(), mvcData.getActionName());
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Should add all defaults but AreaName & ViewName are custom");
            }
        };
        verify(viewModelRegistry).getViewModelType(argThat(mvcDataMatcher));

        //TSI-2131
        assertEquals("tcm:1-2-3", page.getId());
        // It's confusing, but what DD4T calls the "title" is what is called the "name" in the view model
        assertEquals(pageName, page.getName());
        assertThat("MvcData matches with that used for view resolving", page.getMvcData(), mvcDataMatcher);
        assertEquals("css1 css2 css3", page.getHtmlClasses());

        assertEquals(expected.title, page.getTitle());

        //in this test we don't set regions yet
        assertEquals(new RegionModelSetImpl(), page.getRegions());


        Map<String, String> meta = page.getMeta();

        //region web semantic fields asserts
        assertEquals(expected.ogTitle, meta.get("og:title"));
        assertEquals("summary", meta.get("twitter:card"));
        assertEquals("full-url", meta.get("og:url"));
        assertEquals("article", meta.get("og:type"));
        assertEquals("nl-en", meta.get("og:locale"));
        assertEquals("/base-url/path/resolved-image", meta.get("og:image"));
        assertEquals(descriptionMetaStr, meta.get("og:description"));
        //endregion

        // format of metadata was set by TSI-1308
        assertEquals("metadata-default-1,metadata-default-2", meta.get("metadata-default"));
        assertEquals("/resolved-link", meta.get("internalLink"));
        assertEquals("/resolved-image", meta.get("image"));
        assertEquals("Line1\r\n,Line2", meta.get("multiline"));
        assertEquals(now.toString(), meta.get("date"));
        assertEquals("3.123", meta.get("number"));
        assertEquals("embedded1", meta.get("embedded1"));
        assertEquals("embedded2-1,embedded2-2", meta.get("embedded2"));
        assertEquals("desc1", meta.get("keyword1"));
        assertEquals("title2", meta.get("keyword2"));
        assertEquals(now.toString(), meta.get("embedded3"));
        assertEquals(html, richTextExample.toString());
        assertEquals(html, meta.get("richText"));
        assertFalse(meta.containsKey("embedded"));
        assertFalse(meta.containsKey("emptyField"));
        assertFalse(meta.containsKey("unknown"));


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

        Field templatesField = getFieldWithValues("view", FieldType.TEXT, (Object[]) templates);
        Field htmlClassesField = getFieldWithValues("htmlClasses", FieldType.TEXT, htmlClasses);

        doReturn(new HashMap<String, Field>() {{
            put(templatesField.getName(), templatesField);
            put(htmlClassesField.getName(), htmlClassesField);
        }}).when(pageTemplate).getMetadata();

        when(pageTemplate.getId()).thenReturn(id);
        when(pageTemplate.getRevisionDate()).thenReturn(nowPageTemplate);

        return pageTemplate;
    }

    private Field getFieldWithValues(String name, FieldType fieldType, Object... values) {
        BaseField field = mock(BaseField.class);
        List<String> strings = Stream.of(values).map(Object::toString).collect(Collectors.toList());

        when(field.getValues()).thenReturn(Lists.newArrayList(values));
        when(field.getName()).thenReturn(name);
        when(field.getFieldType()).thenReturn(fieldType);

        if (fieldType == FieldType.TEXT || fieldType == FieldType.MULTILINETEXT || fieldType == FieldType.XHTML
                || fieldType == FieldType.EXTERNALLINK) {
            when(field.getTextValues()).thenReturn(strings);
        }

        if (fieldType == FieldType.EMBEDDED) {
            when(field.getEmbeddedValues()).thenReturn(cast(values, FieldSet.class));
        }
        if (fieldType == FieldType.MULTIMEDIALINK || fieldType == FieldType.COMPONENTLINK) {
            Multimedia multimedia = mock(Multimedia.class);
            when(multimedia.getUrl()).thenReturn("/resolved-" + name);
            Component component = mock(Component.class);
            when(component.getMultimedia()).thenReturn(multimedia);
            List<Component> list = Lists.newArrayList(component);

            when(field.getLinkedComponentValues()).thenReturn(list);
        }

        if (fieldType == FieldType.DATE) {
            when(field.getDateTimeValues()).thenReturn(strings);
        }

        if (fieldType == FieldType.NUMBER) {
            when(field.getNumericValues())
                    .thenReturn(Stream.of(values)
                            .map(o -> Double.parseDouble(o.toString()))
                            .collect(Collectors.toList()));
        }

        if (fieldType == FieldType.KEYWORD) {
            when(field.getKeywordValues()).thenReturn(cast(values, Keyword.class));
        }
        return field;
    }

    private <T> List<T> cast(Object[] values, Class<T> aClass) {
        return Stream.of(values).map(aClass::cast).collect(Collectors.toList());
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

    @Data
    @Accessors(fluent = true)
    protected static class TestDataPage {

        String title;

        String ogTitle;

    }


}