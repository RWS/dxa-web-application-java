package com.sdl.webapp.tridion.xpm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.xpm.OccurrenceConstraint;

public class OccurrenceConstraintImpl implements OccurrenceConstraint {

    @JsonProperty(value = "MinOccurs", required = true)
    private int minOccurs;

    @JsonProperty(value = "MaxOccurs", required = true)
    private int maxOccurs;

    /** {@inheritDoc} */
    @Override
    public int getMinOccurs() {
        return minOccurs;
    }

    /** {@inheritDoc} */
    @Override
    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    /** {@inheritDoc} */
    @Override
    public int getMaxOccurs() {
        return maxOccurs;
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }
}
