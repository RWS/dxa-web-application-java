package com.sdl.dxa.tridion.content;

import com.sdl.web.pca.client.auth.Authentication;
import com.sdl.web.pca.client.exception.UnauthorizedException;
import com.sdl.webapp.common.api.content.StaticContentNotLoadedException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
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
import java.io.InputStream;

@Component
@Profile("!cil.providers.active")
public class BinaryContentDownloader {

    private static final Integer TIMEOUT_IN_MILLIS = 10000;

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
            return new HttpHost(proxyHost, proxyPort != null ? Integer.parseInt(proxyPort) : -1);
        }
        return null;
    }

    public byte[] downloadContent(File file, String downloadUrl) throws StaticContentNotLoadedException {
        int attempt = 3;
        UnauthorizedException[] exception = new UnauthorizedException[1];
        while(true) {
            if (attempt > 0) {
                try {
                    --attempt;
                    return downloadContentInternal(file, downloadUrl);
                }
                catch (UnauthorizedException unauthorizedException) {
                    if (exception[0] == null) {
                        exception[0] = unauthorizedException;
                    }
                    try {
                        Thread.currentThread();
                        Thread.sleep(200L);
                        continue;
                    }
                    catch (InterruptedException var6) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            throw new StaticContentNotLoadedException("Cannot download content for file " + file, exception[0]);
        }
    }

    public byte[] downloadContentInternal(File file, String downloadUrl) throws UnauthorizedException, StaticContentNotLoadedException {
        HttpGet httpGet = new HttpGet(downloadUrl);
        RequestConfig params = RequestConfig.custom().setConnectTimeout(TIMEOUT_IN_MILLIS).setSocketTimeout(TIMEOUT_IN_MILLIS).build();
        httpGet.setConfig(params);
        authentication.applyManualAuthentication(httpGet);
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            InputStream contentStream = response.getEntity().getContent();
            if (response.getStatusLine().getStatusCode() != 200) {
                if (response.getStatusLine().getStatusCode() == 401) {
                    throw new UnauthorizedException("Unable to retrieve requested entity");
                }
                throw new StaticContentNotLoadedException("Unable to retrieve requested entity from " + downloadUrl);
            }
            return IOUtils.toByteArray(contentStream);
        }
        catch (IOException e) {
            throw new StaticContentNotLoadedException("Cannot download content for file " + file, e);
        }
    }
}
