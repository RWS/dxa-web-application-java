package com.sdl.webapp.tridion.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.DynamicList;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.query.ComponentMetadata;
import com.sdl.webapp.common.api.model.query.ComponentMetadata.MetaEntry;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.ImageUtils;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.DateField;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.NumericField;
import org.dd4t.contentmodel.impl.SchemaImpl;
import org.dd4t.contentmodel.impl.TextField;
import org.dd4t.core.factories.ComponentPresentationFactory;
import org.dd4t.core.factories.PageFactory;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sdl.webapp.common.util.TcmUtils.buildTcmUri;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class DefaultContentProviderTest {

    @Autowired
    private DefaultContentProvider defaultContentProvider;

    @Autowired
    private ModelBuilderPipeline modelBuilderPipeline;

    @Test
    public void shouldInjectIsQueryBasedParam() throws DxaException, ContentProviderException {
        //given
        EntityModel entity = mock(EntityModel.class);
        when(entity.getXpmMetadata()).thenReturn(new HashMap<String, Object>());
        when(modelBuilderPipeline.createEntityModel(Matchers.<ComponentPresentation>any(), Matchers.any()))
                .thenReturn(entity);

        //when
        EntityModel entityModel = defaultContentProvider.getEntityModel("1-1", null);

        //then
        assertEquals(entityModel.getXpmMetadata().get("IsQueryBased"), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfIdIsWrong() throws DxaException, ContentProviderException {
        //when
        defaultContentProvider.getEntityModel("1", null);

        //then
        //exception is thrown
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfIdIsEmpty() throws DxaException, ContentProviderException {
        //when
        defaultContentProvider.getEntityModel("", null);

        //then
        //exception is thrown
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionIfIdIsNull() throws DxaException, ContentProviderException {
        //when
        defaultContentProvider.getEntityModel(null, null);

        //then
        //exception is thrown
    }

    @Test
    public void shouldCreateAllFoldersForNonExisting() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(false);

        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(true);

        //when
        //file.exists = false, parent.exists = true
        boolean notExist = AbstractDefaultContentProvider.isToBeRefreshed(file, 1000L);

        //then
        assertTrue(notExist);
    }

    @Test
    public void shouldSayFalseForFreshFile() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.lastModified()).thenReturn(1000L);
        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(false);
        when(parent.mkdirs()).thenReturn(true);

        //when
        //file.exists = false, parent.exists = false, parent.mkdirs = true
        boolean newFile = AbstractDefaultContentProvider.isToBeRefreshed(file, 500L);

        //then
        assertFalse(newFile);
    }

    @Test(expected = ContentProviderException.class)
    public void shouldFailForProblemsWithDirs() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(false);
        when(file.lastModified()).thenReturn(1000L);
        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(false);
        when(parent.mkdirs()).thenReturn(false);

        //when
        //file.exists = false, parent.exists = false, parent.mkdirs = false
        AbstractDefaultContentProvider.isToBeRefreshed(file, 500L);
        //then
        //exception
    }

    @Test
    public void shouldFindTheOldFileForRefreshFolderAction() throws ContentProviderException {
        //given
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.lastModified()).thenReturn(1000L);
        File parent = mock(File.class);
        when(file.getParentFile()).thenReturn(parent);
        when(parent.exists()).thenReturn(true);

        //when
        //file.exists = true, parent.exists = true
        boolean oldFile = AbstractDefaultContentProvider.isToBeRefreshed(file, 1500L);

        //then
        verify(parent, never()).mkdirs();
        assertTrue(oldFile);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetQueryFromDynamicListAndCallExecuteImplementation() throws ContentProviderException {
        //given
        SimpleBrokerQuery query = new SimpleBrokerQuery();
        AbstractDefaultContentProvider spy = spy(defaultContentProvider);

        final Date dateCreated_m = new Date();
        final ComponentMetadata componentMetadata = ComponentMetadata.builder()
                .id("1")
                .publicationId("2")
                .lastPublicationDate(new Date())
                .modificationDate(new Date())
                .title("3")
                .schemaId("4")
                .custom(ImmutableMap.<String, MetaEntry>builder()
                        .put("dateCreated_m", MetaEntry.builder()
                                .value(new Timestamp(dateCreated_m.getTime())).metaType(ComponentMetadata.MetaType.DATE).build())
                        .put("name_m", MetaEntry.builder()
                                .value("name_m").metaType(ComponentMetadata.MetaType.STRING).build())
                        .put("float_m", MetaEntry.builder()
                                .value(12.0).metaType(ComponentMetadata.MetaType.FLOAT).build())
                        .build())
                .build();

        when(spy.executeMetadataQuery(eq(query))).thenReturn(new ArrayList<ComponentMetadata>() {{
            add(componentMetadata);
            add(componentMetadata);
        }});
        DynamicList dynamicList = mock(DynamicList.class);
        when(dynamicList.getQuery(any(Localization.class))).thenReturn(query);
        when(dynamicList.getEntityType()).thenReturn(Link.class);
        when(modelBuilderPipeline.createEntityModel(argThat(new BaseMatcher<Component>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Component should match metadata");
            }

            @Override
            public boolean matches(Object item) {
                Component component = (Component) item;
                String dateTimeStringFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
                Map<String, Field> standardMeta = ((EmbeddedField) component.getMetadata().get("standardMeta")).getEmbeddedValues().get(0).getContent();
                assertEquals(component.getId(), buildTcmUri(componentMetadata.getPublicationId(), componentMetadata.getId()));
                assertEquals(component.getPublication().getId(), componentMetadata.getPublicationId());
                assertEquals(component.getLastPublishedDate(), new DateTime(componentMetadata.getLastPublicationDate()));
                assertEquals(component.getRevisionDate(), new DateTime(componentMetadata.getModificationDate()));
                assertEquals(component.getTitle(), componentMetadata.getTitle());
                assertEquals(component.getSchema().getId(), buildTcmUri(componentMetadata.getPublicationId(), componentMetadata.getSchemaId()));
                assertEquals(((SchemaImpl) component.getSchema()).getPublication().getId(), componentMetadata.getPublicationId());

                assertEquals(standardMeta.get("dateCreated").getFieldType(), FieldType.DATE);
                assertEquals(((DateField) standardMeta.get("dateCreated")).getDateTimeValues().get(0),
                        new DateTime(componentMetadata.getLastPublicationDate()).toString(dateTimeStringFormat));

                assertEquals(standardMeta.get("name").getFieldType(), FieldType.TEXT);
                assertEquals(((TextField) standardMeta.get("name")).getTextValues().get(0), componentMetadata.getTitle());

                assertEquals(standardMeta.get("name_m").getFieldType(), FieldType.TEXT);
                assertEquals(((TextField) standardMeta.get("name_m")).getTextValues().get(0), "name_m");

                assertEquals(standardMeta.get("dateCreated_m").getFieldType(), FieldType.DATE);
                assertEquals(((DateField) standardMeta.get("dateCreated_m")).getDateTimeValues().get(0), new DateTime(dateCreated_m).toString(dateTimeStringFormat));

                assertEquals(standardMeta.get("float_m").getFieldType(), FieldType.NUMBER);
                assertEquals(((NumericField) standardMeta.get("float_m")).getNumericValues().get(0), 12.0, 0.0);

                return true;
            }
        }), any(Localization.class), eq(Link.class))).thenReturn(new Link());
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                List<Component> list = invocation.getArgumentAt(0, List.class);
                assertTrue(list.size() == 2);

                assertEquals(list.get(0), list.get(1));
                return true;
            }
        }).when(dynamicList).setQueryResults(anyList(), anyBoolean());


        //when
        spy.populateDynamicList(dynamicList, mock(Localization.class));

        //then
        verify(dynamicList).getQuery(any(Localization.class));
        verify(spy).executeMetadataQuery(eq(query));
        verify(dynamicList).getEntityType();
        verify(dynamicList).setQueryResults(anyList(), anyBoolean());
    }

    @Configuration
    @Profile("test")
    static class SpringContext {

        @Bean
        public DefaultContentProvider defaultProvider() {
            return new DefaultContentProvider(webRequestContext(), linkResolver(), webApplicationContext(),
                    dynamicMetaRetriever(), binaryContentRetriever(),
                    dd4tPageFactory(), componentPresentationFactory(), modelBuilderPipeline()) {
                @Override
                protected StaticContentFile getStaticContentFile(File file, ImageUtils.StaticContentPathInfo pathInfo, int publicationId) throws ContentProviderException, IOException {
                    return mock(AbstractDefaultContentProvider.StaticContentFile.class);
                }

                @Override
                protected List<ComponentMetadata> executeMetadataQuery(SimpleBrokerQuery query) {
                    return Collections.emptyList();
                }
            };
        }

        @Bean
        public WebRequestContext webRequestContext() {
            WebRequestContext mock = mock(WebRequestContext.class);
            when(mock.getLocalization()).thenReturn(mock(Localization.class));
            return mock;
        }

        @Bean
        public LinkResolver linkResolver() {
            return mock(LinkResolver.class);
        }

        @Bean
        public WebApplicationContext webApplicationContext() {
            return mock(WebApplicationContext.class);
        }

        @Bean
        public DynamicMetaRetriever dynamicMetaRetriever() {
            return mock(DynamicMetaRetriever.class);
        }

        @Bean
        public BinaryContentRetriever binaryContentRetriever() {
            return mock(BinaryContentRetriever.class);
        }

        @Bean
        public PageFactory dd4tPageFactory() {
            return mock(PageFactory.class);
        }

        @Bean
        public ComponentPresentationFactory componentPresentationFactory() {
            return mock(ComponentPresentationFactory.class);
        }

        @Bean
        public ModelBuilderPipeline modelBuilderPipeline() {
            return mock(ModelBuilderPipeline.class);
        }

        @Bean
        public ObjectMapper objectMapper() {
            return mock(ObjectMapper.class);
        }
    }
}