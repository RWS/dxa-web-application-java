package com.sdl.webapp.common.api.model.mvcdata;

import com.sdl.webapp.common.api.model.MvcData;

public final class MvcDataCreator {

    private MvcDataImpl mvcData;

    public static MvcDataCreator creator() {
        MvcDataCreator creator = new MvcDataCreator();
        creator.mvcData = new MvcDataImpl();
        return creator;
    }

    public static MvcDataCreator creator(MvcData mvcData) {
        MvcDataCreator creator = new MvcDataCreator();
        creator.mvcData = new MvcDataImpl(mvcData);
        return creator;
    }

    public static MvcDataCreator creator(MvcDataImpl.MvcDataImplBuilder builder) {
        MvcDataCreator creator = new MvcDataCreator();
        creator.mvcData = builder.build();
        return creator;
    }

    public MvcData create() {
        return mvcData;
    }

    /**
     * @param qualifiedViewName fully qualified name if defined format. Format must be 'ViewName'
     *                          or 'AreaName:ViewName' or 'AreaName:ControllerName:ViewName.'
     */
    public MvcDataCreator fromQualifiedName(String qualifiedViewName) {
        String[] parts = qualifiedViewName == null || qualifiedViewName.isEmpty() ? null : qualifiedViewName.split(":");

        if (parts == null || parts.length < 1 || parts.length > 3) {
            throw new IllegalArgumentException(
                    String.format("Invalid format for Qualified View Name: '%s'. " +
                            "Format must be 'ViewName' or 'AreaName:ViewName' " +
                            "or 'AreaName:ControllerName:ViewName.'", qualifiedViewName));
        }

        MvcDataImpl.MvcDataImplBuilder builder = MvcDataImpl.builder();

        switch (parts.length) {
            case 1:
                builder
                        .viewName(parts[0]);
                break;
            case 2:
                builder
                        .areaName(parts[0])
                        .viewName(parts[1]);
                break;
            case 3:
                builder
                        .areaName(parts[0])
                        .controllerName(parts[1])
                        .viewName(parts[2]);
                break;
        }

        this.mvcData = builder.build();
        return this;
    }

    public MvcDataCreator defaults(DefaultsMvcData defaults) {
        return defaultsInternal(defaults);
    }

    public MvcDataCreator mergeIn(MvcData toMerge) {
        String controllerName = mergeChoose(this.mvcData.getControllerName(), toMerge.getControllerName());
        String areaName = mergeChoose(this.mvcData.getAreaName(), toMerge.getAreaName());
        String viewName = mergeChoose(this.mvcData.getViewName(), toMerge.getViewName());

        this.mvcData
                .setControllerName(controllerName)
                .setAreaName(areaName)
                .setViewName(viewName);
        return this;
    }

    public MvcDataImpl.MvcDataImplBuilder builder() {
        return this.mvcData.toBuilder();
    }

    /**
     * This method treats {@link #mergeChoose(String, String, boolean)} differently.
     * <p/>
     * {@code newValue} in calls is actually an old value,
     * but if it happens to be null or empty, then it's replaced by a value
     * from {@link DefaultsMvcData} instance.
     * <p/>
     * Normally, {@code newValue} comes from an object that is being merged in, thus if is is empty, then the
     * {@code MvcData} saves the original value.
     */
    private MvcDataCreator defaultsInternal(DefaultsMvcData defaults) {
        String controllerAreaName = mergeChoose(defaults.getControllerAreaName(), mvcData.getControllerAreaName());
        String controllerName = mergeChoose(defaults.getControllerName(), mvcData.getControllerName());
        String actionName = mergeChoose(defaults.getActionName(), mvcData.getActionName());
        String areaName = mergeChoose(defaults.getAreaName(), mvcData.getAreaName());

        this.mvcData
                .setControllerAreaName(controllerAreaName)
                .setControllerName(controllerName)
                .setActionName(actionName)
                .setAreaName(areaName);
        return this;
    }

    private String mergeChoose(String oldValue, String newValue) {
        return mergeChoose(oldValue, newValue, false);
    }

    private String mergeChoose(String oldValue, String newValue, boolean emptyStringAware) {
        return newValue == null || (emptyStringAware && newValue.isEmpty()) ? oldValue : newValue;
    }
}
