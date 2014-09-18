package com.sdl.tridion.referenceimpl.model;

/**
 * TODO: Documentation.
 */
public interface ContentProvider {

    PageModel getPageModel(String uri) throws PageNotFoundException;

    // TODO: Add methods to get pages, regions and entities.
}
