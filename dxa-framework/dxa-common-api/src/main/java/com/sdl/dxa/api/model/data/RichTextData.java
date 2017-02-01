package com.sdl.dxa.api.model.data;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Value;

import java.util.List;

@Value
@JsonTypeName
public class RichTextData {

    private List<?> fragments;
}
