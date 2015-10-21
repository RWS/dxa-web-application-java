package com.sdl.dxa.modules.test.model;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.MvcDataImpl;
import com.sdl.webapp.common.api.model.entity.EclItem;
import org.w3c.dom.Node;

public class ExternalContentLibraryStubSchemaflickr extends EclItem {

    public void readFromXhtmlElement(Node xhtmlElement) {
        this.setMvcData(getMvcData());
    }

    @Override
    public MvcData getMvcData() {
        return new MvcDataImpl("Test:Entity:EclFlickr").defaults(MvcDataImpl.Defaults.ENTITY);
    }
}
