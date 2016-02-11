package com.sdl.webapp.common.api.model.region;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.api.xpm.ComponentType;
import com.sdl.webapp.common.api.xpm.XpmRegion;
import com.sdl.webapp.common.api.xpm.XpmRegionConfig;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.CORE_REGION;


/**
 * <p>RegionModelImpl class.</p>
 */
@EqualsAndHashCode(of = {"name"})
public class RegionModelImpl implements RegionModel {
    /**
     * The XPM metadata key used for the ID of the (Include) Page from which the Region originates.
     * Avoid using this in implementation code because it may change in a future release.
     */
    public static final String INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY = "IncludedFromPageID";
    /**
     * The XPM metadata key used for the title of the (Include) Page from which the Region originates.
     * Avoid using this in implementation code because it may change in a future release.
     */
    public static final String INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY = "IncludedFromPageTitle";
    /**
     * The XPM metadata key used for the file name of the (Include) Page from which the Region originates.
     * Avoid using this in implementation code because it may change in a future release.
     */
    public static final String INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY = "IncludedFromPageFileName";

    private static final String XPM_REGION_MARKUP = "<!-- Start Region: {title: \"%s\", allowedComponentTypes: [%s], minOccurs: %s} -->";

    private static final String XPM_COMPONENT_TYPE_MARKUP = "{schema: \"%s\", template: \"%s\"}";

    @JsonProperty("Name")
    @Getter
    // TODO: Should we really expose a setter for the region name when we already set that in the constructor?
    @Setter
    private String name;

    @JsonIgnore
    @Getter
    @Setter
    private String htmlClasses;

    @JsonProperty("Entities")
    @Getter
    @Setter
    private List<EntityModel> entities = new ArrayList<>();

    @JsonProperty("XpmMetadata")
    @Getter
//    setter explicitly defined
    private Map<String, Object> xpmMetadata = new HashMap<>();

    @JsonProperty("MvcData")
    @Getter
    @Setter
    private MvcData mvcData;

    @JsonProperty("Regions")
    @Getter
    @Setter
    private RegionModelSet regions;

    /**
     * <p>Constructor for RegionModelImpl.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    public RegionModelImpl(String name) throws DxaException {
        if (Strings.isNullOrEmpty(name)) {
            throw new DxaException("Region must have a non-empty name.");
        }
        this.setName(name);
    }

    /**
     * <p>Constructor for RegionModelImpl.</p>
     *
     * @param name              a {@link java.lang.String} object.
     * @param qualifiedViewName a {@link java.lang.String} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    public RegionModelImpl(String name, String qualifiedViewName) throws DxaException {
        this(name);
        this.setMvcData(MvcDataCreator.creator()
                .defaults(CORE_REGION)
                .builder()
                .regionName(qualifiedViewName)
                .viewName(qualifiedViewName)
                .build());
    }

    /**
     * <p>Constructor for RegionModelImpl.</p>
     *
     * @param mvcData a {@link com.sdl.webapp.common.api.model.MvcData} object.
     * @throws com.sdl.webapp.common.exceptions.DxaException if any.
     */
    public RegionModelImpl(MvcData mvcData) throws DxaException {
        this(mvcData.getRegionName());
        this.mvcData = mvcData;
    }

    private static XpmRegionConfig getXpmRegionConfig() {
        return ApplicationContextHolder.getContext().getBean(XpmRegionConfig.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEntity(EntityModel entity) {
        this.entities.add(entity);
    }

    /** {@inheritDoc} */
    @Override
    public EntityModel getEntity(String entityId) {
        Collection<EntityModel> c = new ArrayList<>();
        for (EntityModel entity : this.entities) {
            if (Objects.equals(entity.getId(), entityId)) {
                c.add(entity);
            }
        }

        if (!c.isEmpty()) {
            return c.iterator().next();
        }
        return null;
    }

    /**
     * <p>Setter for the field <code>xpmMetadata</code>.</p>
     *
     * @param xpmMetadata a {@link java.util.Map} object.
     */
    public void setXpmMetadata(Map<String, Object> xpmMetadata) {
        this.xpmMetadata = ImmutableMap.copyOf(xpmMetadata);
    }

    /** {@inheritDoc} */
    @Override
    public String getXpmMarkup(Localization localization) {
        XpmRegionConfig xpmRegionConfig = getXpmRegionConfig();
        XpmRegion xpmRegion = xpmRegionConfig.getXpmRegion(this.name, localization);

        if (xpmRegion == null) {
            return "";
        }

        List<String> types = new ArrayList<>();

        for (ComponentType ct : xpmRegion.getComponentTypes()) {
            types.add(String.format(XPM_COMPONENT_TYPE_MARKUP, ct.getSchemaId(), ct.getTemplateId()));
        }

        // TODO: obtain MinOccurs & MaxOccurs from regions.json
        return String.format(
                XPM_REGION_MARKUP,
                this.name,
                Joiner.on(", ").join(types),
                0);
    }
}
