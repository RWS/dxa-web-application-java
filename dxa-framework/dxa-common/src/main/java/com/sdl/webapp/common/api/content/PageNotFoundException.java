package com.sdl.webapp.common.api.content;

import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a content provider cannot find the requested page.
 * @dxa.publicApi
 */
@Getter
@ResponseStatus(reason = "Page not found", code = HttpStatus.NOT_FOUND)
public class PageNotFoundException extends DxaItemNotFoundException {

    private final static String MESSAGE = "Cannot find a page with publicationId '%s' and url '%s'";

    private int publicationId;

    private String path;

    public PageNotFoundException(int publicationId, String path) {
        super(String.format(MESSAGE, publicationId, path));
        this.publicationId = publicationId;
        this.path = path;
    }

    public PageNotFoundException(int publicationId, String path, Throwable cause) {
        super(String.format(MESSAGE, publicationId, path), cause);
        this.publicationId = publicationId;
        this.path = path;
    }

    public PageNotFoundException() {
    }

    public PageNotFoundException(String message) {
        super(message);
    }

    public PageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PageNotFoundException(Throwable cause) {
        super(cause);
    }

    public PageNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
