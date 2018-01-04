package com.sdl.webapp.common.api.model.mvcdata;

import com.sdl.dxa.api.datamodel.model.MvcModelData;
import com.sdl.dxa.common.util.MvcUtils;
import com.sdl.webapp.common.api.model.MvcData;

import java.util.Map;

/**
 * <p>Used to create {@link MvcData} instances.</p>
 * @dxa.publicApi
 */
public final class MvcDataCreator {

    private MvcDataImpl mvcData;

    /**
     * <p>Returns an empty creator.</p>
     *
     * @return a new creator instance
     */
    public static MvcDataCreator creator() {
        MvcDataCreator creator = new MvcDataCreator();
        creator.mvcData = new MvcDataImpl();
        return creator;
    }

    /**
     * <p>Returns a creator filled with data from {@link MvcData} instance.</p>
     *
     * @param mvcData an object to copy in to creator
     * @return a new creator instance
     */
    public static MvcDataCreator creator(MvcData mvcData) {
        MvcDataCreator creator = new MvcDataCreator();
        creator.mvcData = new MvcDataImpl(mvcData);
        return creator;
    }

    /**
     * <p>Returns a creator filled with data from
     * {@link com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl.MvcDataImplBuilder} instance.</p>
     *
     * @param builder a builder to copy in to creator
     * @return a new creator instance
     */
    public static MvcDataCreator creator(MvcDataImpl.MvcDataImplBuilder builder) {
        MvcDataCreator creator = new MvcDataCreator();
        creator.mvcData = builder.build();
        return creator;
    }

    private static <T> T mergeChoose(T oldValue, T newValue, Class<T> type) {
        return mergeChoose(oldValue, newValue, false, type);
    }

    private static <T> T mergeChoose(T oldValue, T newValue, boolean emptyStringAware, Class<T> type) {
        boolean f = (emptyStringAware && type.equals(String.class) && ((String) newValue).isEmpty());
        return newValue == null || f ? oldValue : newValue;
    }

    /**
     * <p>Creates an instance of {@link MvcData}.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.MvcData} object.
     */
    public MvcData create() {
        return mvcData;
    }

    /**
     * Returns the instance of MvcDataCreator which helps to construct ${@link com.sdl.webapp.common.api.model.MvcData}.
     *
     * @param qualifiedViewName fully qualified name if defined format. Format must be 'ViewName'
     *                          or 'AreaName:ViewName' or 'AreaName:ControllerName:ViewName.'
     * @return new instance of creator
     */
    public MvcDataCreator fromQualifiedName(String qualifiedViewName) {
        MvcModelData mvcModelData = MvcUtils.parseMvcQualifiedViewName(qualifiedViewName);

        MvcDataImpl.MvcDataImplBuilder builder = MvcDataImpl.newBuilder();
        builder.viewName(mvcModelData.getViewName());
        builder.areaName(mvcModelData.getAreaName());
        builder.controllerName(mvcModelData.getControllerName());

        this.mvcData = builder.build();
        return this;
    }

    /**
     * <p>Applies default values from {@link DefaultsMvcData} object.</p>
     *
     * @param defaults a {@link com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData} object.
     * @return a {@link com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator} object.
     */
    public MvcDataCreator defaults(DefaultsMvcData defaults) {
        return defaultsInternal(defaults);
    }

    /**
     * <p>Converts this creator to {@link com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl.MvcDataImplBuilder}
     * which has direct access to fields.</p>
     *
     * @return a {@link MvcDataImpl.MvcDataImplBuilder} object.
     */
    public MvcDataImpl.MvcDataImplBuilder builder() {
        return this.mvcData.toBuilder();
    }

    /**
     * <p>Merges the given {@link MvcData} object into the current creator.</p>
     *
     * @param toMerge a {@link com.sdl.webapp.common.api.model.MvcData} object to merge in.
     * @return a {@link com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator} object.
     */
    public MvcDataCreator mergeIn(MvcData toMerge) {
        String controllerName = mergeChoose(this.mvcData.getControllerName(), toMerge.getControllerName(), String.class);
        String controllerAreaName = mergeChoose(this.mvcData.getControllerAreaName(), toMerge.getControllerAreaName(), String.class);
        String actionName = mergeChoose(this.mvcData.getActionName(), toMerge.getActionName(), String.class);
        String areaName = mergeChoose(this.mvcData.getAreaName(), toMerge.getAreaName(), String.class);
        String viewName = mergeChoose(this.mvcData.getViewName(), toMerge.getViewName(), String.class);
        String regionAreaName = mergeChoose(this.mvcData.getRegionAreaName(), toMerge.getRegionAreaName(), String.class);
        String regionName = mergeChoose(this.mvcData.getRegionName(), toMerge.getRegionName(), String.class);

        @SuppressWarnings("unchecked")
        Map<String, String> routeValues = mergeChoose(this.mvcData.getRouteValues(), toMerge.getRouteValues(), Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = mergeChoose(this.mvcData.getRouteValues(), toMerge.getRouteValues(), Map.class);

        this.mvcData
                .setControllerName(controllerName)
                .setControllerAreaName(controllerAreaName)
                .setActionName(actionName)
                .setAreaName(areaName)
                .setViewName(viewName)
                .setRegionAreaName(regionAreaName)
                .setRegionName(regionName)
                .setRouteValues(routeValues)
                .setMetadata(metadata);

        return this;
    }

    /**
     * This method treats {@link #mergeChoose(Object, Object, Class)} differently.
     * <p></p>
     * {@code newValue} in calls is actually an old value,
     * but if it happens to be null or empty, then it's replaced by a value
     * from {@link DefaultsMvcData} instance.
     * <p></p>
     * Normally, {@code newValue} comes from an object that is being merged in, thus if is is empty, then the
     * {@code MvcData} saves the original value.
     */
    private MvcDataCreator defaultsInternal(DefaultsMvcData defaults) {
        String controllerAreaName = mergeChoose(defaults.getControllerAreaName(), mvcData.getControllerAreaName(), String.class);
        String controllerName = mergeChoose(defaults.getControllerName(), mvcData.getControllerName(), String.class);
        String actionName = mergeChoose(defaults.getActionName(), mvcData.getActionName(), String.class);
        String areaName = mergeChoose(defaults.getAreaName(), mvcData.getAreaName(), String.class);

        this.mvcData
                .setControllerAreaName(controllerAreaName)
                .setControllerName(controllerName)
                .setActionName(actionName)
                .setAreaName(areaName);
        return this;
    }
}
