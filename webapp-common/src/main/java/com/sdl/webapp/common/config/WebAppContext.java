package com.sdl.webapp.common.config;

import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Application-scoped component that provides information about the context that the web app is running in.
 */
@Component
public class WebAppContext {

    private static final String STATICS_DIR = "BinaryData";

    private File webAppRootPath;
    private File staticsPath;

    public File getWebAppRootPath() {
        return webAppRootPath;
    }

    public void setWebAppRootPath(File webAppRootPath) {
        this.webAppRootPath = webAppRootPath;
        this.staticsPath = new File(webAppRootPath, STATICS_DIR);
    }

    public File getStaticsPath() {
        return staticsPath;
    }
}
