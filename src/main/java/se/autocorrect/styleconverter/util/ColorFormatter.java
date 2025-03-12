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


public final class ColorFormatter {

	private ColorFormatter() {
	}

	public static String checkAndFormatColorString(String colorStr) {

		if (colorStr.contains("hsl(")) {

			String values = colorStr.substring("hsl(".length(), colorStr.length() - 1);
			values = values.replace('%', ' ');

			String[] valuesArr = values.split(",");

			colorStr = RGBUtils.hslToRgbHexString(Float.valueOf(valuesArr[0]), Float.valueOf(valuesArr[1]),
					Float.valueOf(valuesArr[2]));

		} else if (colorStr.contains("hsla(")) {

			String values = colorStr.substring("hsla(".length(), colorStr.length() - 1);
			values = values.replace('%', ' ');

			String[] valuesArr = values.split(",");

			colorStr = RGBUtils.hslaToRgbaHexString(Float.valueOf(valuesArr[0].trim()),
					Float.valueOf(valuesArr[1].trim()), Float.valueOf(valuesArr[2]),
					Float.valueOf(valuesArr[3].trim()));

		} else if (colorStr.contains("rgba(")) {

			String values = colorStr.substring("rgba(".length(), colorStr.length() - 1);
			String[] valuesArr = values.split(",");

			colorStr = RGBUtils.rgbaToHexString(Float.valueOf(valuesArr[3].trim()),
					new RGB(Integer.valueOf(valuesArr[0].trim()), Integer.valueOf(valuesArr[1].trim()),
							Integer.valueOf(valuesArr[2].trim())));

		} else if (colorStr.contains("rgb(")) {

			String values = colorStr.substring("rgb(".length(), colorStr.length() - 1);
			String[] valuesArr = values.split(",");

			colorStr = RGBUtils.rgbaToHexString(1.0f, new RGB(Integer.valueOf(valuesArr[0].trim()),
					Integer.valueOf(valuesArr[1].trim()), Integer.valueOf(valuesArr[2].trim())));

		} else if (colorStr.length() < 6 && colorStr.charAt(0) == '#') {

			/*
			 * Web safe colors uses three digits. It�s equivalent to the six-digit format,
			 * where each digit is duplicated to give the six-digit version. The value #46A
			 * is the same as #4466AA. Read more:
			 * https://html.com/blog/design/html-colors/#ixzz8fO8T60ds
			 */

			String firstDigit = colorStr.substring(1, 2);
			String secondDigit = colorStr.substring(2, 3);
			String thirdDigit = colorStr.substring(3, 4);

			StringBuilder sb = new StringBuilder();

			sb.append('#');
			sb.append(firstDigit);
			sb.append(firstDigit);

			sb.append(secondDigit);
			sb.append(secondDigit);

			sb.append(thirdDigit);
			sb.append(thirdDigit);

			colorStr = sb.toString();
		}

		return colorStr;
	}
}
