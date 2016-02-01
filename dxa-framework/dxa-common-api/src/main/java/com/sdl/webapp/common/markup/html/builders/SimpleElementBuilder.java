package com.sdl.webapp.common.markup.html.builders;

/**
 * <p>SimpleElementBuilder class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public final class SimpleElementBuilder extends AbstractElementBuilder<SimpleElementBuilder> {

    /**
     * <p>Constructor for SimpleElementBuilder.</p>
     *
     * @param tagName  a {@link java.lang.String} object.
     * @param closeTag a boolean.
     */
    public SimpleElementBuilder(String tagName, boolean closeTag) {
        super(tagName, closeTag);
    }
}
