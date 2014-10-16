package org.dd4t.contentmodel;

/**
 * Interface for items which have multimedia
 *
 * @author bjornl
 */
public interface HasMultimedia {
    /**
     * Get the multimedia.
     *
     * @return the multimedia.
     */
    public Multimedia getMultimedia();

    /**
     * Set the multimedia
     *
     * @param multimedia
     */
    public void setMultimedia(Multimedia multimedia);
}
