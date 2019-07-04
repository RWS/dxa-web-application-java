package com.sdl.webapp.common.impl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * The ErrorMessage class. It holds error message with its Http response code.
 */
@Data
@AllArgsConstructor
@JsonIgnoreProperties({"httpStatus"})
public class ErrorMessage {

    @JsonProperty("Message")
    private String message;

    private HttpStatus httpStatus;
}
