package com.sdl.webapp.common.api;

import java.io.IOException;
import java.io.InputStream;

public interface StaticContentItem {

    long getLastModified();

    InputStream getContent() throws IOException;
}
