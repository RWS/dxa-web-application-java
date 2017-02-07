package com.sdl.dxa.api.model.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * Base class of DXA Data Model, the top of View Model hierarchy.
 * <p>Introduced as a replacement for the old DD4T data model which is unnecessarily too verbose forcing to do all
 * the mapping logic os a server-side that made some sense in its original purpose but is not needed in DXA
 * since all the schemas and types are known out of the box.</p>
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
public abstract class ViewModelData {

    private String schemaId;

    private String htmlClasses;

    private Map<String, ?> xpmMetadata;

    private ContentModelData metadata;

    private Map<String, ?> extensionData;

    private MvcData mvcData;
}
