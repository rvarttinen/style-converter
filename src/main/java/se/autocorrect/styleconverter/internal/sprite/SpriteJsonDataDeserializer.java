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
package se.autocorrect.styleconverter.internal.sprite;


import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class SpriteJsonDataDeserializer implements JsonDeserializer<SpriteJsonDataObject> {

	@Override
	public SpriteJsonDataObject deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		SpriteJsonDataObject jsonDataObj = new SpriteJsonDataObject();

		if (jsonElement.isJsonObject()) {

			Map<String, JsonElement> objMap = jsonElement.getAsJsonObject().asMap();

			objMap.entrySet().forEach(entry -> {

				SpriteJsonData.Builder builder = SpriteJsonData.builder();

				builder.name(entry.getKey());

				JsonObject obj = entry.getValue().getAsJsonObject();

				builder.height(obj.get("height").getAsInt()).width(obj.get("width").getAsInt())
						.x(obj.get("x").getAsInt()).y(obj.get("y").getAsInt())
						.pixelRatio(obj.get("pixelRatio").getAsInt());

				jsonDataObj.addSpriteJsonData(builder.build());
			});
		}

		return jsonDataObj;
	}
}
