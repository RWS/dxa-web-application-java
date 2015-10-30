package com.sdl.webapp.common.api.model.region;

import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.Collection;
import java.util.LinkedList;

public class RegionModelSetImpl extends LinkedList<RegionModel> implements RegionModelSet {

    @Override
    public RegionModel get(String name) {
        // TODO Auto-generated method stub

        Collection<RegionModel> c = CollectionUtils.select(this, new RegionsPredicate(name));
        if (!c.isEmpty()) {
            return c.iterator().next();
        }
        return null;
    }

    @Override
    public Boolean containsKey(final String name) {
        int matches = CollectionUtils.countMatches(this, new RegionsPredicate(name));
        return matches > 0;
    }

    static class RegionsPredicate implements Predicate {
        private final String regionName;

        public RegionsPredicate(String name) {
            regionName = name;
        }

        public boolean evaluate(Object r) {
            RegionModel m = (RegionModel) r;
            return m.getName().equals(regionName);
        }
    }
}
