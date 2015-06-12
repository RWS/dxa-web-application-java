package org.dd4t.core.util;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DateUtilsTest {
    public static final String timeWithMS = "2015-07-02T19:30:02.24";
    public static final String timeWithoutMS = "2015-07-04T19:30:02";

    @Test
    public void testConvertStringToDateWithMS() {
        DateTime date = DateUtils.convertStringToDate(timeWithMS);

        assertNotNull("Unexpected null result in timeWithMS", date);
        assertEquals("Invalid ConvertStringToDateWithMS month", 2, date.getDayOfMonth());
    }

    @Test
    public void testConvertStringToDateWithoutMS() {
        DateTime date = DateUtils.convertStringToDate(timeWithoutMS);

        assertNotNull("Unexpected null result in timeWithoutMS", date);
        assertEquals("Invalid ConvertStringToDateWithMS month", 4, date.getDayOfMonth());
    }
}
