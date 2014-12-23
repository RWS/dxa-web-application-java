package org.dd4t.core.services.impl;

import org.dd4t.contentmodel.Keyword;
import org.dd4t.core.services.TaxonomyService;
import org.dd4t.core.util.TCMURI;

import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Service class that wraps around a Taxonomy Keyword and offers utility methods for retrieving an entire Taxonomy
 * (as Keyword) or individual Keywords identified by their name, key, desciption or TCMURI.
 *
 * @author Mihai Cadariu
 * @since 18.06.2014
 */
public class TaxonomyServiceImpl implements TaxonomyService {

    private final Keyword taxonomy;
    private final Map<String, Keyword> keywordsIdCache;

    public TaxonomyServiceImpl(Keyword taxonomy) {
        this.taxonomy = taxonomy;
        keywordsIdCache = new TreeMap<>();
    }

    /**
     * Returns the root Keyword of Taxonomy.
     *
     * @return Keyword the root node of the Taxonomy
     */
    @Override
    public Keyword getTaxonomy() {
        return taxonomy;
    }

    /**
     * Retrieves a resolved Keyword object that was used to classify the item with the given TCMURI.
     *
     * @param tcmuri String representing the TCMURI of the item that was classified with the Keyword we are looking for
     * @return Keyword object with its parent/children and metadata resolved
     */
    @Override
    public Keyword findKeywordByClassifiedId(final TCMURI tcmuri) {
        return find(taxonomy, new KeywordComparator() {
            @Override
            public boolean compare(Keyword keyword) {
                return keyword.getClassifiedItems().contains(tcmuri);
            }
        });
    }

    /**
     * Retrieves a resolved Keyword object corresponding to the given description.
     *
     * @param description String representing the descrption of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    @Override
    public Keyword findKeywordByDescription(final String description) {
        return find(taxonomy, new KeywordComparator() {
            @Override
            public boolean compare(Keyword keyword) {
                return description.equals(keyword.getDescription());
            }
        });
    }

    /**
     * Retrieves a resolved Keyword object corresponding to the given TCMURI.
     *
     * @param tcmuri String representing the TCMURI of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    @Override
    public Keyword findKeywordById(final String tcmuri) {
        Keyword result = keywordsIdCache.get(tcmuri);

        if (result == null) {
            result = find(taxonomy, new KeywordComparator() {
                @Override
                public boolean compare(Keyword keyword) {
                    return tcmuri.equals(keyword.getId());
                }
            });

            keywordsIdCache.put(tcmuri, result);
        }

        return result;
    }

    /**
     * Retrieves a resolved Keyword object corresponding to the given TCMURI item id.
     *
     * @param itemId String representing the TCMURI item id of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    @Override
    public Keyword findKeywordByItemId(final int itemId) {
        return find(taxonomy, new KeywordComparator() {
            @Override
            public boolean compare(Keyword keyword) {
                try {
                    return itemId == new TCMURI(keyword.getId()).getItemId();
                } catch (ParseException e) {
                    return false;
                }
            }
        });
    }

    /**
     * Retrieves a resolved Keyword object corresponding to the given key.
     *
     * @param key String representing the key of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    @Override
    public Keyword findKeywordByKey(final String key) {
        return find(taxonomy, new KeywordComparator() {
            @Override
            public boolean compare(Keyword keyword) {
                return key.equals(keyword.getKey());
            }
        });
    }

    /**
     * Retrieves a resolved Keyword object corresponding to the given name.
     *
     * @param name String representing the name of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    @Override
    public Keyword findKeywordByName(final String name) {
        return find(taxonomy, new KeywordComparator() {
            @Override
            public boolean compare(Keyword keyword) {
                return name.equals(keyword.getTitle());
            }
        });
    }

    /**
     * Retrieves a resolved Keyword object corresponding to the given path.
     *
     * @param path String representing the path of the Keyword to look for
     * @return Keyword object with its parent/children and metadata resolved
     */
    @Override
    public Keyword findKeywordByPath(final String path) {
        return find(taxonomy, new KeywordComparator() {
            @Override
            public boolean compare(Keyword keyword) {
                return path.equals(keyword.getPath());
            }
        });
    }

    /*
    Recursive utility method that traverses the given Keyword in a depth-first manner searching for a particular Keyword
     */
    private Keyword find(Keyword keyword, KeywordComparator comparator) {
        if (comparator.compare(keyword)) {
            return keyword;
        }

        for (Keyword child : keyword.getChildKeywords()) {
            Keyword result = find(child, comparator);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    /*
    Utility interface for the Command pattern used for sending inline comparators between keywords
     */
    private interface KeywordComparator {
        public boolean compare(Keyword keyword);
    }
}
