package com.sdl.dxa.api.datamodel.model.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sdl.dxa.api.datamodel.json.Polymorphic;

/**
 * When you have a parent class and its children, and you have a {@code ListWrapper}&lt;{@code ParentClass}&gt; with
 * any children type expected inside the list, then we cannot guess the generic type of the list because generics in Java
 * are removed in runtime. If we simply guess the generic type by the first element, we may be wrong
 * <p>To handle this we need this interface. It should be implemented by parent class of the parent-children hierarchy.
 * When we need to get a type of the whole list, the first element will be checked for this, and if the parent implements
 * this, then we will get a list of parents.</p>
 * <p>A parent should also implement {@link Polymorphic}, so we know it is a parent and not a concrete class. By the way,
 * it cannot be abstract or interface. Sorry about this.</p>
 *
 * @dxa.publicApi
 */
@FunctionalInterface
public interface HandlesHierarchyTypeInformation {

    /**
     * Returns {@code Type ID} of the implementor. Implement on parent (top level) classes.
     * <p>The simplest suggested implementation is done on a parent class and look as simple as this:
     * {@code return KnownParentClass.class.getSimpleName();}
     *
     * @return type id
     */
    @JsonIgnore
    String getTypeId();
}
