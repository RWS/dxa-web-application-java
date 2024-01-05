package com.sdl.webapp.tridion.xpm.filter;

import com.sdl.web.preview.model.PreviewSession;
import com.sdl.web.preview.util.SessionHandlingException;
import com.tridion.data.BinaryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Incorporated this class from the udp library in order to update it with the Jakarta Servlet API.
 * Base class for BinaryContentFilters.
 */
public abstract class BinaryContentFilter extends ContentFilter {

    private static final Logger LOG = LoggerFactory.getLogger(BinaryContentFilter.class);

    protected abstract BinaryData fetchBinaryData(String url);

    public final String handleRequest(final String fullURL, final String realPath, final String rootPath)
            throws IOException {
        LOG.trace("handleRequest, fullURL={}, realPath={}, rootPath={}", fullURL, realPath, rootPath);

        String realFileName = null;
        PreviewSession session = obtainPreviewSession();

        if (session != null) {
            BinaryData data = fetchBinaryData(fullURL);
            if (data != null && data.getDataSize() > 0) {
                LOG.debug("Found binary content that has {} bytes length!", data.getDataSize());

                try {
                    realFileName = handleBinaryData(session, data, realPath, rootPath);

                }
                catch (SessionHandlingException | IOException e) {
                    LOG.error("An exception occurred while processing request", e);
                    realFileName = null;
                }
            }

        }
        return realFileName;
    }

    protected abstract String handleBinaryData(PreviewSession session, BinaryData data, String realPath,
                                               String rootPath) throws SessionHandlingException, IOException;
}
