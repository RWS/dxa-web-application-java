package com.sdl.webapp.common.markup.html;

/**
 * @dxa.publicApi
 */
public abstract class HtmlRenderable {

    // Cached HTML string, to avoid rendering multiple times
    private volatile String html;

    public final String toHtml() {
        // NOTE: Double-checked locking idiom to avoid unnecessary synchronization
        // See http://en.wikipedia.org/wiki/Double-checked_locking
        String result = html;
        if (result == null) {
            synchronized (this) {
                result = html;
                if (result == null) {
                    html = result = renderHtml();
                }
            }
        }
        return result;
    }

    public abstract String renderHtml();

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + toHtml();
    }
}
