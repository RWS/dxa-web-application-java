package com.sdl.webapp.common.markup.html.builders;

/**
 * @dxa.publicApi
 */
public final class ButtonElementBuilder extends AbstractElementBuilder<ButtonElementBuilder> {

    public ButtonElementBuilder() {
        super("button", true);
    }

    public ButtonElementBuilder ofType(String type) {
        return withAttribute("type", type);
    }
}
