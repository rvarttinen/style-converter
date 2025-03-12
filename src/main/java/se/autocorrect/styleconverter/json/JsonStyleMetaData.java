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
package se.autocorrect.styleconverter.json;


import java.util.LinkedHashMap;
import java.util.Map;

public class JsonStyleMetaData {

	/*
	 * TODO: about colons in identifiers in JSON:
	 * https://stackoverflow.com/questions/13462076/how-do-you-parse-json-with-a-
	 * colon-in-the-name-android-java
	 */

	private Map<String, MetaDataObject> metaDataObjects = new LinkedHashMap<>();

	public boolean contains(String key) {
		return metaDataObjects.containsKey(key);
	}

	public void add(String key, MetaDataObject metaDataobject) {
		metaDataObjects.put(key, metaDataobject);
	}

	public MetaDataObject get(String key) {
		return metaDataObjects.get(key);
	}

	public static class MetaDataObject {

		private Class<?> type;
		private Object value;
		private Map<String, MetaDataObject> children = new LinkedHashMap<>();

		public MetaDataObject(Class<?> type, Object value) {
			this.type = type;
			this.value = value;
		}

		public Class<?> getType() {
			return type;
		}

		public Object getValue() {
			return value;
		}

		public void add(String key, MetaDataObject child) {
			children.put(key, child);
		}

		public boolean contains(String key) {
			return children.containsKey(key);
		}

		public MetaDataObject get(String key) {
			return children.get(key);
		}
	}
}
