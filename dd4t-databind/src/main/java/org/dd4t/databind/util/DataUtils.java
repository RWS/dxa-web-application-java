package org.dd4t.databind.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class DataUtils {
	private DataUtils() {

	}
	public static List<String> convertToNonEmptyList(String[] items) {
		final List<String> list = new ArrayList<>();

		for(final String s : items) {
			if(StringUtils.isNotEmpty(s)) {
				list.add(s);
			}
		}
		return list;
	}
}
