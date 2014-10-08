package com.sdl.tridion.referenceimpl.common;

import com.sdl.tridion.referenceimpl.common.config.WebRequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

public abstract class BaseStaticFileManager implements StaticFileManager {

    @Autowired
    private WebRequestContext webRequestContext;

    @Override
    public boolean getStaticContent(String url, File destinationFile) throws IOException {
        return getStaticContent(url, destinationFile, webRequestContext.getPublicationId());
    }
}
