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

import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;
import org.oscim.theme.ThemeFile;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;
import org.xmlunit.diff.DifferenceEvaluators;
import org.xmlunit.placeholder.PlaceholderDifferenceEvaluator;

import se.autocorrect.styleconverter.internal.DefaultJsonStyleConverter;

/**
 * This is the base and entry point for all the source layer specific test cases. 
 */
public abstract class StyleConverterTestBase {

	protected StyleConverter eut;

	@AfterEach
	void tearDown() {
		this.eut = null;
	}

	/**
	 * Execute the test, retrieve the relevant test data and assert the result
	 * thereof. This is the entry point for executing a test.
	 *
	 * @throws Exception            if something goes wrong during the conversion
	 *                              and thus fails the test
	 * @throws TestAbortedException if the test case is aborted, i.e. being
	 *                              deactivated
	 */
	@Test
	void executeTest() throws Exception {

		if (!isActive()) {
			throw new TestAbortedException("Test case deactivated: " + this.getClass().getName());
		}

		String json = getJsonData();

		this.eut = new DefaultJsonStyleConverter();

		ThemeFile theme = eut.convert(json);

		assertResult(theme);
	}

	/**
	 * Concrete subclasses implement this method returning the relevant JSON-data
	 * for exercising the {@code StyleConverter}.
	 *
	 * @return the JSON-data as string for use in the test case in question
	 * @throws IOException if there is a problem retrieving the json data
	 */
	abstract String getJsonData() throws IOException;

	/**
	 * Assert the correctness of the resulting theme that is the outcome from the
	 * conversion.
	 *
	 * @param result the resulting theme
	 * @throws Exception
	 */
	abstract void assertResult(ThemeFile result) throws Exception;

	/**
	 * A subclass may override this method to indicate that the test case should not
	 * be executed, due to e.g not fully implemented yet or awaiting more
	 * requirements for implementation, execution environment, etc.
	 *
	 * @return {@code true} if this test shall be executed, {@code false} otherwise
	 */
	protected boolean isActive() {
		return true;
	}

	/**
	 * Extract the render theme to a corresponding string.
	 *
	 * @param theme the theme
	 * @return the theme represented by a string
	 * @throws IOException if something goes wrong
	 */
	protected String xmlRenderThemeToString(ThemeFile theme) throws IOException {

		InputStream renderThemeAsStream = theme.getRenderThemeAsStream();

		int bufferSize = 1024;
		char[] buffer = new char[bufferSize];
		StringBuilder out = new StringBuilder();

		Reader in = new InputStreamReader(renderThemeAsStream, StandardCharsets.UTF_8);

		for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0;) {
			out.append(buffer, 0, numRead);
		}

		return out.toString();
	}

	/**
	 * Read the contents of a file.
	 *
	 * @param fileName name of the file
	 * @return the file contents as a string
	 * @throws IOException if something goes wrong
	 */
	protected String readFile(String fileName) throws IOException {

		InputStream inputStream = StyleConverterTestBase.class.getResourceAsStream(fileName);

		if(inputStream == null) {
			fail("Could not read file: " + fileName);
		}

		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

			return bufferedReader.lines().collect(Collectors.joining("\n"));
		}
	}

	/**
	 * Calculate the number of diffs between an actual and the expected. This check will
	 * ignore whitespace and check that the two strings are similar, not necessarily
	 * identical though.
	 *
	 * @param actual   the actual produced xml from the convereion under test
	 * @param expected the expected xml
	 * @return the number of differences, 0 if no differences
	 */
	protected int detectDiff(String actual, String expected) {

		Diff diff = DiffBuilder
				.compare(expected)
				.withTest(actual)
				.withDifferenceEvaluator(DifferenceEvaluators.chain(
						DifferenceEvaluators.Default, new PlaceholderDifferenceEvaluator()))
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

	/**
	 * For some reason the {@code @DisabledIfEnvironmentVariable} annotation does not
	 * seem to work with this test case so we resort to this clumsier solution for
	 * now.
	 *
	 * @return {@code true} if this test is executing within a Maven context,
	 *         {@code false} otherwise
	 */
	protected boolean inMavenContext() {
		return System.out.getClass().getName().contains("maven");
	}
}
