package com.sdl.webapp.common.api.model;

import java.util.StringTokenizer;

/**
 * ECL Url
 *
 * @author nic
 * @version 1.3-SNAPSHOT
 */
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

    /**
     * <p>Getter for the field <code>itemId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * <p>Getter for the field <code>mountpointId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMountpointId() {
        return mountpointId;
    }

    /**
     * <p>Getter for the field <code>publicationId</code>.</p>
     *
     * @return a int.
     */
    public int getPublicationId() {
        return publicationId;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getType() {
        return type;
    }
}
