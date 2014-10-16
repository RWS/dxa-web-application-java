package org.dd4t.contentmodel;

public interface ComponentPresentation {

    /**
     * Get the component
     *
     * @return
     */
    public GenericComponent getComponent();

    /**
     * Set the component
     *
     * @param component
     */
    public void setComponent(GenericComponent component);

    /**
     * Get the component template
     *
     * @return
     */
    public ComponentTemplate getComponentTemplate();

    /**
     * Set the component template
     *
     * @param componentTemplate
     */
    public void setComponentTemplate(ComponentTemplate componentTemplate);

    /**
     * Get the rendered content
     */
    public String getRenderedContent();

    /**
     * Set the rendered content
     *
     * @param renderedContent
     */
    public void setRenderedContent(String renderedContent);

    /**
     * Return true if the component presentation is dynamic (i.e. available in the broker database as a separate item)
     *
     * @return
     */
    public boolean isDynamic();
}
