package com.sdl.webapp.common.api.model;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.formatters.support.FeedItemsProvider;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;

import static org.mockito.Mockito.*;

public abstract class TestEntity extends AbstractEntityModel implements FeedItemsProvider {

    public static TestEntity entity(FeedItem feedItem) {
        TestEntity testEntity = mock(TestEntity.class);
        lenient().when(testEntity.extractFeedItems()).thenReturn(Lists.newArrayList(feedItem));
        return testEntity;
    }

    public static FeedItem feedItem(String headline) {
        FeedItem feedItem = new FeedItem();
        feedItem.setHeadline(headline);
        return feedItem;
    }

    public static class TestEntityNoFeed extends AbstractEntityModel {

    }
}
