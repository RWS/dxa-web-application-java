package com.sdl.dxa.api.datamodel.processing;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.KeywordModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.api.datamodel.model.RegionModelData;
import com.sdl.dxa.api.datamodel.model.RichTextData;
import com.sdl.dxa.api.datamodel.model.util.CanWrapContentAndMetadata;
import com.sdl.dxa.api.datamodel.model.util.ListWrapper;
import com.sdl.dxa.api.datamodel.model.util.ModelDataWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * Traverses a given data model and calls different processing methods in case it finds specific leaves.
 * Follows deep-first approach.
 * Default methods implementations do nothing.
 */
@Slf4j
public class DataModelDeepFirstSearcher {

    /**
     * Checks if it is still allowed to traverse recursively deeper.
     *
     * @return whether we can go deeper
     */
    protected boolean goingDeepIsAllowed() {
        // does nothing, override if needed
        return true;
    }

    /**
     * Notifies whenever it goes up.
     */
    protected void goLevelUp() {
        // does nothing, override if needed
    }

    /**
     * Processes a {@link PageModelData} leaf.
     *
     * @param pageModelData current leaf
     */
    protected void processPageModel(PageModelData pageModelData) {
        // does nothing, override if needed
    }

    /**
     * Processes a {@link EntityModelData} leaf.
     *
     * @param entityModelData current leaf
     */
    protected void processEntityModel(EntityModelData entityModelData) {
        // does nothing, override if needed
    }

    /**
     * Processes a {@link KeywordModelData} leaf.
     *
     * @param keywordModelData current leaf
     */
    protected void processKeywordModel(KeywordModelData keywordModelData) {
        // does nothing, override if needed
    }

    /**
     * Processes a {@link RichTextData} leaf.
     *
     * @param richTextData current leaf
     */
    protected void processRichTextData(RichTextData richTextData) {
        // does nothing, override if needed
    }

    protected void traverseObject(@Nullable Object value) {
        try {
            if (!goingDeepIsAllowed()) {
                log.warn("Went too deep expanding the model, returning");
                return;
            }

            if (value == null) {
                log.warn("Cannot traverse null value, returning");
                return;
            }

            log.trace("Traversing '{}'", value);

            if (isCollectionType(value)) { // is it a collection type?
                // then let's expand everything and do not expect it to have anything more than concrete types
                traverseCollection(value);
                return;
            }

            // ok, we have one of concrete models, which one? do we want to process/expand it?
            if (value instanceof PageModelData) { // maybe it's a Page?
                traversePageModel((PageModelData) value);
            } else if (value instanceof RegionModelData) { // this is not a page, so maybe region?
                traverseRegionModel((RegionModelData) value);
            } else { // it's one of data models (entities, keywords, etc...)
                traverseDataModel(value);
            }

            // if it may have own content or metadata, let's process it also, maybe we can find models there
            // should go last because content may appear during other expansions
            if (value instanceof CanWrapContentAndMetadata) {
                traverseWrapper((CanWrapContentAndMetadata) value);
            }

        } finally {
            goLevelUp();
        }
    }

    private boolean isCollectionType(Object value) {
        return value instanceof ListWrapper || value instanceof Collection || value instanceof Map;
    }

    private void traverseCollection(Object value) {
        Collection<?> values;

        if (value instanceof Map) { // ok, found a Map (CMD?)
            values = ((Map) value).values();
        } else if (value instanceof ListWrapper) { // if it's not a map, then it's probable a ListWrapper
            values = ((ListWrapper) value).getValues();
        } else { // should have been handled previously, but maybe we lost a type and it's just a collection?
            values = (Collection) value;
        }

        for (Object element : values) { // let's expand our collection element by element
            traverseObject(element);
        }
    }

    private void traversePageModel(PageModelData page) {
        // let's expand all regions, one by one
        if (page.getRegions() != null) {
            for (RegionModelData region : page.getRegions()) {
                traverseObject(region);
            }
        }

        processPageModel(page);
    }

    private void traverseRegionModel(RegionModelData region) {
        if (region.getRegions() != null) { // then it may have nested regions
            for (RegionModelData nestedRegion : region.getRegions()) {
                traverseObject(nestedRegion);
            }
        }

        if (region.getEntities() != null) { // or maybe it has entities?
            for (EntityModelData entity : region.getEntities()) {
                traverseObject(entity);
            }
        }
    }

    private void traverseDataModel(Object value) {
        if (value instanceof EntityModelData) {
            processEntityModel((EntityModelData) value);
        }

        if (value instanceof KeywordModelData) {
            processKeywordModel((KeywordModelData) value);
        }

        if (value instanceof RichTextData) {
            processRichTextData((RichTextData) value);
        }
    }

    private void traverseWrapper(CanWrapContentAndMetadata value) {
        ModelDataWrapper wrapper = value.getDataWrapper();
        if (wrapper.getContent() != null) {
            traverseObject(wrapper.getContent());
        }
        if (wrapper.getMetadata() != null) {
            traverseObject(wrapper.getMetadata());
        }
    }
}
