package com.sdl.webapp.common.api.model.entity;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.TestEntity;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.sdl.webapp.common.api.model.TestEntity.entity;
import static com.sdl.webapp.common.api.model.TestEntity.feedItem;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class DynamicListTest {

    @Test
    public void shouldFollowEqualsHashCodeContract() {
        DynamicList dynamicList = getDynamicList();
        DynamicList dynamicList2 = getDynamicList();

        assertTrue(dynamicList.equals(dynamicList2));
        assertTrue(dynamicList.hashCode() == dynamicList2.hashCode());
    }

    @Test
    public void shouldCollectFeedItems() {
        //given
        FeedItem feedItem1 = feedItem("1");
        FeedItem feedItem2 = feedItem("2");
        TestEntity entity1 = entity(feedItem1);
        TestEntity entity2 = entity(feedItem2);

        DynamicList<TestEntity, SimpleBrokerQuery> dynamicList = spy(getDynamicList());
        lenient().when(dynamicList.getQueryResults()).thenReturn(Lists.newArrayList(entity1, entity2));

        //when
        List<FeedItem> list = dynamicList.extractFeedItems();

        //then
        assertThat(list, IsIterableContainingInOrder.contains(feedItem1, feedItem2));
    }

    @NotNull
    private DynamicList<TestEntity, SimpleBrokerQuery> getDynamicList() {
        return new DynamicList<TestEntity, SimpleBrokerQuery>() {

            @Override
            public SimpleBrokerQuery getQuery(Localization localization) {
                return null;
            }

            @Override
            public List<TestEntity> getQueryResults() {
                return null;
            }

            @Override
            public void setQueryResults(List<TestEntity> queryResults, boolean hasMore) {

            }

            @Override
            public void setQueryResults(List<TestEntity> queryResults) {

            }

            @Override
            public Class<TestEntity> getEntityType() {
                return null;
            }
        };
    }
}