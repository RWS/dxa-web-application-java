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

import org.dd4t.contentmodel.Binary;
import org.dd4t.contentmodel.BinaryData;

public class BinaryImpl extends BaseRepositoryLocalItem implements Binary {

    private BinaryData binaryData;
    private String mimeType;
    private String urlPath;

    @Override
    public void setBinaryData (final BinaryData binaryData) {
        this.binaryData = binaryData;
    }

    @Override
    public BinaryData getBinaryData () {
        return this.binaryData;
    }

    @Override
    public void setMimeType (final String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String getMimeType () {
        return this.mimeType;
    }

    @Override
    public void setUrlPath (final String urlPath) {
        this.urlPath = urlPath;
    }

    @Override
    public String getUrlPath () {
        return this.urlPath;
    }
}