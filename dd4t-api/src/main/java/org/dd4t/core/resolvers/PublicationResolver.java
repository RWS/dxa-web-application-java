package org.dd4t.core.resolvers;

public interface PublicationResolver {

    /**
     * Gets the Publication TCMURI item id for the current request
     *
     * @return int representing the SDL Tridion Publication item id
     */
    public int getPublicationId();

    /**
     * Gets the Publication Url property as defined in Tridion Publication metadata corresponding to the current request
     *
     * @return String representing the SDL Tridion Publication Url metadata property
     */
    public String getPublicationUrl();

    /**
     * Gets the Images URL property as defined in Tridion Publication metadata corresponding to the current request
     *
     * @return String representing the SDL Tridion Images URL metadata property
     */
    public String getImagesUrl();

    /**
     * Gets the Page URL in the current Publication corresponding to the given generic URL
     *
     * @param url String representing the generic URL (i.e. URL path without PublicationUrl prefix)
     * @return String representing the current Publication URL followed by the given URL
     */
    public String getLocalPageUrl(String url);

    /**
     * Gets the Binary URL in the current Publication corresponding to the given generic URL
     *
     * @param url String representing the generic URL (i.e. URL path without PublicationUrl prefix)
     * @return String representing the current Publication URL followed by the given URL
     */
    public String getLocalBinaryUrl(String url);
}
