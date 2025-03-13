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
 * Some useful utilities for processing text fields. 
 */
public final class TextUtils {
	
	private TextUtils() { }

	/**
	 * Clean up a text field from references to {@code :latin} and {@code :nonlatin.}
	 * 
	 * @param textField the field to clean
	 * @return as cleaned up field
	 */
	public static String cleanUpTextField(String textField) {
		
		if(textField.contains(":latin")) {
			textField = textField.replace(":latin", "");
		}
		
		if(textField.contains(":nonlatin")) {
			textField = textField.replace(":nonlatin", "");
		}

		textField = textField
				.replaceAll("[{}]", " ")
				.replace("\n", "")
				.replace("\r", "")
				.trim();
		
		return TextUtils.removeDuplicates(textField);
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
}
