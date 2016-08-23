package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.dto.FeedItem;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.page.PageModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.InitializationUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FeedFormatterTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldReturnNullForNullModel() {
        //given
        TestFeedFormatter formatter = new TestFeedFormatter(null, null);

        //when
        Object formatData = formatter.formatData(null);

        //then
        assertNull(formatData);
    }

    @Test
    public void shouldReturnEmptyListForEmptyRegions() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(getRegionModel("reg1"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnListOfItemsForFilledPageModel() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(
                new RegionModelImpl[]{
                        getRegionModel("reg1", getTestEntity("1"), getTestEntity("2")),
                        getRegionModel("reg2", getTestEntity("3"), getTestEntity("4"))
                },
                new Entry("1"), new Entry("2"), new Entry("3"), new Entry("4")
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnListOfItemsIfEntitiesContainsListsOfEntitiesInside() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(
                getRegionModel("reg1", getTestEntity("1", getTestEntity("2"), getTestEntity("3"))),
                new Entry("1"), new Entry("2"), new Entry("3")
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotAddEmptyEntities() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(getRegionModel("reg1", new EmptyTestEntity()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportTestEntity1FieldNames() throws DxaException {
        Link link = new Link();
        link.setUrl("url");
        Date date = new Date();
        shouldGetGivenEntriesFromRegionModel(
                getRegionModel("reg1", new TestEntity("1", "2", link.getUrl(), date, Collections.<TestEntity>emptyList())),
                new Entry("1", link, new RichText("2"), date)
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportTestEntity2FieldNames() throws DxaException {
        Link link = new Link();
        link.setUrl("url");
        DateTime dateTime = DateTime.now();
        shouldGetGivenEntriesFromRegionModel(
                getRegionModel("reg1", new TestEntity2("1", "2", link, dateTime)),
                new Entry("1", link, new RichText("2"), dateTime.toDate())
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportTestEntity3FieldNames() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(
                getRegionModel("reg1", new TestEntity3("1", "2", "date")),
                new Entry("1", null, new RichText("2"), null)
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportTestEntity4FieldNames() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(
                getRegionModel("reg1", new TestEntity4("1", "2")),
                new Entry("1", null, new RichText("2"), null)
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportTestEntity5FieldNames() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(
                getRegionModel("reg1", new TestEntity5("2")),
                new Entry(null, null, new RichText("2"), null)
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportTestEntity6FieldNames() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(
                getRegionModel("reg1", new TestEntity6(new RichText("2"))),
                new Entry(null, null, new RichText("2"), null)
        );
    }

    @Test
    public void shouldSupportTestEntity7FieldNames() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(
                getRegionModel("reg1", new TestEntity7("2")),
                new Entry(null, null, new RichText("2"), null)
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionIfModelIsNotPage() {
        //given
        FeedFormatter feedFormatter = new TestFeedFormatter(null, null);

        //when
        feedFormatter.formatData(new Object());

        //then
        //exception
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnEmptyListInCaseOfInnerException() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(
                new ExceptionTestFeedFormatter(null, null),
                new RegionModelImpl[]{getRegionModel("reg1", new TestEntity4("1", "2"))}
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldHandleWhenCollectionIsNull() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(
                getRegionModel("reg1", new TestEntity("1", null, null, null, null)),
                new Entry("1")
        );
    }

    @Test
    public void shouldContainFullMappingForFieldsInRealCode() throws IOException {
        //given
        Properties mainProps = new Properties();
        mainProps.load(FileUtils.openInputStream(new File("./src/main/resources/dxa.defaults.properties")));

        Properties testProps = InitializationUtils.loadDxaProperties();

        //then
        sameValuesForKeys(mainProps, testProps,
                "dxa.api.formatters.mapping.Headline",
                "dxa.api.formatters.mapping.Summary",
                "dxa.api.formatters.mapping.Date",
                "dxa.api.formatters.mapping.Link");
    }

    private void sameValuesForKeys(Properties checked, Properties expectedOk, String... keys) {
        assertTrue(keys.length > 0);
        for (String key : keys) {
            assertTrue(
                    extractProperties(key, checked).containsAll(
                            extractProperties(key, expectedOk)
                    ));
        }
    }

    private List<String> extractProperties(String key, Properties expected) {
        List<String> list = new ArrayList<>();
        for (String s : expected.get(key).toString().split(",")) {
            list.add(s.trim());
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private void shouldGetGivenEntriesFromRegionModel(FeedFormatter formatter, RegionModelImpl[] regionModel, Entry... contains) throws DxaException {
        //given
        RegionModelSetImpl regionModels = new RegionModelSetImpl();
        Collections.addAll(regionModels, regionModel);

        //when
        Object formatData = formatter.formatData(getPageModel(regionModels));

        //then
        assertTrue(formatData instanceof List);
        List<Entry> data = (List<Entry>) formatData;

        if (contains.length == 0) {
            assertThat(data, empty());
        } else {
            assertThat(data, contains(contains));
        }
    }

    @SuppressWarnings("unchecked")
    private void shouldGetGivenEntriesFromRegionModel(RegionModelImpl[] regionModel, Entry... contains) throws DxaException {
        shouldGetGivenEntriesFromRegionModel(new TestFeedFormatter(null, null), regionModel, contains);
    }

    private void shouldGetGivenEntriesFromRegionModel(RegionModelImpl regionModel, Entry... contains) throws DxaException {
        shouldGetGivenEntriesFromRegionModel(Collections.singletonList(regionModel).toArray(new RegionModelImpl[1]), contains);
    }

    @NotNull
    private RegionModelImpl getRegionModel(String name, EntityModel... entityModels) throws DxaException {
        RegionModelImpl region = new RegionModelImpl(name);
        for (EntityModel entityModel : entityModels) {
            region.addEntity(entityModel);
        }
        return region;
    }

    private @NotNull TestEntity getTestEntity(String id, TestEntity... testEntities) {
        return new TestEntity(id, null, null, null, Arrays.asList(testEntities));
    }

    @NotNull
    private PageModelImpl getPageModel(RegionModelSet regionModels) {
        PageModelImpl pageModel = new PageModelImpl();
        pageModel.setRegions(regionModels);
        return pageModel;
    }

    private static class TestFeedFormatter extends FeedFormatter {

        TestFeedFormatter(HttpServletRequest request, WebRequestContext context) {
            super(request, context);
        }

        @Override
        public Object getSyndicationItem(FeedItem item) throws Exception {
            return new Entry(item.getHeadline(), item.getLink(), item.getSummary(), item.getDate());
        }
    }

    private static class ExceptionTestFeedFormatter extends FeedFormatter {

        ExceptionTestFeedFormatter(HttpServletRequest request, WebRequestContext context) {
            super(request, context);
        }

        @Override
        public Object getSyndicationItem(FeedItem item) throws Exception {
            throw new RuntimeException();
        }
    }

    @Data
    @AllArgsConstructor
    private static class Entry {

        private String headline;

        private Link link;

        private RichText summary;

        private Date date;

        Entry(String headline) {
            this.headline = headline;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    private static class TestEntity extends AbstractEntityModel {

        private String headline;

        private String summary;

        private String url;

        private Date updated;

        private List<TestEntity> testEntities;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    private static class TestEntity2 extends AbstractEntityModel {

        private String title;

        private String description;

        private Link link;

        private DateTime date;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    private static class TestEntity3 extends AbstractEntityModel {

        private String name;

        private String snippet;

        /**
         * Whatever it is, don't treat as date.
         */
        private String date;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    private static class TestEntity4 extends AbstractEntityModel {

        private String linkText;

        private String teaser;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    private static class TestEntity5 extends AbstractEntityModel {

        private String text;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    private static class TestEntity6 extends AbstractEntityModel {

        private RichText text;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    private static class TestEntity7 extends AbstractEntityModel {

        private String alternateText;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class EmptyTestEntity extends AbstractEntityModel {

    }

}