package com.sdl.dxa.api.datamodel.model.util;

import com.sdl.dxa.api.datamodel.model.ContentModelData;

/**
 * Handler of an unknown entity-level class. Basically represents any data structure that is not known with a Map.
 */
public class UnknownClassesContentModelData extends ContentModelData {

    @Override
    protected boolean shouldRemoveDollarType() {
        return false;
    }
}
