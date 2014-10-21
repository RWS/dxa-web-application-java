package org.dd4t.contentmodel;

import java.io.InputStream;

/**
 * Created by rai on 02/06/14.
 */
public interface BinaryData {

    public byte[] getBytes();

    public InputStream getInputStream();

    public long getDataSize();
}
