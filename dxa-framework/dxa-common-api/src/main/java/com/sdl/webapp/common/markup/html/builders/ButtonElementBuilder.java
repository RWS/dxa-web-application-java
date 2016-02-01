package com.sdl.webapp.common.markup.html.builders;

/**
 * <p>ButtonElementBuilder class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public final class ButtonElementBuilder extends AbstractElementBuilder<ButtonElementBuilder> {

    /**
     * <p>Constructor for ButtonElementBuilder.</p>
     */
    public ButtonElementBuilder() {
        super("button", true);
    }

    /**
     * <p>ofType.</p>
     *
     * @param type a {@link java.lang.String} object.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.ButtonElementBuilder} object.
     */
    public ButtonElementBuilder ofType(String type) {
        return withAttribute("type", type);
    }
}
