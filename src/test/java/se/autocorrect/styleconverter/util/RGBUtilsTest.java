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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RGBUtilsTest {

	@ParameterizedTest
	@MethodSource("hslAndCorrespondingRgbValues")
	void testHslToRgbHexString(Float[] hsl, Integer[] rgb) {

		String expected = rgbStringFromInts(rgb[0], rgb[1], rgb[2]);
		String rgbHexString = RGBUtils.hslToRgbHexString(hsl[0], hsl[1], hsl[2]);

		assertAll(
				() -> assertTrue(rgbHexString.startsWith("#")), 
				() -> {
					
					// Remove prepended "#" hex indicator
					String actual = rgbHexString.substring(1);
					assertEquals(expected, actual);
		});
	}
	
	@ParameterizedTest
	@MethodSource("hslaAndCorrespondingRgbaValues")
	void testHslaToRgbaHexString(Float[] hsla, Integer[] rgba, float alpha) {

		String expected = rgbaStringFromIntsAndAlpha(rgba[0], rgba[1], rgba[2], alpha);
		String rgbaHexString = RGBUtils.hslaToRgbaHexString(hsla[0], hsla[1], hsla[2], hsla[3]);

		assertAll(
				() -> assertTrue(rgbaHexString.startsWith("#")), 
				() -> {
					
					// Remove prepended "#" hex indicator
					String actual = rgbaHexString.substring(1);
					assertEquals(expected, actual);
		});
	}

	private String rgbStringFromInts(Integer r, Integer g, Integer b) {
		return String.format("%02x%02x%02x", r, g, b);
	}
	
	private String rgbaStringFromIntsAndAlpha(Integer r, Integer g, Integer b, float alpha) {
		
		int a = (int) Math.floor(alpha >= 1.0 ? 255 : alpha * 256.0);
		
		return String.format("%02x%02x%02x%02x", a, r, g, b);
	}

	static Stream<Arguments> hslAndCorrespondingRgbValues() {
		
		/*
		 * h: 0, s: 100, l: 100 -> RGB(255, 255, 255)
		 * h: 360, s: 0, l: 0 -> RGB(0, 0, 0)
		 * h: 0, s: 50, l: 50 -> RGB(191, 64, 64)
		 * h: 50, s: 60, l: 60 -> RGB(214, 194, 92)
		 */
		
	    return Stream.of(
	        arguments(new Float [] {0.0f, 100.0f, 100.0f}, new Integer [] {255, 255, 255}),
	        arguments(new Float [] {360.0f, 0.0f, 0.0f}, new Integer [] {0, 0, 0}),
	        arguments(new Float [] {0.0f, 50.0f, 50.0f}, new Integer [] {191, 64, 64}),
			arguments(new Float[] { 50.0f, 60.0f, 60.0f }, new Integer[] { 214, 194, 92 })
		);
	}

	static Stream<Arguments> hslaAndCorrespondingRgbaValues() {
		
		/*
		 * hsla(0, 0%, 100%, 0) -> rgba(255, 255, 255, 0)
		 * hsla(0, 0%, 0%, 0) -> rgba(0, 0, 0, 0)
		 * hsla(0, 50%, 60%, 0.6) -> rgba(204, 102, 102, 0.6)
		 * hsla(278, 70%, 60%, 0.4) -> rgba(172, 82, 224, 0.4)
		 */

		return Stream.of(
				arguments(new Float[] { 0.0f, 0.0f, 100.0f, 0.0f }, new Integer[] { 255, 255, 255}, 0.0f),
				arguments(new Float[] { 0.0f, 0.0f, 0.0f, 0.0f }, new Integer[] { 0, 0, 0 }, 0.0f),
				arguments(new Float[] { 0.0f, 50.0f, 60.0f, 0.6f }, new Integer[] { 204, 102, 102 }, 0.6f),
				arguments(new Float[] { 278.0f, 70.0f, 60.0f, 0.4f }, new Integer[] { 172, 82, 224 }, 0.4f)
		);
	}
}
