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

package org.dd4t.core.util;

import java.io.Serializable;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TCMURI implements Serializable {

    public static final String URI_NAMESPACE = "tcm:";
    protected static final String SEPARATOR = "-";

    protected int itemType;
    protected int itemId;
    protected int pubId;
    protected int version;

    @Deprecated
    public TCMURI() {
    }

    @Deprecated
    public TCMURI(String uri) throws ParseException {
        this(new Builder().with(uri));
    }

    //added because of custom code, next release can be deleted?
    @Deprecated
    public TCMURI(String uri, int version) throws ParseException {
        this(new Builder()
                .with(uri)
                .version(version));
    }

    //make it private instead of delete on next release
    @Deprecated
    public TCMURI(int publicationId, int itemId, int itemType, int version) {
        this(new Builder()
                .publicationId(publicationId)
                .itemId(itemId)
                .itemType(itemType)
                .version(version));
    }

    public TCMURI(Builder builder) {
        this.itemType = builder.itemType;
        this.itemId = builder.itemId;
        this.pubId = builder.pubId;
        this.version = builder.version;
    }

    public static boolean isValid(String tcmUri) {
        return tcmUri != null && tcmUri.startsWith(URI_NAMESPACE);
    }

    @Deprecated
    protected void load(String uriString) throws ParseException {
        Builder builder = new Builder().with(uriString);
        this.itemType = builder.itemType;
        this.itemId = builder.itemId;
        this.pubId = builder.pubId;
        this.version = builder.version;
    }

    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(URI_NAMESPACE);
        sb.append(this.pubId);
        sb.append(SEPARATOR);
        sb.append(this.itemId);
        sb.append(SEPARATOR);
        sb.append(this.itemType);
        return sb.toString();
    }

    public int getItemType() {
        return this.itemType;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getPublicationId() {
        return this.pubId;
    }

    public int getVersion() {
        return this.version;
    }

    public static class Builder {
        private int pubId;
        private int itemId;
        private int itemType;
        private int version = -1;

        public Builder publicationId(int pubId) {
            this.pubId = pubId;
            return this;
        }

        public Builder itemId(int itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder itemType(int itemType) {
            this.itemType = itemType;
            return this;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        private void validatePatternOf(String uri) {
            if (uri == null) {
                throw new IllegalArgumentException("Invalid TCMURI String, string cannot be null");
            }

            if (!uri.startsWith(URI_NAMESPACE)) {
                throw new IllegalArgumentException(String.format("URI string %s does not start with %s",
                        uri, URI_NAMESPACE));
            }
        }

        private void extractItemsFrom(String uri) {
            Pattern p = Pattern.compile("^tcm:(?<pubId>\\d+)-(?<itemId>\\d+)(-(?<itemType>\\d+))*(-v(?<version>\\d+))*$");
            Matcher m = p.matcher(uri);

            if (!m.find()) {
                throw new IllegalArgumentException(String.format("URI %s does not match the pattern", uri));
            }

            this.pubId = Integer.parseInt(m.group("pubId"));
            this.itemId = Integer.parseInt(m.group("itemId"));
            this.itemType = m.group("itemType") == null ? 16 : Integer.parseInt(m.group("itemType"));
            this.version = m.group("version") == null ? -1 : Integer.parseInt(m.group("version"));
        }

        public Builder with(String uri) throws ParseException {
            try {
                validatePatternOf(uri);
                extractItemsFrom(uri);
            } catch (IllegalArgumentException iae) {
                throw new ParseException(iae.getMessage(), 0);
            }

            return this;
        }

        public TCMURI create() {
            return new TCMURI(pubId, itemId, itemType, version);
        }
    }
}
