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

/**
 * Interface for a binary items i.e. images and pdfs.
 * <p/>
 * Bear in mind that this interface is only valid for fetching actual
 * binaries through the BinaryFactory and a Binary Controller.
 * <p/>
 * This class is NOT for deserializing Multimedia Components!
 *
 * @author bjornl
 * @see org.dd4t.contentmodel.Multimedia
 */
public interface Binary extends RepositoryLocalItem {

    void setBinaryData (BinaryData binaryData);

    BinaryData getBinaryData ();

    void setMimeType (String mimeType);

    String getMimeType ();

    void setUrlPath (String urlPath);

    String getUrlPath ();

}
