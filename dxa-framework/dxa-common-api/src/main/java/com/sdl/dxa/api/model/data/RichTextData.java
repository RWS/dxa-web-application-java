package com.sdl.dxa.api.model.data;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.model.data.util.CanGetAndCast;
import com.sdl.dxa.api.model.data.util.DelegatesToList;
import lombok.Value;

import java.util.List;

@Value
@JsonTypeName
public class RichTextData implements DelegatesToList<Object>, CanGetAndCast<Integer> {

    private List<Object> fragments;

    @Override
    public List<Object> getValues() {
        return getFragments();
    }

    @Override
    public Object getElement(Integer identifier) {
        return getFragments().get(identifier);
    }
}
