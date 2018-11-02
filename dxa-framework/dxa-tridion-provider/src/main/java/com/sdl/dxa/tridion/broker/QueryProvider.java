package com.sdl.dxa.tridion.broker;

import com.sdl.web.pca.client.contentmodel.generated.Item;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;

import java.util.List;

public interface QueryProvider {

    boolean hasMore();

    String getCursor();

    List<Item> executeQueryItems(SimpleBrokerQuery query);
}
