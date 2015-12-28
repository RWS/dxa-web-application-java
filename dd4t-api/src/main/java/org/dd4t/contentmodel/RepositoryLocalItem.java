/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.contentmodel;

import org.joda.time.DateTime;

/**
 * Interface for all items except for publications
 *
 * @author bjornl
 */
public interface RepositoryLocalItem extends Item {

    /**
     * Get the organizational item
     *
     * @return
     */
    OrganizationalItem getOrganizationalItem ();

    /**
     * Set the organizational item
     *
     * @param organizationalItem
     */
    void setOrganizationalItem (OrganizationalItem organizationalItem);

    /**
     * Get the publication
     *
     * @return
     */
    Publication getOwningPublication ();

    /**
     * Set the owning publication
     *
     * @param publication
     */
    void setOwningPublication (Publication publication);

    /**
     * Get the owning publication
     *
     * @return
     */
    Publication getPublication ();

    /**
     * Set the publication
     *
     * @param publication
     */
    void setPublication (Publication publication);

    void setLastPublishedDate (DateTime date);

    DateTime getLastPublishedDate ();

    /**
     * Get the Schema
     *
     * @return
     */
    Schema getSchema ();
}
