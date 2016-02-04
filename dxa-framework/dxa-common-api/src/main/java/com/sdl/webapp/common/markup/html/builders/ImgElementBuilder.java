package com.sdl.webapp.common.markup.html.builders;

/**
 * <p>ImgElementBuilder class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public final class ImgElementBuilder extends SrcElementBuilder<ImgElementBuilder> {

    /**
     * <p>Constructor for ImgElementBuilder.</p>
     */
    public ImgElementBuilder() {
        super("img", false);
    }

    /**
     * <p>withAlt.</p>
     *
     * @param alt a {@link java.lang.String} object.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.ImgElementBuilder} object.
     */
    public ImgElementBuilder withAlt(String alt) {
        return withAttributeIfNotEmpty("alt", alt);
    }

    /**
     * <p>withWidth.</p>
     *
     * @param width a {@link java.lang.String} object.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.ImgElementBuilder} object.
     */
    public ImgElementBuilder withWidth(String width) {
        return withAttributeIfNotEmpty("width", width);
    }

    /**
     * <p>withWidth.</p>
     *
     * @param width a int.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.ImgElementBuilder} object.
     */
    public ImgElementBuilder withWidth(int width) {
        return withWidth(Integer.toString(width));
    }

    /**
     * <p>withHeight.</p>
     *
     * @param height a {@link java.lang.String} object.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.ImgElementBuilder} object.
     */
    public ImgElementBuilder withHeight(String height) {
        return withAttributeIfNotEmpty("height", height);
    }

    /**
     * <p>withHeight.</p>
     *
     * @param height a int.
     * @return a {@link com.sdl.webapp.common.markup.html.builders.ImgElementBuilder} object.
     */
    public ImgElementBuilder withHeight(int height) {
        return withHeight(Integer.toString(height));
    }
}
