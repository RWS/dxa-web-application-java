package com.sdl.webapp.common.api.model.region;

import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.AbstractViewModel;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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

    @Test
    public void shouldGetRegionModelByName() throws DxaException {
        //given
        RegionModelSet set = new RegionModelSetImpl();
        RegionModelImpl expected = new RegionModelImpl("test1");
        set.add(new RegionModelImpl("test"));
        set.add(expected);

        //when
        RegionModel result = set.get("test1");

        //then
        assertEquals(expected, result);
    }

    @Test
    public void shouldHaveWorkingEqualsHashCodeToStringMethods() {
        //given
        RegionModelSet set1 = new RegionModelSetImpl();
        RegionModelSet set2 = new RegionModelSetImpl();

        //when

        //then
        assertTrue(set1.equals(set2));
        assertEquals(set1.hashCode(), set2.hashCode());
        assertEquals(set1.toString(), set2.toString());
    }

    @Test
    public void shouldImplementEqualsAndHashCodeAndToString() throws NoSuchMethodException {
        //given
        Class<RegionModelSetImpl> modelSetClass = RegionModelSetImpl.class;

        //when

        //then
        assertNotNull(modelSetClass.getDeclaredMethod("equals", Object.class));
        assertNotNull(modelSetClass.getDeclaredMethod("hashCode"));
        assertNotNull(modelSetClass.getDeclaredMethod("toString"));
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    private static class RegionModelImplTest extends AbstractViewModel implements RegionModel {

        private String name;

        private String schemaId;

        public RegionModelImplTest(String name) {

            this.name = name;
        }

        @Override
        public String getSchemaId() {
            return schemaId;
        }

        @Override
        public void setSchemaId(String schemaId) {
            this.schemaId = schemaId;
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
        public void addEntity(EntityModel entity) {

        }

        @Override
        public RegionModel deepCopy() {
            return null;
        }

        @Override
        public String getXpmMarkup(Localization localization) {
            return null;
        }

        @Override
        public List<FeedItem> extractFeedItems() {
            return null;
        }
    }

}