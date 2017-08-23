package org.dd4t.core.util;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;

@RunWith (Theories.class)
public class DateUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(DateUtilsTest.class);
    public static final String EXPECTED_DATE_STAMP = "2014-06-24T15:45:32.325";
    public static final int TEST_YEAR = 2014;
    public static final int TEST_MONTH = 5;
    public static final int TEST_DAY = 24;
    public static final int TEST_HOUR = 15;
    public static final int TEST_MINUTE = 45;
    public static final int TEST_SECOND = 32;
    public static final int TEST_MILLIS = 325;
    private DateTime beginTime;
    private DateTime endTime;

    @Before
    public void setUp () {

        beginTime = DateTime.now().minusWeeks(1);
        endTime = DateTime.now().plusWeeks(1);
        LOG.info("Done setting up dates.");
    }

    @Test
    public void testConvertDateToString () {

        // Test the date
        Calendar calendar = Calendar.getInstance();

        // 2014-06-24T15:45:32.325 give it a fixed date
        calendar.set(Calendar.YEAR, TEST_YEAR);
        calendar.set(Calendar.MONTH, TEST_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, TEST_DAY);

        calendar.set(Calendar.HOUR_OF_DAY, TEST_HOUR);
        calendar.set(Calendar.MINUTE, TEST_MINUTE);
        calendar.set(Calendar.SECOND, TEST_SECOND);
        calendar.set(Calendar.MILLISECOND, TEST_MILLIS);

        // Call the actual method
        String result = DateUtils.convertDateToString(new DateTime(calendar));

        // Test the outcome of the date, if it is in the correct format
        Assert.assertEquals(EXPECTED_DATE_STAMP, result);

    }

    @Test
    public void testConvertTimstampToString () {
        // Test the date
        Calendar calendar = Calendar.getInstance();

        // 2014-06-24T15:45:32.325 give it a fixed date
        calendar.set(Calendar.YEAR, TEST_YEAR);
        calendar.set(Calendar.MONTH, TEST_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, TEST_DAY);

        calendar.set(Calendar.HOUR_OF_DAY, TEST_HOUR);
        calendar.set(Calendar.MINUTE, TEST_MINUTE);
        calendar.set(Calendar.SECOND, TEST_SECOND);
        calendar.set(Calendar.MILLISECOND, TEST_MILLIS);

        // Call the actual method
        String result = DateUtils.convertSqlTimestampToString(new Timestamp(calendar.getTimeInMillis()));

        // Test the outcome of the date, if it is in the correct format
        Assert.assertEquals(EXPECTED_DATE_STAMP, result);

    }

    @Test
    public void testConvertStringToDate () {
        DateTime date = DateUtils.convertStringToDate(EXPECTED_DATE_STAMP);

        Calendar cal = date.toGregorianCalendar();


        Assert.assertEquals(TEST_YEAR, cal.get(Calendar.YEAR));
        Assert.assertEquals(TEST_MONTH, cal.get(Calendar.MONTH));
        Assert.assertEquals(TEST_DAY, cal.get(Calendar.DAY_OF_MONTH));

        Assert.assertEquals(TEST_HOUR, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(TEST_MINUTE, cal.get(Calendar.MINUTE));
        Assert.assertEquals(TEST_SECOND, cal.get(Calendar.SECOND));
        Assert.assertEquals(TEST_MILLIS, cal.get(Calendar.MILLISECOND));
    }

    @Test
    public void testRandomConversions () {
        for (int i = 0; i < 10; i++) {
            DateTime random = DateTime.now().withMillis(getRandomTimeBetweenTwoDates(beginTime, endTime));
            String randomDateString = DateUtils.convertDateToString(random);

            DateTime date = DateUtils.convertStringToDate(randomDateString);

            Calendar cal = date.toGregorianCalendar();

            Assert.assertEquals(random.getYear(), cal.get(Calendar.YEAR));
            Assert.assertEquals(random.getMonthOfYear(), cal.get(Calendar.MONTH) + 1);
            Assert.assertEquals(random.getDayOfMonth(), cal.get(Calendar.DAY_OF_MONTH));

            Assert.assertEquals(random.getHourOfDay(), cal.get(Calendar.HOUR_OF_DAY));
            Assert.assertEquals(random.getMinuteOfHour(), cal.get(Calendar.MINUTE));
            Assert.assertEquals(random.getSecondOfMinute(), cal.get(Calendar.SECOND));
            Assert.assertEquals(random.getMillisOfSecond(), cal.get(Calendar.MILLISECOND));
        }
    }


    /**
     * Method should generate random number that represents
     * a time between two dates.
     *
     * @return time difference
     */
    private long getRandomTimeBetweenTwoDates (DateTime begin, DateTime end) {
        long diff = end.getMillis() - begin.getMillis() + 1;
        return begin.getMillis() + (long) (Math.random() * diff);
    }
}