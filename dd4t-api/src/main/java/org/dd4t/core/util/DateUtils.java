package org.dd4t.core.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Calendar;

public class DateUtils {


    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private DateUtils(){

    }
    /**
     * Helper method to convert the date to string
     *
     * @param date the Joda DateTime object
     * @return the date as String, formatted according to the date pattern.
     */
    public static String convertDateToString(DateTime date) {
        return DateTimeFormat.forPattern(DATE_PATTERN).print(date);
    }

    /**
     * Helper method to convert java.sql.Timestamp to String
     *
     * @param timestamp the Joda Timestamp
     * @return the timestamp as String, formatted according to the date pattern.
     */
    public static String convertSqlTimestampToString (Timestamp timestamp) {
        return convertDateToString(new DateTime(timestamp));
    }

    /**
     * Helper method to convert the date from the xml to date
     *
     * TODO: Redo with a pattern String.
     * @param date the date string.
     * @return the Joda DateTime
     */
    public static DateTime convertStringToDate(String date) {
        String year = date.substring(0, 4);
        String month = date.substring(5, 7);
        String day = date.substring(8, 10);
        String hour = date.substring(11, 13);
        String min = date.substring(14, 16);
        String sec = date.substring(17, 19);
        String millis = null;

        if (date.length() > 19) {
            millis = date.substring(20);
        }

        Calendar caldate = Calendar.getInstance();
        caldate.set(Calendar.YEAR, Integer.parseInt(year));
        caldate.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        caldate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        caldate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        caldate.set(Calendar.MINUTE, Integer.parseInt(min));
        caldate.set(Calendar.SECOND, Integer.parseInt(sec));
        if (millis == null) {
            caldate.set(Calendar.MILLISECOND, 0);
        } else {
            caldate.set(Calendar.MILLISECOND, Integer.parseInt(millis));
        }
        return new DateTime(caldate);
    }

}
