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
package se.autocorrect.styleconverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.oscim.theme.ThemeFile;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import org.xmlunit.placeholder.PlaceholderDifferenceEvaluator;


class PoiTest extends StyleConverterTestBase {

	@Override
	String getJsonData() throws IOException {
		return readFile("/json/poi.json");
	}

	@Override
	void assertResult(ThemeFile result) throws Exception {

		String actual = xmlRenderThemeToString(result);

		String expected = readFile("/xml/expected/poi.xml");

		int numOfDiffs = detectDiff(actual, expected);

		assertEquals(0, numOfDiffs);
	}

	@Override
	protected int detectDiff(String actual, String expected) {

		Diff diff = DiffBuilder
				.compare(expected)
				.withTest(actual)
				.withDifferenceEvaluator(new PlaceholderDifferenceEvaluator())
				.ignoreWhitespace()
				.checkForSimilar()
				.build();

		Iterable<Difference> differences = diff.getDifferences();

		int numOfDiffs = 0;

		for (Difference difference : differences) {
			numOfDiffs++;
		}

		return numOfDiffs;
	}

	@Override
	protected boolean isActive() {
		return !inMavenContext();
	}
}

