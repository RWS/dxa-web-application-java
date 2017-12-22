package com.sdl.dxa.api.datamodel.model.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.KeywordModelData;
import com.sdl.dxa.api.datamodel.model.RichTextData;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for the polymorphic list JSON representation coming from .NET Template Builder.
 * <p>It serializes the list as the following meaning that all elements of a list are of type {@link ContentModelData}</p>
 * <pre><code>
 * {
 *     "list": {
 *         "$type": "ContentModelData[]",
 *         "$values": [
 *          { ... }, { ... }
 *         ]
 *     }
 * }
 * </code></pre>
 * <p>For the known and excepted types of list subtypes are created so that type information is not expected on leaves.
 * In case of using the generic type, type information is added and expected on leaves by deserializer.</p>
 * <p>Unfortunately it's currently not possible to implement {@link List} interface since Jackson is handling this differently.
 * This is to be investigated and done in the future.</p>
 *
 * @param <T> type of elements of the list
 */
@Setter(value = AccessLevel.NONE)
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListWrapper<T> implements DelegatesToList<T> {

    @JsonProperty("$values")
    private List<T> values;

    public ListWrapper(List<T> values) {
        this.values = new ArrayList<>();
        this.values.addAll(values);
    }

    /**
     * The concrete implementation of {@link ListWrapper} for {@link ContentModelData}.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @JsonTypeName("ContentModelData[]")
    public static class ContentModelDataListWrapper extends ListWrapper<ContentModelData> {

        public ContentModelDataListWrapper(List<ContentModelData> values) {
            super(values);
        }
    }


    /**
     * The concrete implementation of {@link ListWrapper} for {@link ContentModelData}.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @JsonTypeName("KeywordModelData[]")
    public static class KeywordModelDataListWrapper extends ListWrapper<KeywordModelData> {

        public KeywordModelDataListWrapper(List<KeywordModelData> values) {
            super(values);
        }
    }


    /**
     * The concrete implementation of {@link ListWrapper} for {@link EntityModelData}.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @JsonTypeName("EntityModelData[]")
    public static class EntityModelDataListWrapper extends ListWrapper<EntityModelData> {

        public EntityModelDataListWrapper(List<EntityModelData> values) {
            super(values);
        }
    }

    /**
     * The concrete implementation of {@link ListWrapper} for {@link RichTextData}.
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @JsonTypeName("RichTextData[]")
    public static class RichTextDataListWrapper extends ListWrapper<RichTextData> {

        public RichTextDataListWrapper(List<RichTextData> values) {
            super(values);
        }
    }
}
