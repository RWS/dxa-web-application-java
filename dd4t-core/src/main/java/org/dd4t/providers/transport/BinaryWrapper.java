package org.dd4t.providers.transport;

import java.io.Serializable;

/**
 * Java bean representing a wrapper for a Binary object and a binary content.
 * <p/>
 * The Binary is JSONed, then GZipped, then Base64 encoded. The binary content is a raw byte array.
 *
 * @author Mihai Cadariu
 */
public class BinaryWrapper implements Serializable {

    private String binary;
    private byte[] content;

    /**
     * Initialization constructor
     *
     * @param binary  String representing a Binary object that has been JSONed, GZipped and Base64 encoded
     * @param content byte array representing the raw content of a binary
     */
    public BinaryWrapper(String binary, byte[] content) {
        this.binary = binary;
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }
}
