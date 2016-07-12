package com.sdl.webapp.common.api.model;

import lombok.Getter;

import java.util.StringTokenizer;

@Getter
public class EclUrl {

    private int publicationId;
    private String mountpointId;
    private String itemId;
    private String type;

    /**
     * <p>Constructor for EclUrl.</p>
     *
     * @param eclUrl a {@link java.lang.String} object.
     */
    public EclUrl(String eclUrl) {
        StringTokenizer tokenizer = new StringTokenizer(eclUrl, ":-");
        tokenizer.nextToken();
        this.publicationId = Integer.parseInt(tokenizer.nextToken());
        this.mountpointId = tokenizer.nextToken();
        this.itemId = tokenizer.nextToken();
        this.type = tokenizer.nextToken();
    }
}
