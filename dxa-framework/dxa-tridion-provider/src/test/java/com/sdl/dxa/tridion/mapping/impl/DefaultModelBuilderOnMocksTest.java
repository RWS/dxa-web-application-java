package com.sdl.dxa.tridion.mapping.impl;

import com.google.common.collect.Lists;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.MvcModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.api.datamodel.model.RegionModelData;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.page.DefaultPageModel;
import org.jetbrains.annotations.NotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultModelBuilderOnMocksTest {
    public static final String REGION_SCHEMA_ID = "region schema id : 1";
    @Mock
    private ViewModelRegistry viewModelRegistry;
    @Mock
    private PageModel pageModel;
    @Mock
    private PageModelData pageModelData;
    @Mock
    private MvcModelData mvcModelData;
    @Mock
    private MvcData mvcData;
    @Mock
    private RegionModel regionModel1;
    @Mock
    private RegionModel regionModel2;
    @Mock
    private RegionModel regionModel3;

    @Spy
    @InjectMocks
    private DefaultModelBuilder modelBuilder;

    @BeforeEach
    public void setUp() {
        lenient().when(pageModelData.getMvcData()).thenReturn(mvcModelData);
    }

    @Test
    public void instantiatePageModelInvalidArgument() throws Exception {
        assertSame(pageModel, modelBuilder.instantiatePageModel(pageModel, pageModelData));
    }

    @Test
    public void instantiatePageModelException() throws Exception {
        Assertions.assertThrows(SemanticMappingException.class, () -> {
            lenient().doReturn(mvcData).when(modelBuilder).createMvcData(mvcModelData, DefaultsMvcData.PAGE);
            doThrow(SemanticMappingException.class).when(viewModelRegistry).getViewModelType(mvcData);

            modelBuilder.instantiatePageModel(null, pageModelData);
        });
    }

    @Test
    public void instantiatePageModelVerifyCreateByType() throws Exception {
        lenient().doReturn(mvcData).when(modelBuilder).createMvcData(mvcModelData, DefaultsMvcData.PAGE);
        lenient().doReturn(TestPageModel.class).when(viewModelRegistry).getViewModelType(mvcData);
        TestPageModel realPageModel = mock(TestPageModel.class);
        lenient().doReturn(realPageModel).when(modelBuilder).createViewModel(TestPageModel.class, pageModelData);

        assertSame(realPageModel, modelBuilder.instantiatePageModel(null, pageModelData));

        verify(modelBuilder).createViewModel(TestPageModel.class, pageModelData);
        verify(realPageModel).setMvcData(mvcData);
    }

    @Test
    public void instantiatePageModelVerifyDefaultPageModel() throws Exception {
        lenient().doReturn(mvcData).when(modelBuilder).createMvcData(mvcModelData, DefaultsMvcData.PAGE);
        lenient().doReturn(null).when(viewModelRegistry).getViewModelType(mvcData);
        TestPageModel realPageModel = mock(TestPageModel.class);
        lenient().doReturn(realPageModel).when(modelBuilder).createDefaultPageModel();

        assertSame(realPageModel, modelBuilder.instantiatePageModel(null, pageModelData));

        verify(modelBuilder, never()).createViewModel(TestPageModel.class, pageModelData);
        verify(modelBuilder).createDefaultPageModel();
        verify(realPageModel).setMvcData(mvcData);
    }

    @Test
    public void processRegionsNull() throws Exception {
        RegionModelSet regionsToAdd = mock(RegionModelSet.class);

        modelBuilder.processRegions(null, regionsToAdd);

        verify(modelBuilder).processRegions(null, regionsToAdd);
        verifyNoMoreInteractions(modelBuilder, regionsToAdd);
    }

    @Test
    public void processRegionsExceptionOnSecondItem() throws Exception {
        Assertions.assertThrows(SemanticMappingException.class, () -> {
            List<RegionModelData> regions = prepareRegions();
            doThrow(new SemanticMappingException()).when(modelBuilder).createRegionModel(regions.get(1));

            RegionModelSet regionsToAdd = mock(RegionModelSet.class);

            modelBuilder.processRegions(regions, regionsToAdd);
        });
    }

    @Test
    public void processRegionsAllItemsAdded() throws Exception {
        List<RegionModelData> regions = prepareRegions();
        RegionModelSet regionsToAdd = mock(RegionModelSet.class);

        modelBuilder.processRegions(regions, regionsToAdd);

        verify(regionsToAdd).add(regionModel1);
        verify(regionsToAdd).add(regionModel2);
        verify(regionsToAdd).add(regionModel3);
        verifyNoMoreInteractions(regionsToAdd);
    }

    @Test
    public void createRegionModel() throws Exception {
        RegionModelData regionModelData = mock(RegionModelData.class);
        List<RegionModelData> regions = new ArrayList<>();
        lenient().when(regionModelData.getRegions()).thenReturn(regions);
        lenient().when(regionModelData.getMvcData()).thenReturn(mvcModelData);
        lenient().doReturn(mvcData).when(modelBuilder).createMvcData(mvcModelData, DefaultsMvcData.REGION);
        lenient().doReturn(TestPageModel.class).when(viewModelRegistry).getViewModelType(mvcData);
        lenient().doReturn(regionModel1).when(modelBuilder).createRegionModel(regionModelData, TestPageModel.class);
        lenient().when(regionModelData.getSchemaId()).thenReturn(REGION_SCHEMA_ID);
        lenient().doNothing().when(modelBuilder).processOwnSchema(regionModelData, TestPageModel.class, regionModel1, REGION_SCHEMA_ID);
        RegionModelSet regionModelSet = mock(RegionModelSet.class);
        lenient().when(regionModel1.getRegions()).thenReturn(regionModelSet);
        lenient().doNothing().when(modelBuilder).processRegions(regions, regionModelSet);
        lenient().doNothing().when(modelBuilder).addEntitiesToRegionModels(regionModelData, regionModel1);

        assertEquals(regionModel1, modelBuilder.createRegionModel(regionModelData));

        verify(regionModel1).setSchemaId(REGION_SCHEMA_ID);
        verify(modelBuilder).processOwnSchema(regionModelData, TestPageModel.class, regionModel1, REGION_SCHEMA_ID);
        verify(modelBuilder).fillViewModel(regionModel1, regionModelData);
        verify(regionModel1).setMvcData(mvcData);
        verify(modelBuilder).processRegions(regions, regionModelSet);
    }

    @Test
    public void addEntitiesToRegionModelsNoEntities() {
        RegionModelData regionModelData = mock(RegionModelData.class);
        lenient().when(regionModelData.getEntities()).thenReturn(null);

        modelBuilder.addEntitiesToRegionModels(regionModelData, regionModel1);

        verify(modelBuilder).addEntitiesToRegionModels(regionModelData, regionModel1);
        verifyNoMoreInteractions(modelBuilder);
    }

    @Test
    public void addEntitiesToRegionModels() {
        RegionModelData regionModelData = mock(RegionModelData.class);
        EntityModelData entityModelData = mock(EntityModelData.class);
        EntityModel entityModel = mock(EntityModel.class);
        lenient().when(regionModelData.getEntities()).thenReturn(Collections.singletonList(entityModelData));
        doReturn(entityModel).when(modelBuilder).createEntityModel(entityModelData);
        lenient().when(entityModel.getMvcData()).thenReturn(mvcData);
        lenient().when(regionModelData.getName()).thenReturn("regionModelData name");

        modelBuilder.addEntitiesToRegionModels(regionModelData, regionModel1);

        verify(modelBuilder).addEntitiesToRegionModels(regionModelData, regionModel1);
        verify(entityModel).setMvcData(any());
        verify(regionModel1).addEntity(entityModel);
    }

    @NotNull
    private List<RegionModelData> prepareRegions() throws SemanticMappingException {
        RegionModelData regionModelData1 = mock(RegionModelData.class);
        RegionModelData regionModelData2 = mock(RegionModelData.class);
        RegionModelData regionModelData3 = mock(RegionModelData.class);
        List<RegionModelData> regions = Lists.newArrayList(regionModelData1, regionModelData2, regionModelData3);
        lenient().doReturn(regionModel1).when(modelBuilder).createRegionModel(regionModelData1);
        lenient().doReturn(regionModel2).when(modelBuilder).createRegionModel(regionModelData2);
        lenient().doReturn(regionModel3).when(modelBuilder).createRegionModel(regionModelData3);
        return regions;
    }

    public static class TestPageModel extends DefaultPageModel {
        private String localName;

        public TestPageModel(String name) {
            this.localName = name;
        }
    }
}