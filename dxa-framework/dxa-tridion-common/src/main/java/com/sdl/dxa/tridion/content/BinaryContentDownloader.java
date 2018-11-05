package com.sdl.dxa.tridion.content;

import com.sdl.webapp.common.api.content.StaticContentNotLoadedException;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class BinaryContentDownloader {

    private final CloseableHttpClient httpclient;

    public BinaryContentDownloader() {
        httpclient = HttpClients.createDefault();
    }

    public byte[] downloadContent(File file, String downloadUrl) throws StaticContentNotLoadedException {
        HttpGet httpget = new HttpGet(downloadUrl);
        try(CloseableHttpResponse response = httpclient.execute(httpget)) {
            return IOUtils.toByteArray(response.getEntity().getContent());
        } catch (IOException e) {
            throw new StaticContentNotLoadedException("Cannot content for file " + file, e);
        }
    }
}
