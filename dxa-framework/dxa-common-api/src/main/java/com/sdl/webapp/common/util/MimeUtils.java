package com.sdl.webapp.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

/**
 * <p>MimeUtils class.</p>
 */
public final class MimeUtils {
    private static Properties mimes;

    private static final Logger LOG = LoggerFactory.getLogger(MimeUtils.class);

    static {
        mimes = new Properties();
        try {
            ClassLoader classLoader = MimeUtils.class.getClassLoader();
            InputStream mimepropertiesStream = classLoader.getResourceAsStream("dxa.mimetypes.properties");
            mimes.load(mimepropertiesStream);

            //User defined mimetypes can be added to dxa.user.mimetypes.properties
            InputStream usermimepropertiesStream = classLoader.getResourceAsStream("dxa.user.mimetypes.properties");
            if (usermimepropertiesStream != null) {
                mimes.load(usermimepropertiesStream);
            }
        } catch (IOException e) {
            LOG.error("Couldn't load mimetypes.", e);
        }
    }

    private MimeUtils() {
    }

    /**
     * <p>getMimeType.</p>
     *
     * @param url a {@link java.net.URL} object.
     * @return a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    static public String getMimeType(URL url) throws IOException {

        // First try to get MIME type by peeking the input stream
        InputStream is = new BufferedInputStream(url.openStream());
        String mimeType = URLConnection.guessContentTypeFromStream(is);
        is.close();

        // If not found -> fallback using the file suffix
        if (mimeType == null) {
            mimeType = getMimeType(url.getFile());
        }

        return mimeType;
    }

    /**
     * <p>getMimeType returns mime type base on file extension or one of the {@link mimes}'s key</p>
     *
     * @param filename a {@link java.lang.String} object is a key of mime type in {@link mimes}.
     * @return a {@link java.lang.String} object which represents one of {@link mimes} value.
     */
    static public String getMimeType(String filename) {
        String[] parts = filename.toLowerCase().split("\\.");
        if (parts.length == 1) {
            return (String) mimes.get(filename.toLowerCase());
        } else if (parts.length > 1) {
            int i = parts.length - 1;
            String abbr = parts[i];

            // Exception for tar.gz files
            if (abbr.equals("gz") && parts[i - 1].equals("tar")) {
                abbr = String.join(".", parts[i - 1], abbr);
            }

            return (String) mimes.get(abbr);
        }

        return null;
    }
}
