package org.dd4t.contentmodel;

import org.dd4t.core.util.TCMURI;

import java.util.List;

/**
 * Interface for Keyword item
 *
 * @author Quirijn Slings, Mihai Cadariu
 */
public interface Keyword extends Item, HasMetadata {

    public String getDescription();

    public String getKey();

    public List<Keyword> getChildKeywords();

    public boolean hasChildren();

    public List<Keyword> getParentKeywords();

    public boolean hasParents();

    public List<TCMURI> getRelatedKeywords();

    public boolean hasRelatedKeywords();

    public String getPath();

    public void setPath(String path);

    public List<TCMURI> getClassifiedItems();

    public boolean hasClassifiedItems();

    public String getTaxonomyId();

    public void setTaxonomyId(String taxonomyId);
}
