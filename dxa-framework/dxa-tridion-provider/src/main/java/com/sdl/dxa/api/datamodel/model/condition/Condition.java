package com.sdl.dxa.api.datamodel.model.condition;

import com.sdl.dxa.api.datamodel.json.Polymorphic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Polymorphic
public class Condition {

    private boolean negate;
}
