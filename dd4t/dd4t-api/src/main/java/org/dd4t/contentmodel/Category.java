package org.dd4t.contentmodel;

import java.util.List;

/**
 * Interface for Category item
 *
 * @author Quirijn Slings
 */
public interface Category extends Item {
    public List<Keyword> getKeywords();

    public void setKeywords(List<Keyword> keywords);
}
