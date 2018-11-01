package com.sdl.webapp.common.api.content;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Static content item.
 *
 * @dxa.publicApi
 */
public class StaticContentItem {

    private String contentType;
    private File contentFile;
    private boolean versioned;

    public StaticContentItem(String contentType,
                             File contentFile,
                             boolean versioned) {
        this.contentType = contentType;
        this.contentFile = contentFile;
        this.versioned = versioned;
    }

    /**
     * Returns a timestamp which indicates when this static content item was last modified.
     *
     * @return A timestamp which indicates when this static content item was last modified as a number of milliseconds
     * since the epoch (01-01-1970, 00:00:00 UTC).
     */
    public long getLastModified() {
        return contentFile.lastModified();
    }

    /**
     * Returns the MIME type of this static content item.
     *
     * @return The MIME type of this static content item.
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * Returns an {@link java.io.InputStream} from which the content of the static content item can be read. Callers are
     * expected to close the stream.
     *
     * @return An {@link java.io.InputStream} from which the content of the static content item can be read.
     * @throws java.io.IOException When an I/O error occurs while opening the stream.
     */
    public InputStream getContent() throws IOException {
        return new BufferedInputStream(new FileInputStream(this.contentFile));
    }

    /**
     * Returns whether the file is versioned.
     *
     * @return whether the file is versioned
     */
    public boolean isVersioned() {
        return this.versioned;
    }
}
