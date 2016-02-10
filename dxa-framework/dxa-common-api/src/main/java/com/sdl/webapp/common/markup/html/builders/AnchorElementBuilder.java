package com.sdl.webapp.common.markup.html.builders;

/**
 * <p>AnchorElementBuilder class.</p>
 */
public final class AnchorElementBuilder extends AbstractElementBuilder<AnchorElementBuilder> {

    /**
     * <p>Constructor for AnchorElementBuilder.</p>
     */
    public AnchorElementBuilder() {
        super("a", true);
    }

    /**
     * <p>withHref.</p>
     *
     * @param href a {@link java.lang.String} object.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.AnchorElementBuilder} object.
     */
    public AnchorElementBuilder withHref(String href) {
        return withAttribute("href", href);
    }

    /**
     * <p>withTitle.</p>
     *
     * @param title a {@link java.lang.String} object.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.AnchorElementBuilder} object.
     */
    public AnchorElementBuilder withTitle(String title) {
        return withAttributeIfNotEmpty("title", title);
    }
}
