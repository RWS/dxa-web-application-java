package com.sdl.webapp.common.api.xpm;

public interface OccurrenceConstraint {

    /**
     * <p>getMinOccurs.</p>
     *
     * @return a {@link int} object.
     */
    int getMinOccurs();

    /**
     * <p>setMinOccurs.</p>
     *
     * @param minOccurs a {@link int} object.
     */
    void setMinOccurs(int minOccurs);

    /**
     * <p>getMaxOccurs.</p>
     *
     * @return a {@link int} object.
     */
    int getMaxOccurs();

    /***
     * <p>setMaxOccurs</p>
     * @param maxOccurs
     */
    void setMaxOccurs(int maxOccurs);

}
