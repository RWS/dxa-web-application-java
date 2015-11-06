package com.sdl.webapp.common.api.model;

import java.util.StringTokenizer;

/**
 * ECL Url
 *
 * @author nic
 */
public class EclUrl {

    private int publicationId;
    private String mountpointId;
    private String itemId;
    private String type;

    public EclUrl(String eclUrl) {
        StringTokenizer tokenizer = new StringTokenizer(eclUrl, ":-");
        tokenizer.nextToken();
        this.publicationId = Integer.parseInt(tokenizer.nextToken());
        this.mountpointId = tokenizer.nextToken();
        this.itemId = tokenizer.nextToken();
        this.type = tokenizer.nextToken();
    }

    public String getItemId() {
        return itemId;
    }

    public String getMountpointId() {
        return mountpointId;
    }

    public int getPublicationId() {
        return publicationId;
    }

    public String getType() {
        return type;
    }
}
