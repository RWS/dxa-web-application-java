package org.dd4t.core.util;

import org.junit.Test;

import java.text.ParseException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class TCMURITest {

    private static final int PUBLICATION_ID = 12;
    private static final int ITEM_ID = 14;
    private static final int ITEM_TYPE = 5;
    private static final int VERSION = 2;

    private static final int DEFAULT_VERSION = -1;
    private static final int DEFAULT_ITEM_TYPE = 16;

    private final TCMURI tcmUri = new TCMURI(PUBLICATION_ID, ITEM_ID, ITEM_TYPE, VERSION);

    @Test
    public void convert_to_string_using_format () throws ParseException {
        final String uri = String.format("tcm:%s-%s-%s", PUBLICATION_ID, ITEM_ID, ITEM_TYPE);

        assertThat(tcmUri.toString(), is(uri));
        assertThat(new TCMURI(uri).toString(), is(uri));
    }

    @Test
    public void return_item_type () {
        assertEquals(ITEM_TYPE, tcmUri.getItemType());
    }

    @Test
    public void return_item_id () {
        assertEquals(ITEM_ID, tcmUri.getItemId());
    }

    @Test
    public void return_publication_id () {
        assertEquals(PUBLICATION_ID, tcmUri.getPublicationId());
    }

    @Test
    public void return_version () {
        assertEquals(VERSION, tcmUri.getVersion());
    }

    @Test
    public void tcmUri_be_valid () {
        assertThat(TCMURI.isValid(tcmUri.toString()), is(true));
    }

    @Test
    public void throws_exception_when_uri_is_null () {
        try {
            new TCMURI(null);
            fail("Exception expected");
        } catch (ParseException e) {
            assertThat(e.getMessage(), is("Invalid TCMURI String, string cannot be null"));
            assertThat(e.getErrorOffset(), is(0));
        }
    }

    @Test
    public void throws_exception_when_uri_has_wrong_format () {
        final String uri = String.format("wrong:%s-%s-%s", PUBLICATION_ID, ITEM_ID, ITEM_TYPE);

        try {
            new TCMURI(uri);
            fail("Exception expected");
        } catch (ParseException e) {
            assertThat(e.getMessage(), is(String.format("URI string %s does not start with %s", uri, TCMURI.URI_NAMESPACE)));
        }
    }

    @Test
    public void throws_exception_when_uri_has_only_publication_id () {
        final String uri = String.format("tcm:%s#bad", PUBLICATION_ID);

        try {
            new TCMURI(uri);
            fail("Exception expected");
        } catch (ParseException e) {
            assertThat(e.getMessage(), is(String.format("URI %s does not match the pattern", uri)));
        }
    }

    @Test
    public void build_when_url_only_mandatory_parameters_and_put_default () throws ParseException {
        final String uri = String.format("tcm:%s-%s", PUBLICATION_ID, ITEM_ID);

        TCMURI tcmUri = new TCMURI(uri);

        assertThat(tcmUri.getPublicationId(), is(PUBLICATION_ID));
        assertThat(tcmUri.getItemId(), is(ITEM_ID));
        assertThat(tcmUri.getItemType(), is(DEFAULT_ITEM_TYPE));
        assertThat(tcmUri.getVersion(), is(DEFAULT_VERSION));
    }

    @Test
    public void throws_exception_when_publication_id_is_not_a_number () {
        final String uri = String.format("tcm:%s-%s-%s", "aString", ITEM_ID, ITEM_TYPE);

        try {
            new TCMURI(uri);
            fail("Exception expected");
        } catch (ParseException e) {
            assertThat(e.getMessage(), is(String.format("URI %s does not match the pattern", uri)));
        }
    }

    @Test
    public void return_item_type_16_when_is_not_part_of_URI () throws ParseException {
        final String uri = String.format("tcm:%s-%s", PUBLICATION_ID, ITEM_ID);

        TCMURI tcmUri = new TCMURI(uri);
        assertThat(tcmUri.getItemType(), is(16));
    }

    @Test
    public void return_version_when_is_part_of_uri () throws ParseException {
        final String uri = String.format("tcm:%s-%s-%s-v%s", PUBLICATION_ID, ITEM_ID, ITEM_TYPE, VERSION);

        TCMURI tcmUri = new TCMURI(uri);
        assertThat(tcmUri.getItemType(), is(ITEM_TYPE));
        assertThat(tcmUri.getVersion(), is(VERSION));
    }

    @Test
    public void return_version_and_item_type_16_when_uri_has_version_but_not_item_type () throws ParseException {
        final String uri = String.format("tcm:%s-%s-v%s", PUBLICATION_ID, ITEM_ID, VERSION);

        TCMURI tcmUri = new TCMURI(uri);
        assertThat(tcmUri.getItemType(), is(16));
        assertThat(tcmUri.getVersion(), is(VERSION));
    }

    @Test (expected = IllegalArgumentException.class)
    public void throws_exception_when_long_can_not_be_converted_to_int () {
        TCMURI.safeLongToInt(Long.MAX_VALUE);
        TCMURI.safeLongToInt(Long.MIN_VALUE);
    }

    @Test
    public void create_tcmUri_with_builder () throws ParseException {
        final String uri = String.format("tcm:%s-%s-%s-v%s", PUBLICATION_ID, ITEM_ID, ITEM_TYPE, VERSION);

        TCMURI tcmUri = new TCMURI.Builder(uri).create();

        assertThat(tcmUri.getPublicationId(), is(PUBLICATION_ID));
        assertThat(tcmUri.getItemId(), is(ITEM_ID));
        assertThat(tcmUri.getItemType(), is(ITEM_TYPE));
        assertThat(tcmUri.getVersion(), is(VERSION));
    }

    @Test
    public void create_tcm_uri_with_builder_overwrite_version_from_url () throws ParseException {
        final String uri = String.format("tcm:%s-%s-%s-v%s", PUBLICATION_ID, ITEM_ID, ITEM_TYPE, VERSION);
        final int version = 4;

        TCMURI tcmUri = new TCMURI.Builder(uri).version(version).create();

        assertThat(tcmUri.getPublicationId(), is(PUBLICATION_ID));
        assertThat(tcmUri.getItemId(), is(ITEM_ID));
        assertThat(tcmUri.getItemType(), is(ITEM_TYPE));
        assertThat(tcmUri.getVersion(), is(version));
    }

    @Test
    public void build_with_only_mandatory_fields_and_default () throws ParseException {
        TCMURI tcmUri = new TCMURI.Builder(PUBLICATION_ID, ITEM_ID).create();

        assertThat(tcmUri.getPublicationId(), is(PUBLICATION_ID));
        assertThat(tcmUri.getItemId(), is(ITEM_ID));
        assertThat(tcmUri.getItemType(), is(DEFAULT_ITEM_TYPE));
        assertThat(tcmUri.getVersion(), is(DEFAULT_VERSION));
    }

    @Test
    public void create_using_uri_and_version () throws ParseException {
        final String uri = String.format("tcm:%s-%s-v%s", PUBLICATION_ID, ITEM_ID, VERSION);
        final int version = 4;

        TCMURI tcmUri = new TCMURI(uri, version);
        assertThat(tcmUri.getItemType(), is(16));
        assertThat(tcmUri.getVersion(), is(4));
    }


    @Test
    public void convert_safely_long_to_int () {
        assertThat(TCMURI.safeLongToInt(Integer.MAX_VALUE), is(Integer.MAX_VALUE));
    }
}