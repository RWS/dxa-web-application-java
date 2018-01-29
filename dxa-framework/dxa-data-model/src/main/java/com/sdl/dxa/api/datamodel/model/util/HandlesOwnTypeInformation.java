package com.sdl.dxa.api.datamodel.model.util;

/**
 * Implementors of this interface handle type information for polymorphic serialization. This interfaces works as
 * a marker interfaces and simply repeats {@link HandlesHierarchyTypeInformation} as a more generic.
 * <p>The typical use case is when you lose type information during deserialization. This may happen when you get
 * the data that is mapped to an unknown class. In this case DXA will create a loosely-typed {@link UnknownClassContentModelData}
 * and later restore the original type since it is implementing this interface.</p>
 */
public interface HandlesOwnTypeInformation extends HandlesHierarchyTypeInformation {

}
