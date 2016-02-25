package com.sdl.webapp.common.api.model.region;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RegionModelSetImplTest {

    @Test
    public void shouldPreserveAddingOrder() throws DxaException {
        //given
        List<RegionModel> regionModels = new ArrayList<>();
        regionModels.add(new RegionModelImpl("name1"));
        regionModels.add(new RegionModelImplTest("name3"));
        regionModels.add(new RegionModelImplTest("name2"));

        RegionModelSet set = new RegionModelSetImpl();
        set.add(regionModels.get(0));
        set.add(regionModels.get(1));
        set.add(regionModels.get(2));

        //when
        int i = 0;
        for (RegionModel regionModel : set) {
            //then
            assertEquals(regionModels.get(i++), regionModel);
        }

        //when
        i = 1;
        for (RegionModelImplTest regionModel : set.get(RegionModelImplTest.class)) {
            //then
            assertEquals(regionModels.get(i++), regionModel);
        }
    }

    @Test
    public void shouldGiveListOfParticularClass() throws DxaException {
        //given
        RegionModelSet set = new RegionModelSetImpl();
        set.add(new RegionModelImplTest("1"));
        set.add(new RegionModelImplTest("2"));
        set.add(new RegionModelImpl("3"));
        set.add(new RegionModelImplTest("4"));

        //when
        Set<RegionModelImplTest> testSet = set.get(RegionModelImplTest.class);

        //then
        assertThat(testSet, hasSize(3));
        for (RegionModel test : testSet) {
            assertTrue(test instanceof RegionModelImplTest);
        }
    }

    @Test
    public void shouldNotHaveDuplicateNames() throws DxaException {
        //given
        RegionModelSet set = new RegionModelSetImpl();

        //when
        set.add(new RegionModelImpl("q"));
        set.add(new RegionModelImpl("q"));

        Set<RegionModelImpl> set1 = set.get(RegionModelImpl.class);

        //then
        assertThat(set, hasSize(1));
        assertThat(set1, hasSize(1));
    }

    @Test
    public void shouldReturnTrueOfFalseIfContainsOrNotClassOrName() throws DxaException {
        //given
        RegionModelSet set = new RegionModelSetImpl();
        set.add(new RegionModelImpl("q"));

        //when
        boolean containsName = set.containsName("q");
        boolean containsClass = set.containsClass(RegionModelImpl.class);
        boolean notContainsClass = set.containsClass(RegionModelImplTest.class);
        boolean notContainsName = set.containsName("asd");

        //then
        assertTrue(containsClass && containsName);
        assertFalse(notContainsClass || notContainsName);
    }

    @Test(expected = DxaException.class)
    public void shouldThrowExceptionIfNameIsEmpty() throws DxaException {
        //given
        RegionModelSet set = new RegionModelSetImpl();

        //when
        set.add(new RegionModelImpl(null, ""));

        //then
        //exception
    }

    @Test(expected = DxaException.class)
    public void shouldThrowExceptionIfNameIsNull() throws DxaException {
        RegionModelSet set = new RegionModelSetImpl();

        //when
        set.add(new RegionModelImpl("", ""));

        //then
        //exception
    }

    @Data
    private static class RegionModelImplTest implements RegionModel {

        private String name;

        public RegionModelImplTest(String name) {

            this.name = name;
        }

        @Override
        public List<EntityModel> getEntities() {
            return null;
        }

        @Override
        public EntityModel getEntity(String entityId) {
            return null;
        }

        @Override
        public RegionModelSet getRegions() {
            return null;
        }

        @Override
        public MvcData getMvcData() {
            return null;
        }

        @Override
        public void setMvcData(MvcData value) {

        }

        @Override
        public Map<String, Object> getXpmMetadata() {
            return null;
        }

        @Override
        public String getXpmMarkup(Localization localization) {
            return null;
        }

        @Override
        public String getHtmlClasses() {
            return null;
        }

        @Override
        public void setHtmlClasses(String s) {

        }

        @Override
        public void addEntity(EntityModel entity) {

        }
    }

}