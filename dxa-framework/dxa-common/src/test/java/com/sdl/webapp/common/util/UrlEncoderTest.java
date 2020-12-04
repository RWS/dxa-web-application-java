package com.sdl.webapp.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class UrlEncoderTest {

    @Test
    public void testSpacesInPathEncodedProperly() {
        assertEquals("/media/which%20config%20to%20use.png",
                UrlEncoder.urlPartialPathEncode("/media/which config to use.png"));
    }

    @Test
    public void testSlashesInPathNotEncoded() {
        assertEquals("/media/which\\config\\to\\use.png",
                UrlEncoder.urlPartialPathEncode("/media/which\\config\\to\\use.png"));
    }

    @Test
    public void testAllCharactersEncoded() {
        assertEquals("/media/a-z+A_Z%21%22%23%24%25%26%27%28%29%2A%2C%3A%3B%3C%3D%3E%3F%40%5B%5D%5E%7B%7C%7D%7E.png",
                UrlEncoder.urlPartialPathEncode("/media/a-z+A_Z!\"#$%&\'()*,:;<=>?@[]^{|}~.png"));
    }

    @Test
    public void testSpacesInPathEncodedProperlyGuava() {
        assertEquals("/media/which%20config%20to%20use.png",
                UrlEncoder.urlPartialPathEncodeFullEntityTable("/media/which config to use.png"));
    }

    @Test
    public void testAllCharactersEncodedGuava() {
        assertEquals("/media/%C2%A3/%60/a-z%2BA_Z%21%22%23%24%25%26%27%28%29%2A%2C%3A%3B%3C%3D%3E%3F%40%5B%5D%5E%7B%7C%7D%7E.png",
                UrlEncoder.urlPartialPathEncodeFullEntityTable("/media/£/`/a-z+A_Z!\"#$%&\'()*,:;<=>?@[]^{|}~.png"));
    }

    @Test
    public void testNotAllowerCaharactersInCMS() {
        //replacement might be any
        assertEquals("%5C/%3A/%2A%3F/%3E%3C%7C.png",
                UrlEncoder.urlPartialPathEncodeFullEntityTable("\\/:/*?/><|.png"));
    }
}
