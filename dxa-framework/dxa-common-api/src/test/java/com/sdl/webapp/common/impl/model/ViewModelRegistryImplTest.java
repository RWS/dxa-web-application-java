package com.sdl.webapp.common.impl.model;

import com.google.common.collect.Sets;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.ViewModelRegistry;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.exceptions.DxaException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.util.ReflectionTestUtils.getField;

@RunWith(MockitoJUnitRunner.class)
public class ViewModelRegistryImplTest {

    @Mock
    private SemanticMappingRegistry semanticMappingRegistry;

    @InjectMocks
    private ViewModelRegistry registry = new ViewModelRegistryImpl();

    @Before
    public void init() throws SemanticMappingException {
        registry.registerViewModel(MvcDataCreator.creator()
                .fromQualifiedName("AreaName:Entity:TestEntity")
                .create(), TestEntity.class);

        registry.registerViewModel(MvcDataCreator.creator()
                .fromQualifiedName("Test:Entity:TestEntity2")
                .create(), TestEntity2.class);

        doReturn(TestEntity.class).when(semanticMappingRegistry).getEntityClassByFullyQualifiedName("registered", null);
    }

    @Test
    public void shouldResolveEntityClass_ToDefaultModule() throws DxaException {
        //when
        Class<? extends ViewModel> entityClass = registry.getViewEntityClass("TestEntity");

        //then
        assertEquals(TestEntity.class, entityClass);
    }

    @Test
    public void shouldResolveEntityClass_ToExplicitAreaName() throws DxaException {
        //when
        Class<? extends ViewModel> entityClass = registry.getViewEntityClass("Test:TestEntity2");

        //then
        assertEquals(TestEntity2.class, entityClass);
    }

    @Test
    public void shouldRegisterFirstView_WhenMappingIsMultiple() {
        //given
        int initial = ((Map) getField(registry, "viewEntityClassMap")).size();

        //when
        registry.registerViewModel(MvcDataCreator.creator()
                .fromQualifiedName("AreaName:Entity:TestEntity")
                .create(), TestEntity3.class);

        //then
        assertEquals(((Map) getField(registry, "viewEntityClassMap")).size(), initial);
    }

    @Test(expected = DxaException.class)
    public void shouldThrowException_WhenClassNotFound() throws DxaException {
        //when
        registry.getViewEntityClass("Area:View");
    }

    @Test
    public void shouldReturnViewModel_WhenRegistered() throws DxaException {
        //when
        Class<? extends ViewModel> registered = registry.getMappedModelTypes("registered", null);

        //then
        assertEquals(TestEntity.class, registered);
    }

    @Test
    public void shouldViewModel_WhenRegistered_WithSet() throws DxaException {
        //when
        Class<? extends ViewModel> registered = registry.getMappedModelTypes(Sets.newHashSet("not-registered", "registered"));

        //then
        assertEquals(TestEntity.class, registered);
    }

    @Test
    public void shouldReturnNull_IfNothingIfSemanticRegistryFound() throws DxaException {
        Class<? extends ViewModel> registered = registry.getMappedModelTypes(Sets.newHashSet("not-registered", "not-registered-2"));

        //then
        assertNull(registered);
    }

    @Test
    public void shouldResolveFromViewRegistry_IfSemanticRegistryFailed() throws DxaException {
        //when
        Class<? extends ViewModel> entityClass = registry.getMappedModelTypes("Test:Entity:TestEntity2", null);

        //then
        assertEquals(TestEntity2.class, entityClass);
    }

    @Test
    public void shouldResolveFromViewRegistry_IfSemanticRegistryFailed_WithNoArea() throws DxaException {
        //when
        Class<? extends ViewModel> entityClass = registry.getMappedModelTypes("TestEntity2", null);

        //then
        assertEquals(TestEntity2.class, entityClass);
    }

    private class TestEntity extends AbstractEntityModel {

    }

    private class TestEntity2 extends AbstractEntityModel {

    }

    private class TestEntity3 extends AbstractEntityModel {

    }
}