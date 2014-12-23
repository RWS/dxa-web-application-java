package org.dd4t.core.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.text.ParseException;

public class TCMURITest extends TestCase{

    @Test
    public void testToString() throws ParseException {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        assertEquals("tcm:12-14-6", tcmUri.toString());

        TCMURI tcmUriByString = new TCMURI("tcm:12-14-6");
        assertEquals("tcm:12-14-6", tcmUriByString.toString());
    }

    @Test
    public void testGetItemType() throws ParseException {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        assertEquals(6, tcmUri.getItemType());
    }

    @Test
    public void testGetItemId() throws ParseException {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        assertEquals(14, tcmUri.getItemId());
    }

    @Test
    public void testGetPublicationId() throws ParseException {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        assertEquals(12, tcmUri.getPublicationId());
    }

    @Test
    public void testGetVersion() throws ParseException {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        assertEquals(2, tcmUri.getVersion());
    }
}