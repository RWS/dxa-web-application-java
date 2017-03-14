package com.sdl.dxa.api.datamodel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * Base class of DXA Data Model, the top of View Model hierarchy.
 * <p>Introduced as a replacement for the old DD4T data model which is unnecessarily too verbose forcing to do all
 * the mapping logic os a server-side that made some sense in its original purpose but is not needed in DXA
 * since all the schemas and types are known out of the box.</p>
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class ViewModelData {

    private String schemaId;

    private String htmlClasses;

    private Map<String, Object> xpmMetadata;

    private ContentModelData metadata;

    private Map<String, Object> extensionData;

    private MvcModelData mvcData;
}
