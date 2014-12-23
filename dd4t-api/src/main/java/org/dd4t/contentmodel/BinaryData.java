package org.dd4t.contentmodel;

import java.io.InputStream;

/**
 * @author R. Kempees
 */
public interface BinaryData {

    public byte[] getBytes();

    public InputStream getInputStream();

    public long getDataSize();
}
