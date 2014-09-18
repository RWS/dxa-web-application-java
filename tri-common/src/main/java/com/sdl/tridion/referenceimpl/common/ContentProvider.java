package com.sdl.tridion.referenceimpl.common;

import com.sdl.tridion.referenceimpl.common.model.Page;

/**
 * TODO: Documentation.
 */
public interface ContentProvider {

    Page getPage(String uri) throws PageNotFoundException;
}
