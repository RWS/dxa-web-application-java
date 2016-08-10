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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FeedFormatterTest {

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
    public void shouldReturnEmptyListForEmptyRegions() {
        //given
        TestFeedFormatter formatter = new TestFeedFormatter(null, null);
        PageModelImpl pageModel = getPageModel(new RegionModelSetImpl());

        //when
        Object formatData = formatter.formatData(pageModel);

        //then
        assertTrue(formatData instanceof List);
        List data = (List) formatData;
        assertTrue(data.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnListOfItemsForFilledPageModel() throws DxaException {
        //given
        TestFeedFormatter formatter = new TestFeedFormatter(null, null);
        RegionModelSetImpl regionModels = new RegionModelSetImpl();
        regionModels.add(getRegionModel("reg1", getTestEntity("1"), getTestEntity("2")));
        regionModels.add(getRegionModel("reg2", getTestEntity("3"), getTestEntity("4")));

        //when
        Object formatData = formatter.formatData(getPageModel(regionModels));

        //then
        assertTrue(formatData instanceof List);
        List<Entry> data = (List<Entry>) formatData;
        assertThat(data, contains(new Entry("1"), new Entry("2"), new Entry("3"), new Entry("4")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnListOfItemsIfEntitiesContainsListsOfEntitiesInside() throws DxaException {
        //given
        TestFeedFormatter formatter = new TestFeedFormatter(null, null);
        RegionModelSetImpl regionModels = new RegionModelSetImpl();
        regionModels.add(getRegionModel("reg1", getTestEntity("1", getTestEntity("2"), getTestEntity("3"))));

        //when
        Object formatData = formatter.formatData(getPageModel(regionModels));

        //then
        assertTrue(formatData instanceof List);
        List<Entry> data = (List<Entry>) formatData;
        //also checks order of items
        assertThat(data, contains(new Entry("1"), new Entry("2"), new Entry("3")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotAddEmptyEntities() throws DxaException {
        //given
        TestFeedFormatter formatter = new TestFeedFormatter(null, null);
        RegionModelSetImpl regionModels = new RegionModelSetImpl();
        regionModels.add(getRegionModel("reg1", new EmptyTestEntity()));

        //when
        Object formatData = formatter.formatData(getPageModel(regionModels));

        //then
        assertTrue(formatData instanceof List);
        assertThat((List<Entry>) formatData, empty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportTestEntity1FieldNames() throws DxaException {
        //given
        TestFeedFormatter formatter = new TestFeedFormatter(null, null);
        RegionModelSetImpl regionModels = new RegionModelSetImpl();
        Link link = new Link();
        link.setUrl("url");
        Date date = new Date();
        regionModels.add(getRegionModel("reg1", new TestEntity("1", "2", link.getUrl(), date, Collections.<TestEntity>emptyList())));


        Object formatData = formatter.formatData(getPageModel(regionModels));

        //then
        assertTrue(formatData instanceof List);
        List<Entry> data = (List<Entry>) formatData;
        assertThat(data, contains(new Entry("1", link, new RichText("2"), date)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportTestEntity2FieldNames() throws DxaException {
        //given
        TestFeedFormatter formatter = new TestFeedFormatter(null, null);
        RegionModelSetImpl regionModels = new RegionModelSetImpl();
        Link link = new Link();
        link.setUrl("url");
        DateTime dateTime = DateTime.now();
        regionModels.add(getRegionModel("reg1", new TestEntity2("1", "2", link, dateTime)));

        Object formatData = formatter.formatData(getPageModel(regionModels));

        //then
        assertTrue(formatData instanceof List);
        List<Entry> data = (List<Entry>) formatData;
        assertThat(data, contains(new Entry("1", link, new RichText("2"), dateTime.toDate())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportTestEntity3FieldNames() throws DxaException {
        //given
        TestFeedFormatter formatter = new TestFeedFormatter(null, null);
        RegionModelSetImpl regionModels = new RegionModelSetImpl();
        regionModels.add(getRegionModel("reg1", new TestEntity3("1", "2", "date")));

        Object formatData = formatter.formatData(getPageModel(regionModels));

        //then
        assertTrue(formatData instanceof List);
        List<Entry> data = (List<Entry>) formatData;
        assertThat(data, contains(new Entry("1", null, new RichText("2"), null)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportTestEntity4FieldNames() throws DxaException {
        //given
        TestFeedFormatter formatter = new TestFeedFormatter(null, null);
        RegionModelSetImpl regionModels = new RegionModelSetImpl();
        regionModels.add(getRegionModel("reg1", new TestEntity4("2")));

        Object formatData = formatter.formatData(getPageModel(regionModels));

        //then
        assertTrue(formatData instanceof List);
        List<Entry> data = (List<Entry>) formatData;
        assertThat(data, contains(new Entry(null, null, new RichText("2"), null)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportTestEntity5FieldNames() throws DxaException {
        //given
        TestFeedFormatter formatter = new TestFeedFormatter(null, null);
        RegionModelSetImpl regionModels = new RegionModelSetImpl();
        regionModels.add(getRegionModel("reg1", new TestEntity5("2")));

        Object formatData = formatter.formatData(getPageModel(regionModels));

        //then
        assertTrue(formatData instanceof List);
        List<Entry> data = (List<Entry>) formatData;
        assertThat(data, contains(new Entry(null, null, new RichText("2"), null)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSupportTestEntity6FieldNames() throws DxaException {
        //given
        TestFeedFormatter formatter = new TestFeedFormatter(null, null);
        RegionModelSetImpl regionModels = new RegionModelSetImpl();
        regionModels.add(getRegionModel("reg1", new TestEntity6(new RichText("2"))));

        Object formatData = formatter.formatData(getPageModel(regionModels));

        //then
        assertTrue(formatData instanceof List);
        List<Entry> data = (List<Entry>) formatData;
        assertThat(data, contains(new Entry(null, null, new RichText("2"), null)));
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
        //given
        FeedFormatter feedFormatter = new ExceptionTestFeedFormatter(null, null);
        RegionModelSetImpl regionModels = new RegionModelSetImpl();
        regionModels.add(getRegionModel("reg1", new TestEntity4("2")));

        //when
        Object list = feedFormatter.formatData(getPageModel(regionModels));

        //then
        assertTrue(list instanceof List);
        assertThat((List<Object>) list, empty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldHandleWhenCollectionIsNull() throws DxaException {
        //given
        TestFeedFormatter formatter = new TestFeedFormatter(null, null);
        RegionModelSetImpl regionModels = new RegionModelSetImpl();
        regionModels.add(getRegionModel("reg1", new TestEntity("1", null, null, null, null)));

        //when
        Object formatData = formatter.formatData(getPageModel(regionModels));

        //then
        assertTrue(formatData instanceof List);
        List<Entry> data = (List<Entry>) formatData;
        //also checks order of items
        assertThat(data, contains(new Entry("1")));
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
    private static class EmptyTestEntity extends AbstractEntityModel {

    }

}