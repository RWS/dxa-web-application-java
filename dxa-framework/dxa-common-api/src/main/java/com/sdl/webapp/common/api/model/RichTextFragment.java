package com.sdl.webapp.common.api.model;

import com.sdl.webapp.common.exceptions.DxaException;

public interface RichTextFragment {
    String toHtml() throws DxaException;
}
