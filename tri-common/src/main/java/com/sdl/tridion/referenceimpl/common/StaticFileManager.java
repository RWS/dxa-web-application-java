package com.sdl.tridion.referenceimpl.common;

import java.io.File;
import java.io.IOException;

/**
 * TODO: Documentation.
 */
public interface StaticFileManager {

    String createStaticAssets(File baseDirectory) throws IOException;

    boolean getStaticContent(String url, File destinationFile) throws IOException;
}
