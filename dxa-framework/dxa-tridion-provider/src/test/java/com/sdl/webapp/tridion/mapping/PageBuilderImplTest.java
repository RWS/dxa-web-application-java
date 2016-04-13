package com.sdl.webapp.tridion.mapping;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.entity.Article;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import org.junit.Test;

import static com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator.creator;
import static org.junit.Assert.assertSame;

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
}