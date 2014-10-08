package com.sdl.tridion.referenceimpl.common.config;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class SiteConfiguration {

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
