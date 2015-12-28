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

package org.dd4t.contentmodel.impl;

import org.dd4t.contentmodel.PublicationDescriptor;

/**
 * dd4t-2
 *
 * @author Q. Slings, R. Kempees
 */
public class PublicationDescriptorImpl implements PublicationDescriptor {
    private int id;
    private String key;
    private String title;
    private String multimediaPath;
    private String multimediaUrl;
    private String publicationUrl;
    private String publicationPath;

    public PublicationDescriptorImpl (final int id, final String key, final String title, final String multimediaPath, final String multimediaUrl, final String publicationUrl, final String publicationPath) {
        this.id = id;
        this.key = key;
        this.title = title;
        this.multimediaPath = multimediaPath;
        this.multimediaUrl = multimediaUrl;
        this.publicationUrl = publicationUrl;
        this.publicationPath = publicationPath;
    }

    public PublicationDescriptorImpl () {

    }

    @Override
    public int getId () {
        return id;
    }

    @Override
    public void setId (final int id) {
        this.id = id;
    }

    @Override
    public String getKey () {
        return key;
    }

    @Override
    public void setKey (final String key) {
        this.key = key;
    }

    @Override
    public String getTitle () {
        return title;
    }

    @Override
    public void setTitle (final String title) {
        this.title = title;
    }

    @Override
    public String getMultimediaPath () {
        return multimediaPath;
    }

    @Override
    public void setMultimediaPath (final String multimediaPath) {
        this.multimediaPath = multimediaPath;
    }

    @Override
    public String getMultimediaUrl () {
        return multimediaUrl;
    }

    @Override
    public void setMultimediaUrl (final String multimediaUrl) {
        this.multimediaUrl = multimediaUrl;
    }

    @Override
    public String getPublicationUrl () {
        return publicationUrl;
    }

    @Override
    public void setPublicationUrl (final String publicationUrl) {
        this.publicationUrl = publicationUrl;
    }

    @Override
    public String getPublicationPath () {
        return publicationPath;
    }

    @Override
    public void setPublicationPath (final String publicationPath) {
        this.publicationPath = publicationPath;
    }

    @Override
    public String toString () {
        return "[Id: " + this.id + ", Key: " + this.key + ", Title: " + this.title + "MM Path: " + this.multimediaPath + "MM URL: " + this.multimediaUrl + "Publication URL: " + this.publicationUrl + "Publication Path" + this.publicationPath + "]";
    }
}
