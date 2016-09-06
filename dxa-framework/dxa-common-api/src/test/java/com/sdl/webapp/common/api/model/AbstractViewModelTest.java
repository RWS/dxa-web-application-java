package com.sdl.webapp.common.api.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.formatters.support.FeedItemsProvider;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static com.sdl.webapp.common.api.model.TestEntity.feedItem;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractViewModelTest {

    @Mock
    private AbstractViewModel viewModel;

    @Test
    public void shouldAddExtensionData() throws Exception {
        //given
        doCallRealMethod().when(viewModel).addExtensionData(anyString(), any());
        doCallRealMethod().when(viewModel).getExtensionData();
        String object1 = "object1";
        Object object2 = new Object();

        //when
        viewModel.addExtensionData("key1", object1);
        viewModel.addExtensionData("key2", object2);

        //then
        assertSame(object1, viewModel.getExtensionData().get("key1"));
        assertSame(object2, viewModel.getExtensionData().get("key2"));
    }

    @Test
    public void shouldGetHtmlClasses() {
        //given
        doCallRealMethod().when(viewModel).getHtmlClasses();
        doCallRealMethod().when(viewModel).setHtmlClasses(anyString());
        String htmlClasses = "class1 class2";

        //when
        viewModel.setHtmlClasses(htmlClasses);

        //then
        assertSame(htmlClasses, viewModel.getHtmlClasses());
    }

    @Test
    public void shouldSetXpmMetadata() {
        //given
        doCallRealMethod().when(viewModel).getXpmMetadata();
        doCallRealMethod().when(viewModel).setXpmMetadata(anyMapOf(String.class, Object.class));
        ImmutableMap<String, Object> map = ImmutableMap.<String, Object>of("key1", "obj1");

        //when
        viewModel.setXpmMetadata(map);

        //then
        assertEquals(map, viewModel.getXpmMetadata());
    }

    @Test
    public void shouldSetMvcData() {
        //given
        doCallRealMethod().when(viewModel).getMvcData();
        doCallRealMethod().when(viewModel).setMvcData(any(MvcData.class));
        MvcData mvcData = MvcDataCreator.creator().fromQualifiedName("Core:Entity:Image").create();

        //when
        viewModel.setMvcData(mvcData);

        //then
        assertEquals(mvcData, viewModel.getMvcData());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCollectAllFeedItemProviders() {
        //given
        when(viewModel.collectFeedItems(anyListOf(FeedItemsProvider.class))).thenCallRealMethod();

        FeedItemsProvider feedItemsProvider = mock(FeedItemsProvider.class);
        FeedItem feedItem1 = feedItem("1");
        FeedItem feedItem2 = feedItem("2");
        FeedItem feedItem3 = feedItem("3");
        FeedItem feedItem4 = feedItem("4");
        when(feedItemsProvider.extractFeedItems()).thenReturn(Lists.newArrayList(feedItem1, feedItem2),
                Lists.newArrayList(feedItem3, feedItem4));
        List<FeedItemsProvider> list = Lists.newArrayList(feedItemsProvider, feedItemsProvider);

        //when
        List<FeedItem> feedItems = viewModel.collectFeedItems(list);

        //then
        assertTrue(feedItems.size() == 4);
        assertThat(feedItems, IsIterableContainingInOrder.contains(feedItem1, feedItem2, feedItem3, feedItem4));
    }

    @Test
    public void shouldReturnEmptyListForNullOrEmptyListOfProviders() {
        //given
        when(viewModel.collectFeedItems(anyListOf(FeedItemsProvider.class))).thenCallRealMethod();

        //when
        List<FeedItem> list1 = viewModel.collectFeedItems(null);
        List<FeedItem> list2 = viewModel.collectFeedItems(Collections.<FeedItemsProvider>emptyList());

        //then
        assertThat(list1, empty());
        assertThat(list2, empty());
    }

}