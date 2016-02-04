package com.sdl.webapp.tridion.api;

import com.sdl.webapp.common.api.MediaHelper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class CidHostsNameProvider implements MediaHelper.ResponsiveMediaUrlBuilder.HostsNamesProvider {
    @Override
    public String getHostname() {
        return "source/site";
    }

    @Override
    public String getCidHostname() {
        return "";
    }
}
