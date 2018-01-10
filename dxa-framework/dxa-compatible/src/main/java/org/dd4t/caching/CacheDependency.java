package org.dd4t.caching;

import java.io.Serializable;

/**
 * Interface determines the necessary data to act as a cache dependency
 *
 * @author Rogier Oudshoorn
 */
public interface CacheDependency extends Serializable {

    int getPublicationId();

    int getItemId();
}
