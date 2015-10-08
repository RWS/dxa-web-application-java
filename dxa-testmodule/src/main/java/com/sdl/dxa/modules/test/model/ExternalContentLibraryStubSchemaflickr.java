package com.sdl.dxa.modules.test.model;

import com.sdl.webapp.common.api.model.entity.EclItem;
import com.sdl.webapp.common.api.model.entity.MediaItemMvcData;
import com.sdl.webapp.common.exceptions.DxaException;
import org.w3c.dom.Node;

public class ExternalContentLibraryStubSchemaflickr extends EclItem {

    public void readFromXhtmlElement(Node xhtmlElement) {
        try {
            this.setMvcData(new MediaItemMvcData("Test:Entity:EclFlickr"));
        } catch (DxaException e) {

        }
    }
}
