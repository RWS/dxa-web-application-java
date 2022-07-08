package com.sdl.dxa.tridion.content;

import com.sdl.web.pca.client.auth.Authentication;
import com.sdl.webapp.common.api.content.StaticContentNotLoadedException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Profile("!cil.providers.active")
public class BinaryContentDownloader {

    private final CloseableHttpClient httpclient;

    @Autowired
    private Authentication authentication;

    public BinaryContentDownloader() {
        HttpHost proxy = createProxy();
        if (proxy != null) {
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            httpclient = HttpClients.custom().setRoutePlanner(routePlanner).build();
        }
        else {
            httpclient = HttpClients.createDefault();
        }
    }

    public HttpHost createProxy() {
        String proxyHost = System.getProperty("http.proxyHost");
        if (proxyHost != null) {
            String proxyPort = System.getProperty("http.proxyPort");
            return new HttpHost(
                    proxyHost,
                    proxyPort != null ? Integer.parseInt(proxyPort) : -1,
                    "http");
        }
        return null;
    }

    public byte[] downloadContent(File file, String downloadUrl) throws StaticContentNotLoadedException {
        HttpGet httpget = new HttpGet(downloadUrl);
        authentication.applyManualAuthentication(httpget);
        try (CloseableHttpResponse response = httpclient.execute(httpget)) {
            return IOUtils.toByteArray(response.getEntity().getContent());
        } catch (IOException e) {
            throw new StaticContentNotLoadedException("Cannot download content for file " + file, e);
        }
    }
}
