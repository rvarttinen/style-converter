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
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import se.autocorrect.styleconverter.json.JsonStyleMetaData;
import se.autocorrect.styleconverter.json.JsonStyleMetaData.MetaDataObject;

public class MetaDataDeserializer implements JsonDeserializer<JsonStyleMetaData> {

	@Override
	public JsonStyleMetaData deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		JsonObject jsonObject = jsonElement.getAsJsonObject();

		JsonStyleMetaData metaData = new JsonStyleMetaData();

		jsonObject.entrySet().forEach(member -> retrieveMetaData(metaData, member));

		return metaData;
	}

	private void retrieveMetaData(JsonStyleMetaData metaData, Entry<String, JsonElement> member) {

		String key = member.getKey();
		JsonElement value = member.getValue();

		// TODO: figure out first level, then all is substructure, metadata doesn't go
		// there

		String[] keys = key.split(":");

		String key_ = null;
		Class<?> type = Object.class;
		MetaDataObject metaDataObject = null;
		MetaDataObject metaDataObj = null;

		if (keys.length > 1) {

			key_ = keys[0];
			key = Arrays.stream(keys).skip(1).collect(Collectors.joining(":"));

			Object objVal = null;

			// TODO: determine "all" types - own method
			if (value.isJsonPrimitive()) {

				JsonPrimitive jsonPrimitive = value.getAsJsonPrimitive();
				type = findPrimitiveType(jsonPrimitive);

			} else if (value.isJsonObject()) {

				JsonObject jsonObj = value.getAsJsonObject();

			} else if (value.isJsonArray()) {

				JsonArray arr = value.getAsJsonArray();
			}

			metaDataObj = new MetaDataObject(type, objVal);

			metaDataObject = retrieveMetaDataRecursive(metaDataObj, key, value);
			metaData.add(key_, metaDataObject);

		} else {

			// TODO: determine "all" types - own method
			if (value.isJsonPrimitive()) {

				JsonPrimitive jsonPrimitive = value.getAsJsonPrimitive();
				type = findPrimitiveType(jsonPrimitive);

			} else if (value.isJsonObject()) {

				JsonObject jsonObj = value.getAsJsonObject();

			} else if (value.isJsonArray()) {

				JsonArray arr = value.getAsJsonArray();
			}

			metaDataObject = new MetaDataObject(type, metaDataObj);
		}

		metaData.add(key, metaDataObject);
	}

	private MetaDataObject retrieveMetaDataRecursive(MetaDataObject metaDataObj, String key, JsonElement value) {

		Class<?> type = Object.class;

		// TODO: remove!
//		System.out.println(key + ": " + value);

		String[] keys = key.split(":");

		String key_ = null;
		MetaDataObject metaDataObject = null;

		if (keys.length > 1) {

			// TODO: figure out type ...

			key_ = keys[0];
			key = Arrays.stream(keys).skip(1).collect(Collectors.joining(":"));

			// TODO: determine "all" types - own method
			if (value.isJsonPrimitive()) {

				JsonPrimitive jsonPrimitive = value.getAsJsonPrimitive();
				type = findPrimitiveType(jsonPrimitive);

			} else if (value.isJsonObject()) {

				JsonObject jsonObj = value.getAsJsonObject();

			} else if (value.isJsonArray()) {

				JsonArray arr = value.getAsJsonArray();
			}

			metaDataObject = new MetaDataObject(type, metaDataObj);

			MetaDataObject metaDataObject_ = retrieveMetaDataRecursive(metaDataObject, key, value);

			if (metaDataObj.contains(key_)) {
				metaDataObject = metaDataObj.get(key_);
			} else {
				metaDataObject = new MetaDataObject(type, metaDataObject_);
			}

			metaDataObj.add(key_, metaDataObject);

		} else {

			key_ = key;

			if (!metaDataObj.contains(key_)) {

				if (value.isJsonPrimitive()) {

					JsonPrimitive jsonPrimitive = value.getAsJsonPrimitive();
					type = findPrimitiveType(jsonPrimitive);

				} else if (value.isJsonObject()) {

					JsonObject jsonObj = value.getAsJsonObject();

				} else if (value.isJsonArray()) {

					JsonArray arr = value.getAsJsonArray();
				}

				metaDataObject = new MetaDataObject(type, value);
				metaDataObj.add(key_, metaDataObject);

			} else {

				metaDataObject = metaDataObj.get(key_);

				// TODO: Add substructure if value - otherwise recurse one more level
				// retrain ref here to that next level ..
				System.err.println("Add to structure - next level: " + key_);
			}
		}

		return metaDataObject;
	}

	private Class<?> findPrimitiveType(JsonPrimitive jsonPrimitive) {

		Class<?> result = null;

		if (jsonPrimitive.isBoolean()) {

			result = Boolean.class;

		} else if (jsonPrimitive.isString()) {

			result = String.class;

		} else if (jsonPrimitive.isNumber()) {

			System.out.println("number");
		}

		return result;
	}
}
