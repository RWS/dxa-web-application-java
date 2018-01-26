package com.sdl.dxa.api.datamodel.model.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sdl.dxa.api.datamodel.model.util.ListWrapper.UnknownClassesListWrapper;

/**
 * Implementors of this interface are typically loosely typed but they still can return the {@code type ID} of their type.
 * <p>Let's say, you have customized a data model on CM and your web application and added a non-core data model class.
 * This means that CM will add this class and its type information to R2 JSON and Model Service/WebApp will try to resolve
 * the real class using the information from {@code $type}. If the class is not a known polymorphic class (which is
 * the case if e.g. your Model Service is running in a cloud and you can't extend its data model), then this piece of unknown data
 * becomes a loosely-typed {@link UnknownClassesContentModelData} (a {@code Map} basically)
 * or a {@link UnknownClassesListWrapper} (a {@code List}) of same Maps.</p>
 * <p>On the way back when the data is serialized again, we lose type information. This interface helps classes to report
 * what is the their {@code type ID}.</p>
 * <p>CM Model Builder adds {@code @type} property to JSON to any non-core class or list of such classes.</p>
 */
@FunctionalInterface
public interface HandlesTypeInformationForUnknownClasses {

    /**
     * Returns {@code Type ID} of the implementor. Implement on parent (top level) classes.
     *
     * @return type id
     */
    @JsonIgnore
    String getTypeId();
}
