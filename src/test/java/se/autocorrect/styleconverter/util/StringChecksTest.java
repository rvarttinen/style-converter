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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StringChecksTest {

	@MethodSource("isNullOrEmptyValues")
	@ParameterizedTest
	void testIsNullOrEmpty(String str, boolean expected) {
		
		boolean actual = StringChecks.isNullOrEmpty(str);
		
		assertEquals(expected, actual);
	}

	@MethodSource("isNotNullNorEmptyValues")
	@ParameterizedTest
	void testIsNotNullNorEmpty(String str, boolean expected) {
		
		boolean actual = StringChecks.isNotNullNorEmpty(str);
		
		assertEquals(expected, actual);
	}

	static Stream<Arguments> isNullOrEmptyValues(){
		
		return Stream.of(
				arguments(null, true),
				arguments("", true),
				arguments(" ", true),
				arguments("string", false)
				);
	}
	
	static Stream<Arguments> isNotNullNorEmptyValues(){
		
		return Stream.of(
				arguments(null, false),
				arguments("", false),
				arguments(" ", false),
				arguments("string", true)
				);
	}
}
