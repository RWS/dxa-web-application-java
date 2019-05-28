package com.sdl.webapp.common.controller;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.ViewModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.DynamicList;
import com.sdl.webapp.common.controller.exception.InternalServerErrorException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListControllerTest {

    @Mock
    private WebRequestContext webRequestContext;

    @Mock
    private ContentProvider contentProvider;

    @InjectMocks
    private ListController listController;

    @Test
    public void shouldOnlyEnrichDynamicLists() throws Exception {
        //given
        TestEntity model = new TestEntity();

        //when
        ViewModel viewModel = listController.enrichModel(model, null);

        //then
        assertSame(model, viewModel);
    }

    @Test
    public void shouldCallPopulateDynamicList() throws Exception {
        //given
        String id = "id";
        DynamicList testList = mock(DynamicList.class);
        doCallRealMethod().when(testList).setStart(anyInt());
        doCallRealMethod().when(testList).getStart();
        when(testList.getId()).thenReturn(id);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("id", id);
        request.setParameter("start", "5");

        //when
        listController.enrichModel(testList, request);

        //then
        ArgumentCaptor<DynamicList> captor = ArgumentCaptor.forClass(DynamicList.class);
        verify(contentProvider).populateDynamicList(captor.capture(), any());
        assertEquals("Start is set", 5, captor.getValue().getStart());
    }

    @Test
    public void shouldNotDoubleCallPopulateDynamicList() throws Exception {
        //given
        DynamicList dynamicList = mock(DynamicList.class);
        List<? extends EntityModel> list = Lists.newArrayList(new TestEntity());
        doReturn(list).when(dynamicList).getQueryResults();


        //when
        ViewModel viewModel = listController.enrichModel(dynamicList, null);

        //then
        assertSame(dynamicList, viewModel);
        verify(contentProvider, never()).populateDynamicList(any(DynamicList.class), any());
    }

    @Test(expected = InternalServerErrorException.class)
    public void shouldHandleContentProviderException() throws Exception {
        //given
        DynamicList testList = mock(DynamicList.class);
        doThrow(ContentProviderException.class)
                .when(contentProvider).populateDynamicList(any(DynamicList.class), any());

        //when
        listController.enrichModel(testList, new MockHttpServletRequest());

        //then
        verify(contentProvider).populateDynamicList(any(DynamicList.class), any(Localization.class));
    }

    private static class TestEntity extends AbstractEntityModel {

    }
}