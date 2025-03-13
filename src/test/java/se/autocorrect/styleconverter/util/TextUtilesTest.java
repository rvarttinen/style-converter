package se.autocorrect.styleconverter.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TextUtilesTest {

	@Test
	void testCleanUpTextField() {
	}

	@ParameterizedTest
	@CsvSource({ 
		"name name, name",
		"name  name, name",
		"housenumber, housenumber",
		"geeksfor geeks, geeksfor geeks",
		"\"\", \"\"" // Handle empty string
		})
	void testRemoveDuplicates(String input, String expected) {
		
		String actual = TextUtils.removeDuplicates(input);
		
		assertEquals(expected, actual);
	}
}
