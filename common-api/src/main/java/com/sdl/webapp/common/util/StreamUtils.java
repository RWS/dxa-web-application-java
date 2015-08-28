package com.sdl.webapp.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities for working with I/O streams.
 */
public final class StreamUtils {

    private static final int BUFFER_SIZE = 8192;

    private StreamUtils() {
    }

    /**
     * Copies all dat from an {@code InputStream} to an {@code OutputStream}.
     *
     * @param in The {@code InputStream} to read from.
     * @param out The {@code OutputStream} to write to.
     * @throws IOException When an I/O error occurs.
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        final byte[] buffer = new byte[BUFFER_SIZE];
        int count;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
    }
}
