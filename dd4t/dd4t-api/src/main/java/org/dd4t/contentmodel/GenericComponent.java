package org.dd4t.contentmodel;

import org.joda.time.DateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface representing a component where the xml source from the SimpleComponent
 * has been deserialized into content and metadata.
 *
 * @author bjornl
 */
public interface GenericComponent extends Component {

    /**
     * Get the metadata
     *
     * @return a map of field objects representing the metadata
     */
    public Map<String, Field> getMetadata();

    /**
     * Set the metadata
     */
    public void setMetadata(Map<String, Field> metadata);

    /**
     * Get the content
     *
     * @return a map of field objects representing the content
     */
    public Map<String, Field> getContent();

    /**
     * Set the content
     */
    public void setContent(Map<String, Field> content);

    /**
     * Get the component type
     *
     * @return the component type
     */
    public ComponentType getComponentType();

    /**
     * Set the component type
     *
     * @param componentType
     */
    public void setComponentType(ComponentType componentType);

    /**
     * Get the multimedia object
     *
     * @return the multimedia object
     */
    public Multimedia getMultimedia();

    /**
     * Set the multimedia object
     */
    public void setMultimedia(Multimedia multimedia);

    public List<Category> getCategories();

    public void setCategories(List<Category> categories);

    public int getVersion();

    DateTime getLastPublishedDate();

    void setLastPublishedDate(DateTime date);

    DateTime getRevisionDate();

    void setRevisionDate(DateTime date);

    public enum ComponentType {
        Multimedia, Normal
    }
}