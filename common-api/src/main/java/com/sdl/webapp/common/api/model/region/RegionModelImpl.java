package com.sdl.webapp.common.api.model.region;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl.RegionsPredicate;

import java.util.*;

import com.sdl.webapp.common.api.xpm.ComponentType;
import com.sdl.webapp.common.api.xpm.XpmRegion;
import com.sdl.webapp.common.api.xpm.XpmRegionConfig;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Implementation of {@code Region}.
 */
public class RegionModelImpl implements RegionModel {

    private final String XpmRegionMarkup = "<!-- Start Region: {title: \"%s\", allowedComponentTypes: [%s], minOccurs: %s} -->";
    private final String XpmComponentTypeMarkup = "{schema: \"%s\", template: \"%s\"}";

    private String name;
    private ArrayList<EntityModel> entities = new ArrayList<EntityModel>();
    private Map<String, String> xpmMetadata = new HashMap<>();
    private MvcData mvcData;
    private RegionModelSet regions;

    /*
     * The XPM metadata key used for the ID of the (Include) Page from which the Region originates. Avoid using this in implementation code because it may change in a future release.
     */
    public static final String IncludedFromPageIdXpmMetadataKey = "IncludedFromPageID";

    /*
     * The XPM metadata key used for the title of the (Include) Page from which the Region originates. Avoid using this in implementation code because it may change in a future release.
     */
    public static final String IncludedFromPageTitleXpmMetadataKey = "IncludedFromPageTitle";

    /*
     * The XPM metadata key used for the file name of the (Include) Page from which the Region originates. Avoid using this in implementation code because it may change in a future release.
     */
    public static final String IncludedFromPageFileNameXpmMetadataKey = "IncludedFromPageFileName";

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public RegionModelImpl(String name) throws DxaException {
        if(Strings.isNullOrEmpty(name))
        {
            throw new DxaException("Region must have a non-empty name.");
        }
        this.setName(name);
    }

    public RegionModelImpl(String name, String qualifiedViewName) throws DxaException {
        this(name);
        MvcData data = new SimpleRegionMvcData(qualifiedViewName);
        this.setMvcData(data);
    }

    @Override
    public ArrayList<EntityModel> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<EntityModel> entities) {
        this.entities = entities;
    }

    public void addEntity(EntityModel entity) {
        this.entities.add(entity);
    }

    @Override
    public EntityModel getEntity(String entityId) {
        /*return this.entities.(entityId);*/
        Collection<EntityModel> c = CollectionUtils.select(this.entities, new EntityPredicate(entityId));
        if (!c.isEmpty()) {
            return c.iterator().next();
        }
        return null;
    }

    @Override
    public Map<String, String> getXpmMetadata() {
        return xpmMetadata;
    }

    @Override
    public String getXpmMarkup(Localization localization) {
        XpmRegionConfig xpmRegionConfig = getXpmRegionConfig();
        XpmRegion xpmRegion = xpmRegionConfig.getXpmRegion(this.name, localization);

        if (xpmRegion == null)
        {
            return "";
        }

        List<String> types = new ArrayList<>();

        for(ComponentType ct : xpmRegion.getComponentTypes())
        {
            types.add(String.format(XpmComponentTypeMarkup, ct.getSchemaId(), ct.getTemplateId()));
        }

        // TODO: obtain MinOccurs & MaxOccurs from regions.json
        return String.format(
                XpmRegionMarkup,
                getName(),
                Joiner.on(", ").join(types),
                0);
    }
    private XpmRegionConfig getXpmRegionConfig() {
        return ApplicationContextHolder.getContext().getBean(XpmRegionConfig.class);
    }
    public void setXpmMetadata(Map<String, String> xpmMetadata) {
        this.xpmMetadata = ImmutableMap.copyOf(xpmMetadata);
    }

    @Override
    public MvcData getMvcData() {
        return mvcData;
    }

    public void setMvcData(MvcData mvcData) {
        this.mvcData = mvcData;
    }

    @Override
    public RegionModelSet getRegions() {
        return regions;
    }

    public void setRegions(RegionModelSet mvcData) {
        this.regions = mvcData;
    }


    @Override
    public String getHtmlClasses() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof RegionModel)
        {
            return ((RegionModel) obj).getName().equals(this.getName());
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.getName().hashCode();
    }

    @Override
    public String toString() {
        return "RegionImpl{" +
                "name='" + name + '\'' +
                ", entities=" + entities +
                ", mvcData='" + mvcData + '\'' +
                ", regions='" + regions + '\'' +
                '}';
    }

    class EntityPredicate implements Predicate {
        private final String entityId;

        public EntityPredicate(String id) {
            entityId = id;
        }

        public boolean evaluate(Object r) {
            EntityModel m = (EntityModel) r;
            return m.getId().equals(entityId);
        }
    }
}
