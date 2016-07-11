package com.sdl.webapp.common.impl.localization.semantics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JsonVocabulary {

    @JsonProperty("Prefix")
    private String prefix;

    @JsonProperty("Vocab")
    private String vocab;
}
