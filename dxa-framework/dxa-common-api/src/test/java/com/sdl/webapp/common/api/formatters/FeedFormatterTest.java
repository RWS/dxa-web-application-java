package com.sdl.webapp.common.api.formatters;

import com.sdl.dxa.modules.core.model.entity.Teaser;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.page.PageModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNull;
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
        assertTrue(data.contains(new Entry("1")) &&
                data.contains(new Entry("2")) &&
                data.contains(new Entry("3")) &&
                data.contains(new Entry("4")));
    }

    @NotNull
    private RegionModelImpl getRegionModel(String name, EntityModel... entityModels) throws DxaException {
        RegionModelImpl region = new RegionModelImpl(name);
        for (EntityModel entityModel : entityModels) {
            region.addEntity(entityModel);
        }
        return region;
    }

    private @NotNull EntityModel getTestEntity(String id) {
        TestEntity entity = new TestEntity();
        entity.setId(id);
        return entity;
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
        public Object getSyndicationItemFromTeaser(Teaser item) throws Exception {
            return new Entry(item.getId());
        }

        @Override
        protected List<Object> getFeedItemsFromEntity(EntityModel entity) {
            return Collections.<Object>singletonList(new Entry(entity.getId()));
        }
    }

    @Data
    @AllArgsConstructor
    private static class Entry {

        private String id;
    }

    private static class TestEntity extends AbstractEntityModel {

    }

}