package org.dd4t.contentmodel.impl;

import org.dd4t.contentmodel.BinaryData;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by rai on 02/06/14.
 */
public class BinaryDataImpl implements BinaryData {

    private byte[] bytes;

    public byte[] getBytes() {
        return this.bytes.clone();
    }

    public void setBytes(final byte[] bytes) {
        this.bytes = bytes.clone();
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public long getDataSize() {
        return bytes == null ? 0 : bytes.length;
    }
}
