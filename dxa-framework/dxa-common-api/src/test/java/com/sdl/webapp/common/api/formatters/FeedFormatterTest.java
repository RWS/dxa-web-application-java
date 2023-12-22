package com.sdl.webapp.common.api.formatters;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.formatters.support.FeedItemsProvider;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void shouldThrowAnExceptionIfModelIsNotPage() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            // Given
            FeedFormatter feedFormatter = new TestFeedFormatter(null, null);

            // When
            feedFormatter.formatData(new Object());

            // Then exception
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnEmptyListInCaseOfInnerException() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(
                new ExceptionTestFeedFormatter(null, null),
                new RegionModelImpl[]{getRegionModel("reg1", new TestEntity("1"))}
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldHandleWhenCollectionIsNull() throws DxaException {
        shouldGetGivenEntriesFromRegionModel(getRegionModel("reg1", new TestEntity("1")), new Entry("1"));
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
        return new TestEntity(id);
    }

    @NotNull
    private DefaultPageModel getPageModel(RegionModelSet regionModels) {
        DefaultPageModel pageModel = new DefaultPageModel();
        pageModel.setRegions(regionModels);
        return pageModel;
    }

    private static class TestFeedFormatter extends FeedFormatter {

        TestFeedFormatter(HttpServletRequest request, WebRequestContext context) {
            super(request, context);
        }

        @Override
        public Object getSyndicationItem(FeedItem item) {
            return new Entry(item.getHeadline(), item.getLink(), item.getSummary(), item.getDate());
        }
    }

    private static class ExceptionTestFeedFormatter extends FeedFormatter {

        ExceptionTestFeedFormatter(HttpServletRequest request, WebRequestContext context) {
            super(request, context);
        }

        @Override
        public Object getSyndicationItem(FeedItem item) {
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
    private static class TestEntity extends AbstractEntityModel implements FeedItemsProvider {

        private String headline;

        private String summary;

        private String url;

        private Date updated;

        TestEntity(String headline) {
            this.headline = headline;
        }

        @Override
        public List<FeedItem> extractFeedItems() {
            FeedItem feedItem = new FeedItem();
            feedItem.setHeadline(headline);
            return Lists.newArrayList(feedItem);
        }
    }
}