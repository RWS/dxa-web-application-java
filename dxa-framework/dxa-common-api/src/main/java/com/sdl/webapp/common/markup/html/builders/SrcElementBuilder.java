package com.sdl.webapp.common.markup.html.builders;

/**
 * <p>SrcElementBuilder class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class SrcElementBuilder<B extends AbstractElementBuilder<B>> extends AbstractElementBuilder<B> {
    /**
     * <p>Constructor for SrcElementBuilder.</p>
     *
     * @param tagName  a {@link java.lang.String} object.
     * @param closeTag a boolean.
     */
    public SrcElementBuilder(String tagName, boolean closeTag) {
        super(tagName, closeTag);
    }

    /**
     * <p>withSrc.</p>
     *
     * @param src a {@link java.lang.String} object.
     * @return a B object.
     */
    public B withSrc(String src) {
        return withAttribute("src", src);
    }
}
