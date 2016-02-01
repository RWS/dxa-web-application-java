package com.sdl.webapp.common.markup.html.builders;

/**
 * <p>HtmlBuilders class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public final class HtmlBuilders {

    private HtmlBuilders() {
    }

    /**
     * <p>element.</p>
     *
     * @param tagName  a {@link java.lang.String} object.
     * @param closeTag a boolean.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder} object.
     */
    public static SimpleElementBuilder element(String tagName, boolean closeTag) {
        return new SimpleElementBuilder(tagName, closeTag);
    }

    /**
     * <p>element.</p>
     *
     * @param tagName a {@link java.lang.String} object.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder} object.
     */
    public static SimpleElementBuilder element(String tagName) {
        return element(tagName, true);
    }

    /**
     * <p>div.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder} object.
     */
    public static SimpleElementBuilder div() {
        return element("div");
    }

    /**
     * <p>span.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder} object.
     */
    public static SimpleElementBuilder span() {
        return element("span");
    }

    /**
     * <p>small.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder} object.
     */
    public static SimpleElementBuilder small() {
        return element("small");
    }

    /**
     * <p>empty.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder} object.
     */
    public static SimpleElementBuilder empty() {
        return element("");
    }

    /**
     * <p>i.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder} object.
     */
    public static SimpleElementBuilder i() {
        return element("i");
    }

    /**
     * <p>iframe.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder} object.
     */
    public static SimpleElementBuilder iframe() {
        return element("iframe");
    }

    /**
     * <p>a.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.builders.AnchorElementBuilder} object.
     */
    public static AnchorElementBuilder a() {
        return new AnchorElementBuilder();
    }

    /**
     * <p>a.</p>
     *
     * @param href a {@link java.lang.String} object.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.AnchorElementBuilder} object.
     */
    public static AnchorElementBuilder a(String href) {
        return a().withHref(href);
    }

    /**
     * <p>button.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.builders.ButtonElementBuilder} object.
     */
    public static ButtonElementBuilder button() {
        return new ButtonElementBuilder();
    }

    /**
     * <p>button.</p>
     *
     * @param type a {@link java.lang.String} object.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.ButtonElementBuilder} object.
     */
    public static ButtonElementBuilder button(String type) {
        return button().ofType(type);
    }

    /**
     * <p>img.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.builders.ImgElementBuilder} object.
     */
    public static ImgElementBuilder img() {
        return new ImgElementBuilder();
    }

    /**
     * <p>img.</p>
     *
     * @param src a {@link java.lang.String} object.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.ImgElementBuilder} object.
     */
    public static ImgElementBuilder img(String src) {
        return img().withSrc(src);
    }

    /**
     * <p>script.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.builders.SrcElementBuilder} object.
     */
    public static SrcElementBuilder script() {
        return new SrcElementBuilder("script", true);
    }
}
