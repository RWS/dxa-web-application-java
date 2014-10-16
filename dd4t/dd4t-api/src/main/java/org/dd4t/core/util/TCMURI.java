package org.dd4t.core.util;

import java.io.Serializable;
import java.text.ParseException;
import java.util.StringTokenizer;

/**
 * Created by rai on 25.05.2014
 */
public class TCMURI implements Comparable<TCMURI>, Serializable {

    public static final String URI_NAMESPACE = "tcm:";
    public static final TCMURI NULL_URI = new TCMURI(0, 0, 0, 0);
    protected static final String SEPARATOR = "-";
    protected static final String DELIM_VERSION = "v";

    private int itemType;
    private int itemId;
    private int pubId;
    private int version;

    public TCMURI() {
    }

    public TCMURI(String uri) throws ParseException {
        this.itemType = 0;
        this.itemId = -1;
        this.pubId = -1;
        this.version = -1;
        load(uri);
    }

    public TCMURI(int publicationId, int itemId, int itemType, int version) {
        this.itemType = itemType;
        this.itemId = itemId;
        this.pubId = publicationId;
        this.version = version;
    }

    public static boolean isValid(String tcmUri) {
        return tcmUri != null && tcmUri.startsWith(URI_NAMESPACE);
    }

    protected void load(String uriString) throws ParseException {
        if (uriString != null) {
            int namespaceLength = URI_NAMESPACE.length();
            int uriStringLength = uriString.length();
            int currentPosition = uriStringLength;
            if ((uriStringLength < namespaceLength) || (!uriString.startsWith(URI_NAMESPACE))) {
                throw new ParseException("URI string " + uriString + " does not start with " + URI_NAMESPACE, currentPosition);
            }

            String uri = uriString.substring(namespaceLength, uriStringLength);
            StringTokenizer st = new StringTokenizer(uri, "-");
            if (st.countTokens() < 2) {
                throw new ParseException("URI string " + uriString + " does not contain enough ID's", currentPosition);
            }
            try {
                String token = st.nextToken();
                currentPosition += token.length();
                this.pubId = Integer.parseInt(token);

                token = st.nextToken();
                currentPosition += token.length();
                this.itemId = Integer.parseInt(token);

                if (!st.hasMoreTokens()) {
                    this.itemType = 16;
                } else {
                    token = st.nextToken();
                    currentPosition += token.length();
                    if (!token.startsWith(DELIM_VERSION)) {
                        this.itemType = Integer.parseInt(token);
                    } else {
                        this.version = Integer.parseInt(token.substring(1, token.length()));
                        this.itemType = 16;
                    }

                    if (st.hasMoreTokens()) {
                        token = st.nextToken();
                        currentPosition += token.length();
                        this.version = Integer.parseInt(token.substring(1, token.length()));
                    }
                }
            } catch (NumberFormatException e) {
                throw new ParseException("Invalid ID (not an integer) in URI string " + uriString, currentPosition);
            }
        } else {
            throw new ParseException("Invalid TCMURI String, string cannot be null", 0);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
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

    public void setItemType(int itemType) {
        if ((this == NULL_URI) && (itemType != 0)) {
            throw new IllegalStateException("Changing the item type of the NULL_URI is not allowed!!!");
        }

        this.itemType = itemType;
    }

    public int getItemId() {
        return this.itemId;
    }

    public void setItemId(int itemId) {
        if ((this == NULL_URI) && (itemId != 0)) {
            throw new IllegalStateException("Changing the item id of the NULL_URI is not allowed!!!");
        }

        this.itemId = itemId;
    }

    public int getPublicationId() {
        return this.pubId;
    }

    public void setPublicationId(int pubId) {
        if ((this == NULL_URI) && (pubId != 0)) {
            throw new IllegalStateException("Changing the publication id of the NULL_URI is not allowed!!!");
        }
        this.pubId = pubId;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        if ((this == NULL_URI) && (version != 0)) {
            throw new IllegalStateException("Changing the version of the NULL_URI is not allowed!!!");
        }

        this.version = version;
    }

    private boolean uriIsEqual(String uri) {
        TCMURI otherURI = null;
        try {
            otherURI = new TCMURI(uri);
        } catch (ParseException e) {
            return false;
        }
        return uriIsEqual(otherURI);
    }

    private boolean uriIsEqual(TCMURI uri) {
        if (getItemType() != uri.getItemType()) {
            return false;
        }
        if (getItemId() != uri.getItemId()) {
            return false;
        }
        if (getPublicationId() != uri.getPublicationId()) {
            return false;
        }

        return true;
    }

    public boolean equals(Object object) {
        if ((object instanceof TCMURI)) {
            return uriIsEqual((TCMURI) object);
        }
        if ((object instanceof String)) {
            return uriIsEqual((String) object);
        }

        return false;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p/>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p/>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p/>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p/>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.uriIsEqual(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with uriIsEqual."
     * <p/>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(TCMURI o) {
        if (o == null) {
            return 1;
        }

        if (uriIsEqual(o)) {
            return 0;
        }

        return getItemId() - o.getItemId();
    }
}
