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


/**
 * A {@code ConversionExcpetion} indicates that something has gone wrong in the
 * conversion process.
 */
public class ConversionException extends Exception {

	@java.io.Serial
	private static final long serialVersionUID = -6836024638942102856L;

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 */
	public ConversionException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * 
	 * @param message the detailed message
	 * @param cause   the cause
	 */
	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message the detailed message
	 */
	public ConversionException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message of
	 * {@code (cause==null ? null : cause.toString())} (which typically contains the
	 * class and detail message of {@code cause}). This constructor is useful for
	 * exceptions that are little more than wrappers for other exceptions.
	 * 
	 * @param cause the cause
	 */
	public ConversionException(Throwable cause) {
		super(cause);
	}
}
