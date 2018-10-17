package com.sdl.webapp.common.api.model.region;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.formatters.support.FeedItemsProvider;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.AbstractViewModel;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.api.xpm.ComponentType;
import com.sdl.webapp.common.api.xpm.OccurrenceConstraint;
import com.sdl.webapp.common.api.xpm.XpmRegion;
import com.sdl.webapp.common.api.xpm.XpmRegionConfig;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import com.sdl.webapp.common.util.XpmUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.google.common.collect.FluentIterable.from;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.REGION;


/**
 * Basic implementation of region model. This is a basic extension point to create your region models.
 */
@EqualsAndHashCode(of = "name", callSuper = true)
@Slf4j
public class RegionModelImpl extends AbstractViewModel implements RegionModel {

    /**
     * @see XpmUtils.RegionXpmBuilder#INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY
     * @deprecated since 2.0, use {@link XpmUtils.RegionXpmBuilder#INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY}
     */
    @Deprecated
    public static final String INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY = XpmUtils.RegionXpmBuilder.INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY;

    /**
     * @see XpmUtils.RegionXpmBuilder#INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY
     * @deprecated since 2.0, use {@link XpmUtils.RegionXpmBuilder#INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY}
     */
    @Deprecated
    public static final String INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY = XpmUtils.RegionXpmBuilder.INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY;

    /**
     * @see XpmUtils.RegionXpmBuilder#INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY
     * @deprecated since 2.0, use {@link XpmUtils.RegionXpmBuilder#INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY}
     */
    @Deprecated
    public static final String INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY = XpmUtils.RegionXpmBuilder.INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY;

    private static final String XPM_REGION_MARKUP = "<!-- Start Region: {title: \"%s\",%s allowedComponentTypes: [%s], %s} -->";

    private static final String XPM_COMPONENT_TYPE_MARKUP = "{schema: \"%s\", template: \"%s\"}";

    private static final String XPM_OCCURRENCE_CONSTRAINT_UNLIMITED_MARKUP = "minOccurs: %s";
    private static final String XPM_OCCURRENCE_CONSTRAINT_MARKUP = "minOccurs: %s, maxOccurs: %s";

    @JsonProperty("Name")
    @Getter
    // TODO: Should we really expose a setter for the region name when we already set that in the constructor?
    @Setter
    private String name;

    private String schemaId;

    @JsonProperty("Entities")
    @Getter
    @Setter
    //todo dxa2 refactor: #getEntities() return inner collection to modify, probably that should be done through API,
    //thus it's not possible to e.g. optimize ID search with a map because otherwise getEntities().clear() will set
    //region model into inconsistent state when inner map is full but list is empty
    private List<EntityModel> entities = new ArrayList<>();

    @JsonProperty("Regions")
    @Getter
    @Setter
    private RegionModelSet regions = new RegionModelSetImpl();

    public RegionModelImpl(RegionModel other) {
        super(other);
        this.name = other.getName();
        if (other.getEntities() != null) {
            this.entities.addAll(other.getEntities());
        }
        if (other.getRegions() != null) {
            this.regions.addAll(other.getRegions());
        }
    }

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
                .defaults(REGION)
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
        setMvcData(mvcData);
    }

    private static XpmRegionConfig getXpmRegionConfig() {
        return ApplicationContextHolder.getContext().getBean(XpmRegionConfig.class);
    }

    /** {@inheritDoc} */
    @Override
    public String getSchemaId() { return schemaId; }

    /** {@inheritDoc} */
    @Override
    public void setSchemaId(String schemaId) { this.schemaId = schemaId; }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityModel getEntity(String entityId) {
        for (EntityModel entity : this.entities) {
            if (Objects.equals(entity.getId(), entityId)) {
                return entity;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEntity(EntityModel entity) {
        if (entity.getId() == null) {
            log.warn("Add entity with id null, this might lead to errors! Entity: {}", entity);
        }
        this.entities.add(entity);
    }

    @Override
    public RegionModel deepCopy() {
        return new RegionModelImpl(this);
    }

    @Override
    public void filterConditionalEntities(Collection<ConditionalEntityEvaluator> evaluators) {
        regions.forEach(regionModel -> regionModel.filterConditionalEntities(evaluators));

        entities.removeIf(entityModel -> !evaluators.stream().allMatch(evaluator -> evaluator.includeEntity(entityModel)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXpmMarkup(Localization localization) {
        XpmRegionConfig xpmRegionConfig = getXpmRegionConfig();

        XpmRegion xpmRegion;
        if (this.schemaId != null && !this.schemaId.isEmpty()) {
            xpmRegion = xpmRegionConfig.getXpmRegion(this.schemaId, localization);
        }
        else {
            xpmRegion = xpmRegionConfig.getXpmRegion(this.name, localization);
        }

        if (xpmRegion == null) {
            return "";
        }

        int minOccurs = 0;
        int maxOccurs = -1;
        OccurrenceConstraint occurrenceConstraint = xpmRegion.getOccurrenceConstraint();
        if (occurrenceConstraint != null) {
            minOccurs = occurrenceConstraint.getMinOccurs();
            maxOccurs = occurrenceConstraint.getMaxOccurs();
        }

        String occurrenceConstraintStr;
        if (maxOccurs == -1) {
            occurrenceConstraintStr = String.format(XPM_OCCURRENCE_CONSTRAINT_UNLIMITED_MARKUP, minOccurs);
        } else {
            occurrenceConstraintStr = String.format(XPM_OCCURRENCE_CONSTRAINT_MARKUP, minOccurs, maxOccurs);
        }

        String pathToRegion = null;
        Map<String, Object> xpmMetadata = this.getXpmMetadata();
        if (xpmMetadata != null) {
            pathToRegion = (String)xpmMetadata.get("FullyQualifiedName");
            if(pathToRegion != null) {
                pathToRegion = String.format(" path: \"%s\",", pathToRegion.replace("\\", "\\\\"));
            }
        }
        pathToRegion = pathToRegion != null ? pathToRegion : "";

        List<String> types = new ArrayList<>();

        for (ComponentType ct : xpmRegion.getComponentTypes()) {
            types.add(String.format(XPM_COMPONENT_TYPE_MARKUP, ct.getSchemaId(), ct.getTemplateId()));
        }

        return String.format(
                XPM_REGION_MARKUP,
                this.name,
                pathToRegion,
                Joiner.on(", ").join(types),
                occurrenceConstraintStr,
                0);
    }

    @Override
    public List<FeedItem> extractFeedItems() {
        List<FeedItem> feedItems = collectFeedItems(regions);
        feedItems.addAll(collectFeedItems(from(entities).filter(FeedItemsProvider.class).toList()));
        return feedItems;
    }
}
