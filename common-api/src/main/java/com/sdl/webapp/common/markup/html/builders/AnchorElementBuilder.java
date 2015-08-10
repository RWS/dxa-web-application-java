package com.sdl.webapp.common.markup.html.builders;

public final class AnchorElementBuilder extends AbstractElementBuilder<AnchorElementBuilder> {

    public AnchorElementBuilder() {
        super("a", true);
    }

    public AnchorElementBuilder withHref(String href) {
        return withAttribute("href", href);
    }

    public AnchorElementBuilder withTitle(String title) {
        return withAttributeIfNotEmpty("title", title);
    }
}
