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


import java.io.PrintStream;

import org.oscim.theme.ThemeFile;

import se.autocorrect.styleconverter.internal.DefaultJsonStyleConverter;

/**
 * A {@code StyleConverter} converts style data into a theme that can be loaded
 * in a map.
 */
@FunctionalInterface
public interface StyleConverter {

	/**
	 * Perform the conversion.
	 * 
	 * @param input style data for processing
	 * @return if successful a theme that can be loaded, {@code null} otherwise
	 * @throws Exception if an error occurred during the conversion process
	 */
	ThemeFile convert(String input) throws Exception;

	/**
	 * If the {@code StyleConverter} in question supports verbose output this method
	 * will activate this and allow verbose output to be written to a stream
	 * provided. Otherwise it has no effect, i.e. verbose is not active by default.
	 * 
	 * @param out the stream to use when writing verbose output
	 */
	default void verbose(PrintStream out) {
	}

	/**
	 * Set the URL provided for retrieving the style data. The URL is used for e.g.
	 * retrieving any sprite data to be used. A class implementing this interface
	 * may opt not to implement this method.
	 * 
	 * @param url the URL as string
	 */
	default void setUrl(String url) {
	}

	/**
	 * Dispose any resources, cached files, etc. A class implementing this interface
	 * may opt not to implement this method.
	 */
	default void dispose() {
	}
	
	/**
	 * Retrieve an instance of the default converter taking a JSON style as input. 
	 * 
	 * @return the default JSON converter
	 */
	static StyleConverter getDefaultJsonConverter() {
		return new DefaultJsonStyleConverter();
	}
}
