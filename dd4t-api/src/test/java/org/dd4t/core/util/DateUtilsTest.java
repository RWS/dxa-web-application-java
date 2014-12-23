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

@RunWith(Theories.class)
public class DateUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(DateUtilsTest.class);
    public static final String EXPECTED_DATE_STAMP = "2014-06-24T15:45:32.325";
    private DateTime beginTime;
    private DateTime endTime;

    @Before
    public void setUp() {

        beginTime = DateTime.now().minusWeeks(1);
        endTime = DateTime.now().plusWeeks(1);
        LOG.info("Done setting up dates.");
    }

    @Test
    public void testConvertDateToString() {

        // Test the date
        Calendar calendar = Calendar.getInstance();

        // 2014-06-24T15:45:32.325 give it a fixed date
        calendar.set(Calendar.YEAR, 2014);
        calendar.set(Calendar.MONTH, 5);
        calendar.set(Calendar.DAY_OF_MONTH, 24);

        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 45);
        calendar.set(Calendar.SECOND, 32);
        calendar.set(Calendar.MILLISECOND, 325);

        // Call the actual method
        String result = DateUtils.convertDateToString(new DateTime(calendar));

        // Test the outcome of the date, if it is in the correct format
        Assert.assertEquals(EXPECTED_DATE_STAMP, result);

    }

    @Test
    public void testConvertTimstampToString() {
        // Test the date
        Calendar calendar = Calendar.getInstance();

        // 2014-06-24T15:45:32.325 give it a fixed date
        calendar.set(Calendar.YEAR, 2014);
        calendar.set(Calendar.MONTH, 5);
        calendar.set(Calendar.DAY_OF_MONTH, 24);

        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 45);
        calendar.set(Calendar.SECOND, 32);
        calendar.set(Calendar.MILLISECOND, 325);

        // Call the actual method
        String result = DateUtils.convertSqlTimestampToString(new Timestamp(calendar.getTimeInMillis()));

        // Test the outcome of the date, if it is in the correct format
        Assert.assertEquals(EXPECTED_DATE_STAMP, result);

    }

    @Test
    public void testConvertStringToDate() {
        DateTime date = DateUtils.convertStringToDate(EXPECTED_DATE_STAMP);

        Calendar cal = date.toGregorianCalendar();


        Assert.assertEquals(2014, cal.get(Calendar.YEAR));
        Assert.assertEquals(5, cal.get(Calendar.MONTH));
        Assert.assertEquals(24, cal.get(Calendar.DAY_OF_MONTH));

        Assert.assertEquals(15, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(45, cal.get(Calendar.MINUTE));
        Assert.assertEquals(32, cal.get(Calendar.SECOND));
        Assert.assertEquals(325, cal.get(Calendar.MILLISECOND));
    }

    @Test
    public void testRandomConversions() {
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