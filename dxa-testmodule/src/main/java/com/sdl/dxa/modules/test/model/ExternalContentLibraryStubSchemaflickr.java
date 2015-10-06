package com.sdl.dxa.modules.test.model;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.entity.EclItem;
import com.sdl.webapp.common.api.model.entity.MediaItemMvcData;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import org.w3c.dom.Node;

public class ExternalContentLibraryStubSchemaflickr extends EclItem {


    @Override
    public String toHtml(String widthFactor) {
        // TODO implement this functionality
        throw new UnsupportedOperationException("This should be implemented in a subclass of EclItem");
    }

    @Override
    public String toHtml(String widthFactor, double aspect, String cssClass,
                         int containerSize) {
        // TODO implement this functionality
        throw new UnsupportedOperationException("This should be implemented in a subclass of EclItem");
    }

    @Override
    public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) {
        // TODO implement this functionality
        throw new UnsupportedOperationException("This should be implemented in a subclass of EclItem");
    }

    public void readFromXhtmlElement(Node xhtmlElement) {
        try {
            this.setMvcData(new MediaItemMvcData("Test:Entity:EclFlickr"));
        } catch (DxaException e) {

        }
    }


}
