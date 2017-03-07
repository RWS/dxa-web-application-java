package com.sdl.webapp.common.api.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractQuery {

    private int resultLimit;

    private int startAt;

    private int pageSize;

    private boolean hasMore;
}
