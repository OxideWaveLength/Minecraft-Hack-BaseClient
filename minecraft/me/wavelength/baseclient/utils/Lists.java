package me.wavelength.baseclient.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lists {

	public static List<Object> createList(Object... lines) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < lines.length; i++) {
			list.add(lines[i]);
		}
		return list;
	}

	public static List<String> stringToList(String string, String separator) {
		List<String> result = new ArrayList<String>();

		String[] splittedString = string.split(separator);
		for (int i = 0; i < splittedString.length; i++) {
			result.add(splittedString[i]);
		}

		return result;
	}

	public static List<String> stringArrayToList(String... string) {
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < string.length; i++) {
			result.add(string[i]);
		}

		return result;
	}

	public static List<Object> arrayToList(Object... object) {
		List<Object> result = new ArrayList<Object>();
		for (int i = 0; i < object.length; i++) {
			result.add(object[i]);
		}

		return result;
	}

	public static String listStringToString(List<String> list) {
		return listStringToString(list, ", ");
	}

	public static String listStringToString(List<String> list, String splitter) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			String string = list.get(i);
			if (string == null)
				continue;
			result += (i == 0 ? "" : splitter) + string;
		}

		return result;
	}

	public static String objectArrayToString(Object[] array) {
		return Arrays.toString(array).replace("[", "").replace("]", "");
	}

	public static String stringArrayToString(Object splitter, String... array) {
		return String.join((String) splitter, array);
	}

	public static String[] stringToArray(String string, String separator) {
		String[] result = new String[string.split(separator).length];

		for (int i = 0; i < result.length; i++) {
			result[i] = string.split(separator)[i];
		}

		return result;
	}

	public static Object[] removeElementFromArray(Object[] array, int... indexes) {
		Object[] finalArray = new Object[array.length - 1];
		for (int i = 0; i < array.length; i++) {
			for (int x = 0; x < indexes.length; x++) {
				if (i == indexes[x])
					continue;
				finalArray[i - 1] = array[i];
			}
		}
		return finalArray;
	}

	public static List<String> add(List<String> list, Object obj) {
		if (list == null)
			list = new ArrayList<String>();
		list.add(obj.toString());
		return list;
	}

	public static Object[] listToArray(List<?> list) {
		return list.toArray(new Object[list.size()]);
	}

	public static List<?> clone(List<?> original) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < original.size(); i++) {
			list.add(original.get(i));
		}

		return list;
	}

}