package com.sdl.dxa.common;

import java.net.URI;

public class ClaimValues {
    public static String ISH_MODULE_PREFIX = "taf:ish:";

    public static final String ISH_CONDITIONS = ISH_MODULE_PREFIX + "userconditions";
    public static final URI ISH_CONDITIONS_MERGED = URI.create(ISH_MODULE_PREFIX + "userconditions:merged");
}
