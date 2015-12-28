/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.core.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Timestamp;

public class DateUtils {
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATE_PATTERN);

    private DateUtils () {
    }

    /**
     * Helper method to convert the date to string
     *
     * @param date the Joda DateTime object
     * @return the date as String, formatted according to the date pattern.
     */
    public static String convertDateToString (DateTime date) {
        return DATE_TIME_FORMATTER.print(date);
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
     * @param date the date string.
     * @return the Joda DateTime
     */
    public static DateTime convertStringToDate (String date) {
        if (date.length() > 19) {
            return DATE_TIME_FORMATTER.parseDateTime(date);
        } else {
            return DATE_TIME_FORMATTER.parseDateTime((date + ".00"));
        }
    }
}
