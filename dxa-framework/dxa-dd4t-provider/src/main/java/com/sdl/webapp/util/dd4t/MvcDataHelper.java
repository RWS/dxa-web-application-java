package com.sdl.webapp.util.dd4t;

import com.google.common.collect.Sets;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.tridion.mapping.EntityBuilderImpl;
import com.sdl.webapp.tridion.mapping.PageBuilderImpl;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.HasMetadata;
import org.dd4t.contentmodel.PageTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.getDefaultActionName;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.getDefaultAreaName;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.getDefaultControllerName;
import static com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData.getDefaultRegionName;
import static com.sdl.webapp.util.dd4t.FieldUtils.getStringValue;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Utility class to work with DD4T {@link ComponentPresentation}, {@link PageTemplate} and {@link ComponentTemplate} to
 * extract different forms of view/page/region names, also in form of a {@link MvcData}.
 *
 * @see EntityBuilderImpl
 * @see PageBuilderImpl
 * @since 1.7
 */
public final class MvcDataHelper {

    private static final Pattern REGION_VIEW_NAME_PATTERN = Pattern.compile(".*\\[(.*)]");

    private static final Set<String> NOT_METADATA_FIELD_NAMES = Sets.newHashSet("view", "regionView", "controller", "action", "routeValues");

    private MvcDataHelper() {
    }

    @NotNull
    public static MvcData createPageMvcData(@NotNull PageTemplate pageTemplate) {
        final String[] viewNameParts = getPageViewNameParts(pageTemplate);

        return MvcDataCreator.creator()
                .defaults(DefaultsMvcData.PAGE)
                .builder()
                .areaName(viewNameParts[0])
                .viewName(viewNameParts[1])
                .metadata(getMvcMetadata(pageTemplate))
                .build();
    }

    @NotNull
    public static MvcData createRegionMvcData(@NotNull ComponentTemplate componentTemplate) {
        final String[] viewNameParts = getRegionViewNameParts(componentTemplate);

        return MvcDataCreator.creator()
                .defaults(DefaultsMvcData.REGION)
                .builder()
                .areaName(viewNameParts[0])
                .viewName(viewNameParts[1])
                .build();
    }

    @NotNull
    public static MvcData createMvcData(@NotNull ComponentPresentation componentPresentation) {
        final ComponentTemplate componentTemplate = componentPresentation.getComponentTemplate();

        final String[] controllerNameParts = getControllerNameParts(componentTemplate);
        final String[] viewNameParts = getViewNameParts(componentTemplate);
        final String[] regionNameParts = getRegionViewNameParts(componentTemplate);

        return MvcDataImpl.newBuilder()
                .controllerAreaName(controllerNameParts[0])
                .controllerName(controllerNameParts[1])
                .areaName(viewNameParts[0])
                .viewName(viewNameParts[1])
                .regionAreaName(regionNameParts[0])
                .regionName(regionNameParts[1])
                .actionName(getActionName(componentTemplate))
                .metadata(getMvcMetadata(componentTemplate))
                .routeValues(getRouteValues(componentTemplate))
                .build();
    }

    @Nullable
    public static String getRegionName(@NotNull ComponentPresentation componentPresentation) {
        Map<String, Field> templateMeta = componentPresentation.getComponentTemplate().getMetadata();
        String regionName = null;

        if (templateMeta != null) {
            regionName = FieldUtils.getStringValue(templateMeta, "regionName");
            if (isEmpty(regionName)) {
                //fallback if region name field is empty, use regionView name
                regionName = defaultIfBlank(FieldUtils.getStringValue(templateMeta, "regionView"), getDefaultRegionName());
            }
        }

        return regionName;
    }

    @NotNull
    private static Map<String, String> getRouteValues(@NotNull ComponentTemplate componentTemplate) {
        final Map<String, String> routeValues = new HashMap<>();

        String routeValuesStrings = FieldUtils.getStringValue(componentTemplate.getMetadata(), "routeValues");
        if (routeValuesStrings != null) {
            for (String value : routeValuesStrings.split(",")) {
                final String[] parts = value.split(":");
                if (parts.length > 1 && !routeValues.containsKey(parts[0])) {
                    routeValues.put(parts[0], parts[1]);
                }
            }
        }
        return routeValues;
    }

