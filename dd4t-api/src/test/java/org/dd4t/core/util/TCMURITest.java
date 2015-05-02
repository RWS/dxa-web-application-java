package org.dd4t.core.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.text.ParseException;

public class TCMURITest extends TestCase{

    public static final int PUBLICATION_ID = 12;
    public static final int ITEM_ID = 14;
    public static final int ITEM_TYPE = 16;
    public static final int VERSION = 2;
    public static final String EXPECTED = "tcm:12-14-16";

    @Test
    public void testToString() throws ParseException {
        TCMURI tcmUri = new TCMURI(PUBLICATION_ID, ITEM_ID, ITEM_TYPE, VERSION);
        assertEquals(EXPECTED, tcmUri.toString());

        TCMURI tcmUriByString = new TCMURI(EXPECTED);
        assertEquals(EXPECTED, tcmUriByString.toString());
    }

    @Test
    public void testGetItemType() throws ParseException {
        TCMURI tcmUri = new TCMURI(PUBLICATION_ID, ITEM_ID, ITEM_TYPE, VERSION);
        assertEquals(ITEM_TYPE, tcmUri.getItemType());
    }

    @Test
    public void testGetItemId() throws ParseException {
        TCMURI tcmUri = new TCMURI(PUBLICATION_ID, ITEM_ID, ITEM_TYPE, VERSION);
        assertEquals(ITEM_ID, tcmUri.getItemId());
    }

    @Test
    public void testGetPublicationId() throws ParseException {
        TCMURI tcmUri = new TCMURI(PUBLICATION_ID, ITEM_ID, ITEM_TYPE, VERSION);
        assertEquals(PUBLICATION_ID, tcmUri.getPublicationId());
    }

    @Test
    public void testGetVersion() throws ParseException {
        TCMURI tcmUri = new TCMURI(PUBLICATION_ID, ITEM_ID, ITEM_TYPE, VERSION);
        assertEquals(VERSION, tcmUri.getVersion());
    }
}