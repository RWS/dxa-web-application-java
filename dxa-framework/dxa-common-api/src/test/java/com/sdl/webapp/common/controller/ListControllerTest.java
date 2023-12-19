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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
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
        lenient().doCallRealMethod().when(testList).setStart(anyInt());
        lenient().doCallRealMethod().when(testList).getStart();
        lenient().when(testList.getId()).thenReturn(id);
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

    @Test
    public void shouldHandleContentProviderException() throws Exception {
        Assertions.assertThrows(InternalServerErrorException.class, () -> {
            // Given
            DynamicList testList = mock(DynamicList.class);
            doThrow(ContentProviderException.class)
                    .when(contentProvider).populateDynamicList(any(DynamicList.class), any());

            // When
            listController.enrichModel(testList, new MockHttpServletRequest());

            // Then
            verify(contentProvider).populateDynamicList(any(DynamicList.class), any(Localization.class));
        });
    }

    private static class TestEntity extends AbstractEntityModel {

    }
}