package org.dd4t.core.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Calendar;

public class DateUtils {

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /**
     * Helper method to convert the date to string
     *
     * @param date
     * @return
     */
    public static String convertDateToString(DateTime date) {
        return DateTimeFormat.forPattern(DATE_PATTERN).print(date);
    }

    /**
     * Helper method to convert java.sql.Timestamp to String
     *
     * @param timestamp
     * @return
     */
    public static String convertTimstampToString(Timestamp timestamp) {
        return convertDateToString(new DateTime(timestamp));
    }

    /**
     * Helper method to convert the date from the xml to date
     *
     * @param date
     * @return
     */
    public static DateTime convertStringToDate(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        String hour = date.substring(11, 13);
        String min = date.substring(14, 16);
        String sec = date.substring(17, 19);
        String milis = null;
        if (date.length() > 19) {
            milis = date.substring(20);
        }

        Calendar caldate = Calendar.getInstance();
        caldate.set(Calendar.YEAR, Integer.parseInt(year));
        caldate.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        caldate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        caldate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        caldate.set(Calendar.MINUTE, Integer.parseInt(min));
        caldate.set(Calendar.SECOND, Integer.parseInt(sec));
        if (milis == null) {
            caldate.set(Calendar.MILLISECOND, 0);
        } else {
            caldate.set(Calendar.MILLISECOND, Integer.parseInt(milis));
        }

        return new DateTime(caldate);
    }

}
