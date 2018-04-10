package com.sdl.webapp.common.api.content;

import java.io.IOException;
import java.io.InputStream;

/**
 * Static content item.
 *
 * @dxa.publicApi
 */
public interface StaticContentItem {

    /**
     * Returns a timestamp which indicates when this static content item was last modified.
     *
     * @return A timestamp which indicates when this static content item was last modified as a number of milliseconds
     * since the epoch (01-01-1970, 00:00:00 UTC).
     */
    long getLastModified();

    /**
     * Returns the MIME type of this static content item.
     *
     * @return The MIME type of this static content item.
     */
    String getContentType();

    /**
     * Returns an {@link java.io.InputStream} from which the content of the static content item can be read. Callers are
     * expected to close the stream.
     *
     * @return An {@link java.io.InputStream} from which the content of the static content item can be read.
     * @throws java.io.IOException When an I/O error occurs while opening the stream.
     */
    InputStream getContent() throws IOException;

    /**
     * Returns whether the file is versioned.
     *
     * @return whether the file is versioned
     */
    boolean isVersioned();
}
