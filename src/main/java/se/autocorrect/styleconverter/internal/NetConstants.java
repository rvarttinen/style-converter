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
package se.autocorrect.styleconverter.internal;


/**
 * A collection of useful constants when communicating with various net bound
 * resources.
 */
public final class NetConstants {

	private NetConstants() {
	}

	/**
	 * The accept header name.
	 */
	public static final String ACCEPT = "Accept";

	/**
	 * The accept encoding header name.
	 */
	public static final String ACCEPT_ENCODING = "Accept-Encoding";

	/**
	 * The user agent header name.
	 */
	public static final String USER_AGENT = "User-Agent";

	/**
	 * The content type header name.
	 */
	public static final String CONTENT_TYPE = "Content-Type";

	/**
	 * TRhe content length header name.
	 */
	public static final String CONTENT_LENGTH = "Content-Length";

	/**
	 * TRhe content length header name.
	 */
	public static final String CONTENT_ENCODING = "Content-Encoding";

	/**
	 * The plain text HTML content type or acceptance indicator ("text/html").
	 */
	public static final String TEXT_HTML = "text/html";

	/**
	 * The plain text content type or acceptance indicator ("text/plain").
	 */
	public static final String TEXT_PLAIN = "text/plain";

	/**
	 * The char-set indication for UTF-8.
	 */
	public static final String CHARSET_UTF_8 = ";charset=utf-8";

	/**
	 * The JSON content type or acceptance indicator ("application/json").
	 */
	public static final String APPLICATION_JSON = "application/json";

	/**
	 * The JSON content type or acceptance indicator
	 * ("application/json;charset=utf-8") with the UTF-8 charset.
	 */
	public static final String APPLICATION_JSON_UTF_8 = APPLICATION_JSON + CHARSET_UTF_8;

	/**
	 * The XML content type or acceptance indicator ("application/xml").
	 */
	public static final String APPLICATION_XML = "application/xml";

	/**
	 * The PNG content type or acceptance indicator ("image/png").
	 */
	public static final String IMAGE_PNG = "image/png";

	/**
	 * The JPEG content type or acceptance indicator ("image/jpeg").
	 */
	public static final String IMAGE_JPEG = "image/jpeg";

	/**
	 * The GIF content type or acceptance indicator ("image/gif").
	 */
	public static final String IMAGE_GIF = "image/gif";

	/**
	 * The constant representing wildcard media type ("&#42;/&#42;").
	 */
	public final static String WILDCARD = "*/*";
}
