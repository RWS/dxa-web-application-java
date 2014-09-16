/**  
 *  Copyright 2011 Capgemini & SDL
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dd4t.core.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	/**
	 * Helper method to convert the date from the xml to date
	 * 
	 * @param date
	 * @return
	 */
	public static Date convertStringToDate(String date) {

		String year = date.substring(0, 4);
		String month = date.substring(5, 7);
		String day = date.substring(8, 10);
		String hour = date.substring(11, 13);
		String min = date.substring(14, 16);
		String sec = date.substring(17, 19);

		Calendar caldate = Calendar.getInstance();
		caldate.set(Calendar.YEAR, Integer.parseInt(year));
		caldate.set(Calendar.MONTH, Integer.parseInt(month) -1);
		caldate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
		caldate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
		caldate.set(Calendar.MINUTE, Integer.parseInt(min));
		caldate.set(Calendar.SECOND, Integer.parseInt(sec));

		return caldate.getTime();

	}

}
