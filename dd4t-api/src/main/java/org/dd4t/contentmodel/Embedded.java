package org.dd4t.contentmodel;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public interface Embedded {
    Schema getEmbeddedSchema ();

    void setEmbeddedSchema (final Schema embeddedSchema);
}
