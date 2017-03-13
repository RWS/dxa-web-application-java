package com.sdl.dxa.common.util;

import com.google.common.base.Splitter;
import com.sdl.dxa.api.datamodel.model.MvcModelData;
import com.sdl.webapp.common.util.StringUtils;

import java.util.List;

public final class MvcUtils {

    private MvcUtils() {
    }

    /**
     * Builds a {@link MvcModelData} from a qualified view name.
     *
     * @param qualifiedViewName fully qualified name in a defined format. Format must be 'ViewName'
     *                          or 'AreaName:ViewName' or 'AreaName:ControllerName:ViewName.'
     * @return a {@link MvcModelData} with existing fields initiated
     * @throws IllegalArgumentException if format is wrong
     */
    public static MvcModelData parseMvcQualifiedViewName(String qualifiedViewName) {

        List<String> parts = Splitter.on(":").omitEmptyStrings().splitToList(StringUtils.dashify(qualifiedViewName));

        MvcModelData.MvcModelDataBuilder builder = MvcModelData.builder();
        switch (parts.size()) {
            case 1:
                builder.viewName(parts.get(0));
                break;
            case 2:
                builder.areaName(parts.get(0)).viewName(parts.get(1));
                break;
            case 3:
                builder.areaName(parts.get(0)).controllerName(parts.get(1)).viewName(parts.get(2));
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("Invalid format for Qualified View Name: '%s'. " +
                                "Format must be 'ViewName' or 'AreaName:ViewName' " +
                                "or 'AreaName:ControllerName:ViewName.'", qualifiedViewName));
        }
        return builder.build();
    }
}
