package com.sdl.webapp.common.api.model.region;

import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>RegionModelSet implementation.</p>
 * <p>Keeps the insertion order.</p>
 */
@SuppressWarnings("DefaultAnnotationParam")
@EqualsAndHashCode(callSuper = false)
@ToString
@Slf4j
public class RegionModelSetImpl extends AbstractSet<RegionModel> implements RegionModelSet {

    private Map<String, RegionModel> modelMapByName = new LinkedHashMap<>();
    private Map<Class<? extends RegionModel>, Set<RegionModel>> modelMapByClass = new LinkedHashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<RegionModel> iterator() {
        return modelMapByName.values().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return modelMapByName.size();
    }

    /** {@inheritDoc} */
    @Override
    public boolean add(RegionModel regionModel) {
        if (modelMapByName.containsKey(regionModel.getName())) {
            log.trace("RegionModelSet already contains region with a key {}, skipping!", regionModel.getName());
            return false;
        }

        modelMapByName.put(regionModel.getName(), regionModel);
        Set<RegionModel> modelSet = modelMapByClass.computeIfAbsent(regionModel.getClass(), k -> new LinkedHashSet<>());
        modelSet.add(regionModel);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public RegionModel get(String name) {
        return modelMapByName.get(name);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends RegionModel> Set<T> get(Class<T> clazz) {
        //noinspection unchecked
        return (Set<T>) modelMapByClass.get(clazz);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsName(final String name) {
        return modelMapByName.containsKey(name);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsClass(Class<? extends RegionModel> clazz) {
        return modelMapByClass.containsKey(clazz);
    }
}
