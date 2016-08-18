package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.query.AbstractQuery;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class DynamicListTest {

    @Test
    public void shouldFollowEqualsHashCodeContract() {
        DynamicList dynamicList = getDynamicList();
        DynamicList dynamicList2 = getDynamicList();

        assertTrue(dynamicList.equals(dynamicList2));
        assertTrue(dynamicList.hashCode() == dynamicList2.hashCode());
    }

    @NotNull
    private DynamicList getDynamicList() {
        return new DynamicList() {
            @Override
            public AbstractQuery getQuery(Localization localization) {
                return null;
            }

            @Override
            public List getQueryResults() {
                return null;
            }

            @Override
            public void setQueryResults(List queryResults, boolean hasMore) {

            }

            @Override
            public Class getEntityType() {
                return null;
            }
        };
    }

}