package com.sdl.webapp.tridion.api;

import com.sdl.webapp.common.api.MediaHelper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@Primary
public class CidHostsNameProvider implements MediaHelper.ResponsiveMediaUrlBuilder.HostsNamesProvider {

    @Autowired
    private HttpServletRequest servletRequest;

    private String hostname, cidHostname;

    @Override
    @SneakyThrows(UnknownHostException.class)
    public String getHostname() {
        if (hostname == null) {
            hostname = InetAddress.getLocalHost().getCanonicalHostName() + ':' + servletRequest.getServerPort();
        }
        return hostname;
    }

    @Override
    public String getCidHostname() {
        if (cidHostname == null) {
            cidHostname = servletRequest.getServletContext().getInitParameter("cidServiceUrl");
        }
        return cidHostname;
    }
}
