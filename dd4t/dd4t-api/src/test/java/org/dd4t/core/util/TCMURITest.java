package org.dd4t.core.util;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TCMURITest extends TestCase{

    @Before
    public void setUp() throws Exception {
        // TODO this function is here for reference only: to show this is called before tests are started and you can do any setup.
    }

    @After
    public void tearDown() throws Exception {
        // TODO this function is here for reference only: to show this is called after the tests have been run
    }

    @Test
    public void testLoad() throws Exception {
    }

    @Test
    public void testToString() throws Exception {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        assertEquals("tcm:12-14-6", tcmUri.toString());

        TCMURI tcmUriByString = new TCMURI("tcm:12-14-6");
        assertEquals("tcm:12-14-6", tcmUriByString.toString());
    }

    @Test
    public void testGetItemType() throws Exception {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        assertEquals(6, tcmUri.getItemType());
    }

    @Test
    public void testSetItemType() throws Exception {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        tcmUri.setItemType(7);
        assertEquals(7, tcmUri.getItemType());
    }

    @Test
    public void testGetItemId() throws Exception {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        assertEquals(14, tcmUri.getItemId());
    }

    @Test
    public void testSetItemId() throws Exception {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        tcmUri.setItemId(10);
        assertEquals(10, tcmUri.getItemId());
    }

    @Test
    public void testGetPublicationId() throws Exception {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        assertEquals(12, tcmUri.getPublicationId());
    }

    @Test
    public void testSetPublicationId() throws Exception {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        tcmUri.setPublicationId(11);
        assertEquals(11, tcmUri.getPublicationId());
    }

    @Test
    public void testGetVersion() throws Exception {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        assertEquals(2, tcmUri.getVersion());
    }

    @Test
    public void testSetVersion() throws Exception {
        TCMURI tcmUri = new TCMURI(12, 14, 6, 2);
        tcmUri.setVersion(3);
        assertEquals(3, tcmUri.getVersion());
    }

    @Test
    public void testEquals() throws Exception {
        TCMURI tcmUri1 = new TCMURI(12, 14, 6, 2);
        TCMURI tcmUri2 = new TCMURI(12, 14, 6, 3);
        TCMURI tcmUri3 = new TCMURI(12, 14, 5, 2);

        assertEquals(true, tcmUri1.equals(tcmUri2));
        assertEquals(false, tcmUri1.equals(tcmUri3));
    }

    @Test
    public void testCompareTo() throws Exception {
        TCMURI tcmUri1 = new TCMURI(12, 14, 6, 1);
        TCMURI tcmUri1a = new TCMURI(12, 14, 5, 1);
        TCMURI tcmUri1b = new TCMURI(12, 15, 6, 1);
        TCMURI tcmUri1c = new TCMURI(12, 13, 6, 1);

        TCMURI tcmUri2 = new TCMURI(12, 14, 6, 2);
        TCMURI tcmUri2a = new TCMURI(12, 14, 6, 2);

        TCMURI tcmUri3 = new TCMURI(12, 14, 6, 3);
        TCMURI tcmUri3a = new TCMURI(12, 14, 7, 3);

        assertEquals(-1, tcmUri1.compareTo(tcmUri1b));
        assertEquals(1, tcmUri1.compareTo(tcmUri1c));

        //TODO why should the next test return 0?
        assertEquals(0, tcmUri1.compareTo(tcmUri1a));

        assertEquals(0, tcmUri2.compareTo(tcmUri2a));
        //TODO why should the next test return 0?
        assertEquals(0, tcmUri3.compareTo(tcmUri3a));

        assertEquals(0, tcmUri1.compareTo(tcmUri2));
        assertEquals(0, tcmUri2.compareTo(tcmUri2));
        assertEquals(true, tcmUri2.equals(tcmUri3));
        assertEquals(false, tcmUri2.equals(tcmUri3a));
    }
}