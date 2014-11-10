package com.sdl.webapp.main.markup.html.builders;

public final class ButtonElementBuilder extends AbstractElementBuilder<ButtonElementBuilder> {

    public ButtonElementBuilder() {
        super("button", true);
    }

    public ButtonElementBuilder ofType(String type) {
        return withAttribute("type", type);
    }
}
