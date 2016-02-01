package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;

/**
 * <p>RichTextFragment interface.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public interface RichTextFragment {
    /**
     * <p>toHtmlElement.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.HtmlElement} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    HtmlElement toHtmlElement() throws DxaException;
}
