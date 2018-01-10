package com.sdl.webapp.common.api.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity that forces page to redirect to the given URL.
 *
 * @dxa.publicApi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class RedirectEntity extends AbstractEntityModel {

    private String url;
}
