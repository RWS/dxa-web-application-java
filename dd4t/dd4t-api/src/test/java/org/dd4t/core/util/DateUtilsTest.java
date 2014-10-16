package org.dd4t.core.util;

import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.Calendar;

@RunWith(Theories.class)
public class DateUtilsTest extends TestCase {

    private DateUtils instance;
    private DateTime beginTime;
    private DateTime endTime;

    @Before
    public void setUp() throws Exception {
        // TODO this function is here for reference only: it now generates a simple DateUtils class
        // We can do this because the DateUtils holds no state. Better to normally initialize in the test function because you would have to test each test with a clean state.
        instance = new DateUtils();

        beginTime = DateTime.now().minusWeeks(1);
        endTime = DateTime.now().plusWeeks(1);
    }

    @After
    public void tearDown() throws Exception {
        // TODO this function is here for reference only: to show this is called after the tests have been run
    }

    @Test
    public void testConvertDateToString() throws Exception {

        // Test the date
        Calendar calendar = Calendar.getInstance();

        // 2014-06-24T15:45:32.325 give it a fixed date
        calendar.set(Calendar.YEAR, 2014);
        calendar.set(Calendar.MONTH, 5); // Is Juni the 6th month
        calendar.set(Calendar.DAY_OF_MONTH, 24);

        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 45);
        calendar.set(Calendar.SECOND, 32);
        calendar.set(Calendar.MILLISECOND, 325);

        // Call the actual method
        String result = instance.convertDateToString(new DateTime(calendar));

        // Test the outcome of the date, if it is in the correct format
        assertEquals("2014-06-24T15:45:32.325", result);

    }

    @Test
    public void testConvertTimstampToString() throws Exception {
        // Test the date
        Calendar calendar = Calendar.getInstance();

        // 2014-06-24T15:45:32.325 give it a fixed date
        calendar.set(Calendar.YEAR, 2014);
        calendar.set(Calendar.MONTH, 5); // Is Juni the 6th month
        calendar.set(Calendar.DAY_OF_MONTH, 24);

        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 45);
        calendar.set(Calendar.SECOND, 32);
        calendar.set(Calendar.MILLISECOND, 325);

        // Call the actual method
        String result = instance.convertTimstampToString(new Timestamp(calendar.getTimeInMillis()));

        // Test the outcome of the date, if it is in the correct format
        assertEquals("2014-06-24T15:45:32.325", result);

    }

    @Test
    public void testConvertStringToDate() throws Exception {
        DateTime date = instance.convertStringToDate("2014-06-24T15:45:32.325");

        Calendar cal = date.toGregorianCalendar();

        assertEquals(2014, cal.get(Calendar.YEAR));
        assertEquals(5, cal.get(Calendar.MONTH));
        assertEquals(24, cal.get(Calendar.DAY_OF_MONTH));

        assertEquals(15, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, cal.get(Calendar.MINUTE));
        assertEquals(32, cal.get(Calendar.SECOND));
        assertEquals(325, cal.get(Calendar.MILLISECOND));
    }

    @Test
    public void testRandomConversions() throws Exception {
        for (int i = 0; i < 10; i++) {
            DateTime random = DateTime.now().withMillis(getRandomTimeBetweenTwoDates(beginTime, endTime));
            String randomDateString = instance.convertDateToString(random);

            DateTime date = instance.convertStringToDate(randomDateString);

            Calendar cal = date.toGregorianCalendar();

            assertEquals(random.getYear(), cal.get(Calendar.YEAR));
            assertEquals(random.getMonthOfYear(), cal.get(Calendar.MONTH) + 1);
            assertEquals(random.getDayOfMonth(), cal.get(Calendar.DAY_OF_MONTH));

            assertEquals(random.getHourOfDay(), cal.get(Calendar.HOUR_OF_DAY));
            assertEquals(random.getMinuteOfHour(), cal.get(Calendar.MINUTE));
            assertEquals(random.getSecondOfMinute(), cal.get(Calendar.SECOND));
            assertEquals(random.getMillisOfSecond(), cal.get(Calendar.MILLISECOND));
        }
    }



    /**
     * Method should generate random number that represents
     * a time between two dates.
     *
     * @return
     */
    private long getRandomTimeBetweenTwoDates (DateTime begin, DateTime end) {
        long diff = end.getMillis() - begin.getMillis() + 1;
        return begin.getMillis() + (long) (Math.random() * diff);
    }
}