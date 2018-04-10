package com.sdl.dxa.api.datamodel.model.unknown;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Value;

/**
 * Handler of any unknown entity-level class. Basically represents any data of a class that is not known with a JSON string as it is.
 */
@Value
@JsonDeserialize(using = UnknownModelDataDeserializer.class)
@JsonSerialize(using = UnknownModelDataSerializer.class)
public class UnknownModelData {

    private String content;
}
