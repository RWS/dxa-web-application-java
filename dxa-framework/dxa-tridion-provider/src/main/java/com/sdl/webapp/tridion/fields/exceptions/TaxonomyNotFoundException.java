package com.sdl.webapp.tridion.fields.exceptions;

import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TaxonomyNotFoundException extends DxaItemNotFoundException {
    public TaxonomyNotFoundException(String message) {
        super(message);
    }

    public TaxonomyNotFoundException(Throwable cause) {
        super(cause);
    }

    public TaxonomyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
