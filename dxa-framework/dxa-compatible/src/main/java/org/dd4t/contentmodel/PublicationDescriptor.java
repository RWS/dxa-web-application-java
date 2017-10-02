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
 * dd4t-2
 *
 * @author R. Kempees
 */
public interface PublicationDescriptor {
    int getId ();

    void setId (int id);

    String getKey ();

    void setKey (String key);

    String getTitle ();

    void setTitle (String title);

    String getMultimediaPath ();

    void setMultimediaPath (String multimediaPath);

    String getMultimediaUrl ();

    void setMultimediaUrl (String multimediaUrl);

    String getPublicationUrl ();

    void setPublicationUrl (String publicationUrl);

    String getPublicationPath ();

    void setPublicationPath (String publicationPath);
}
