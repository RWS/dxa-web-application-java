package com.sdl.webapp.common.api.model.query;

import lombok.Data;

@Data
public abstract class AbstractQuery {

    private int resultLimit;

    private int startAt;

    private int pageSize;

    private boolean hasMore;
}
