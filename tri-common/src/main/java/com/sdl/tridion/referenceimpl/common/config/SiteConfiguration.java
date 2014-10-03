package com.sdl.tridion.referenceimpl.common.config;

import org.springframework.stereotype.Component;

@Component
public class SiteConfiguration {

    public String getSystemDir() {
        return "/system";
    }

    public String getStaticDir() {
        return "/BinaryData";
    }
}
