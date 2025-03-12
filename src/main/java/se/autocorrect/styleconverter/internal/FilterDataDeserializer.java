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


import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import se.autocorrect.styleconverter.json.JsonStyleLayerData.FilterData;
import se.autocorrect.styleconverter.util.StringUtils;

public class FilterDataDeserializer implements JsonDeserializer<FilterData> {

	@Override
	public FilterData deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		FilterData fd = new FilterData();

		if (jsonElement.isJsonArray()) {

			JsonArray array = jsonElement.getAsJsonArray();

			array.forEach(element -> {

				if (element.isJsonPrimitive()) {

					String str = element.getAsString();

					if (str.equals("all") || str.equals("in") || str.equals("!in")
							|| StringUtils.containsAny(str, "!=<>")) {
						fd.setFilter(str);
					} else {
						fd.addNode(str);
					}

				} else if (element.isJsonArray()) {

					processArrayRecursive(element.getAsJsonArray(), fd);
				}
			});
		}

		return fd;
	}

	private void processArrayRecursive(JsonArray jsonArray, FilterData parentData) {

		FilterData fd = new FilterData();
		parentData.addNode(fd);

		jsonArray.forEach(element -> {

			if (element.isJsonPrimitive()) {

				String str = element.getAsString();

				if (str.equals("all") || str.equals("in") || str.equals("!in")
						|| StringUtils.containsAny(str, "!=<>")) {
					fd.setFilter(str);
				} else {
					fd.addNode(str);
				}

			} else if (element.isJsonArray()) {
				processArrayRecursive(element.getAsJsonArray(), fd);
			}
		});
	}
}
