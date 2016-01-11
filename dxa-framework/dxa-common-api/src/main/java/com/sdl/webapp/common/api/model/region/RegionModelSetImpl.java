package com.sdl.webapp.common.api.model.region;

import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

@EqualsAndHashCode(callSuper = false)
@ToString
public class RegionModelSetImpl extends AbstractSet<RegionModel> implements RegionModelSet {

    private Map<String, RegionModel> modelMap = new HashMap<>();

    @Override
    public Iterator<RegionModel> iterator() {
        return modelMap.values().iterator();
    }

    @Override
    public int size() {
        return modelMap.size();
    }

    @Override
    public boolean add(RegionModel regionModel) {
        return !Objects.equals(modelMap.put(regionModel.getName(), regionModel), regionModel);
    }

    @Override
    public RegionModel get(String name) {
        return modelMap.get(name);
    }

    @Override
    public boolean containsName(final String name) {
        return modelMap.containsKey(name);
    }
}
