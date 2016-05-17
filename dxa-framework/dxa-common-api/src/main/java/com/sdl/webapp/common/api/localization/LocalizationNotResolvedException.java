package com.sdl.webapp.common.api.localization;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class LocalizationNotResolvedException extends RuntimeException {

    @Getter
    private int httpStatus = HttpStatus.NOT_FOUND.value();

    public LocalizationNotResolvedException(String message) {
        super(message);
    }

    public LocalizationNotResolvedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocalizationNotResolvedException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public LocalizationNotResolvedException(String message, Throwable cause, int httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public static class WithCustomResponse extends LocalizationNotResolvedException {

        @Getter
        private String content;

        @Getter
        private String contentType = "text/html";

        public WithCustomResponse(String message, String content, String contentType) {
            super(message);
            this.content = content;
            this.contentType = contentType;
        }

        public WithCustomResponse(String message, int httpStatus, String content, String contentType) {
            super(message, httpStatus);
            this.content = content;
            this.contentType = contentType;
        }

        public WithCustomResponse(String message, Throwable cause, int httpStatus, String content, String contentType) {
            super(message, cause, httpStatus);
            this.content = content;
            this.contentType = contentType;
        }

        public WithCustomResponse(String message, Throwable cause, String content, String contentType) {
            super(message, cause);
            this.content = content;
            this.contentType = contentType;
        }
    }
}