    @NotNull
    private static Map<String, Object> getMvcMetadata(@NotNull HasMetadata withMetadata) {
        Map<String, Object> metadata = new HashMap<>();

        for (Map.Entry<String, Field> entry : withMetadata.getMetadata().entrySet()) {
            String fieldName = entry.getKey();

            if (NOT_METADATA_FIELD_NAMES.contains(fieldName)) {
                continue;
            }

            Field field = entry.getValue();
            if (!field.getValues().isEmpty()) {
                metadata.put(fieldName, field.getValues().get(0).toString()); // Assume single-value text fields for template metadata
            }
        }

        return metadata;
    }

    private static Map<String, Object> getMvcMetadata(PageTemplate pageTemplate) {

        Map<String, Object> metadata = new HashMap<>();
        Map<String, Field> metadataFields = pageTemplate.getMetadata();
        for (Map.Entry<String, Field> entry : metadataFields.entrySet()) {

            String fieldName = entry.getKey();
            if ("view".equals(fieldName) ||
                    "includes".equals(fieldName)) {
                continue;
            }
            Field field = entry.getValue();
            if (field.getFieldType() == FieldType.EMBEDDED) {
                // Output embedded field as List<Map<String,String>>
                //
                List<Map<String, String>> embeddedDataList = new ArrayList<>();
                for (Object value : field.getValues()) {
                    FieldSet fieldSet = (FieldSet) value;
                    Map<String, String> embeddedData = new HashMap<>();
                    for (String subFieldName : fieldSet.getContent().keySet()) {
                        Field subField = fieldSet.getContent().get(subFieldName);
                        if (!subField.getValues().isEmpty()) {
                            embeddedData.put(subFieldName, subField.getValues().get(0).toString());
                        }
                    }
                    embeddedDataList.add(embeddedData);
                }
                metadata.put(fieldName, embeddedDataList);
            } else {
                // Output other field types as single-value text fields
                if (!field.getValues().isEmpty()) {
                    metadata.put(fieldName, field.getValues().get(0).toString()); // Assume single-value text fields for template metadata
                }
            }
        }
        return metadata;
    }

    @NotNull
    private static String[] getViewNameParts(@NotNull ComponentTemplate componentTemplate) {
        return selectViewName(componentTemplate, "view", new DefaultValueCallback() {
            @Override
            public String call() {
                return componentTemplate.getTitle().replaceAll("\\[.*]|\\s", "");
            }
        });
    }

    @NotNull
    private static String[] getRegionViewNameParts(@NotNull ComponentTemplate componentTemplate) {
        return selectViewName(componentTemplate, "regionView", new DefaultValueCallback() {
            @Override
            public String call() {
                final Matcher matcher = REGION_VIEW_NAME_PATTERN.matcher(componentTemplate.getTitle());

                return matcher.matches() ? matcher.group(1) : getDefaultRegionName();
            }
        });
    }

    @NotNull
    private static String getActionName(@NotNull ComponentTemplate componentTemplate) {
        return defaultIfBlank(getStringValue(componentTemplate.getMetadata(), "action"), getDefaultActionName());
    }

    @NotNull
    private static String[] getControllerNameParts(@NotNull ComponentTemplate componentTemplate) {
        return selectViewName(componentTemplate, "controller", getDefaultControllerName());
    }

    @NotNull
    private static String[] getPageViewNameParts(@NotNull PageTemplate pageTemplate) {

        return selectViewName(pageTemplate, "view", new DefaultValueCallback() {
            @Override
            public String call() {
                return pageTemplate.getTitle().replaceAll(" ", "");
            }
        });
    }

    private static String[] selectViewName(HasMetadata withMetadata, String fieldName, String defaultValue) {
        return selectViewName(withMetadata, fieldName, new DefaultValueCallback() {
            @Override
            public String call() {
                return defaultValue;
            }
        });
    }

    private static String[] selectViewName(HasMetadata withMetadata, String fieldName, DefaultValueCallback callback) {
        String viewName = getStringValue(withMetadata.getMetadata(), fieldName);

        if (isEmpty(viewName)) {
            viewName = callback.call();
        }

        return splitViewName(viewName);
    }

    private static String[] splitViewName(String name) {
        final String[] parts = name.split(":");
        return parts.length > 1 ? parts : new String[]{getDefaultAreaName(), name};
    }

    @FunctionalInterface
    private interface DefaultValueCallback {

        String call();
    }
}
