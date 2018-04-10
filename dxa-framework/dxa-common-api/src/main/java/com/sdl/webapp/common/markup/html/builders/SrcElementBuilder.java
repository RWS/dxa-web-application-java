package com.sdl.webapp.common.markup.html.builders;

/**
 * @dxa.publicApi
 */
public class SrcElementBuilder<B extends AbstractElementBuilder<B>> extends AbstractElementBuilder<B> {

    public SrcElementBuilder(String tagName, boolean closeTag) {
        super(tagName, closeTag);
    }

    public B withSrc(String src) {
        return withAttribute("src", src);
    }
}
