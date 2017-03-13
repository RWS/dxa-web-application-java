package com.sdl.dxa.api.datamodel.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegionModelDataTest {

    @Test
    public void shouldAddRegion() throws Exception {
        //given
        RegionModelData modelData = new RegionModelData("name", "1", null, null);
        RegionModelData regionToAdd = new RegionModelData("name2", null, null, null);

        //when
        modelData.addRegion(regionToAdd);

        //then
        assertEquals(regionToAdd, modelData.getRegions().get(0));
    }
}