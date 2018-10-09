package com.sdl.dxa.common.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClaimHolder {
    private String claimType;
    private String uri;
    private String value;
}
