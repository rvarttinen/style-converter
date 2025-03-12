/*-
 *  
 * style-converter
 *  
 * Copyright (C) 2025 Autocorrect Design HB
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *  
 */
package se.autocorrect.styleconverter.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some collected useful string utilities.
 */
public final class StringUtils {

	/**
	 * An empty immutable {@code char} array.
	 */
	public static final char[] EMPTY_CHAR_ARRAY = {};

	private StringUtils() {
	}

	/**
	 * Convenience check whether a string is null or empty.
	 * 
	 * @param string the string to check
	 * @return {@code true} if the provided string is {@code null} or empty, {@code
	 *         false} otherwise
	 */
	public static boolean isNullOrEmpty(String string) {
		return string == null || string.isBlank();
	}

	/**
	 * Convenience check whether a string is not null nor empty.
	 * 
	 * @param string the string to check
	 * @return {@code true} if the provided string is not {@code null} nor empty,
	 *         {@code
	 *         false} otherwise
	 */
	public static boolean isNotNullNorEmpty(String string) {
		return !isNullOrEmpty(string);
	}

	/**
	 * Checks if the provided {@code String} contains any character from the given
	 * set of characters defined in a {@code String}
	 * 
	 * @param toCheck    the string to check
	 * @param searchCars the characters to check for
	 * @return {@code true} if any of the characters appearing the string is found
	 *         in the string to check
	 */
	public static boolean containsAny(String toCheck, String searchCars) {

		if (searchCars == null) {
			return false;
		}

		return containsAny(toCheck, toCharArray(searchCars));
	}

	/**
	 * Checks if the provided {@code String} contains any character from the given
	 * array of characters provided
	 * 
	 * @param toCheck    the string to check
	 * @param searchCars the characters to check for
	 * @return {@code true} if any of the characters appearing the array to check
	 *         for is found in the string to check
	 */
	public static boolean containsAny(String toCheck, char... searchChars) {

		final int searchLength = searchChars.length;

		if (isNullOrEmpty(toCheck) || searchLength == 0) {
			return false;
		}

		final int csLength = toCheck.length();
		final int csLast = csLength - 1;
		final int searchLast = searchLength - 1;

		for (int i = 0; i < csLength; i++) {

			final char ch = toCheck.charAt(i);

			for (int j = 0; j < searchLength; j++) {

				if (searchChars[j] == ch) {

					if (!Character.isHighSurrogate(ch)) {
						// ch is in the Basic Multilingual Plane
						return true;
					}

					if (j == searchLast) {
						// missing low surrogate, fine, like String.indexOf(String)
						return true;
					}

					if (i < csLast && searchChars[j + 1] == toCheck.charAt(i + 1)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Remove any duplicates from a {@code String}. This method removes only whole
	 * words which are the same. Any multiple spaces in between words will also be
	 * reduced to a single space in between words.
	 * 
	 * @param input the string to process for potential duplicates
	 * @return the resulting string without duplicates
	 */
	public static String removeDuplicates(String input) {

		String duplicatePattern = "\\b([\\w\\s']+) \\1\\b";
		Pattern p = Pattern.compile(duplicatePattern);

		// Remove any multiples of spaces in between words
		input = input.trim().replaceAll(" +", " ");

		String result = input;

		Matcher m = p.matcher(input);

		if (m.matches()) {
			result = m.group(1);
		}

		return result;
	}

	/**
	 * Converts the given {@code CharSequence} to a {@code char[]} .
	 *
	 * @param source the {@link CharSequence} to be processed.
	 */
	private static char[] toCharArray(final CharSequence source) {

		final int length = source.length();

		if (length == 0) {
			return EMPTY_CHAR_ARRAY;
		}

		if (source instanceof String strSouce) {
			return strSouce.toCharArray();
		}

		final char[] array = new char[length];

		for (int i = 0; i < length; i++) {
			array[i] = source.charAt(i);
		}

		return array;
	}
}
