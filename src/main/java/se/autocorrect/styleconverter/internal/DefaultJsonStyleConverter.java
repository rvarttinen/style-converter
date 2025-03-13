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


import java.net.URL;

import org.oscim.theme.ThemeFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import se.autocorrect.styleconverter.ConversionException;
import se.autocorrect.styleconverter.StyleConverter;
import se.autocorrect.styleconverter.internal.sprite.SpriteExtractor;
import se.autocorrect.styleconverter.internal.xml.XmlGenerator;
import se.autocorrect.styleconverter.json.JsonStyle;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.FilterData;
import se.autocorrect.styleconverter.json.JsonStyleMetaData;

public class DefaultJsonStyleConverter implements StyleConverter {
	
	private static Logger logger = LoggerFactory.getLogger(DefaultJsonStyleConverter.class);

	private String name;
	private SpriteExtractor extractor;

	public DefaultJsonStyleConverter() {
		this.extractor = new SpriteExtractor();
	}

	@Override
	public ThemeFile convert(String input) throws Exception {

		Gson gson = initGson();
		XmlGenerator gen = null;

		try {

			JsonStyle jsonStyle = gson.fromJson(input, JsonStyle.class);

			probeUrlForSprites(jsonStyle.getSprite());

			gen = new XmlGenerator(new DefaultJsonConversionContext(jsonStyle, name));

			return gen.generateXml();

		} catch (Throwable t) {

			logger.error("Error when converting style to theme ", t);

			throw new ConversionException(t);

		} finally {

			if (gen != null) {
				gen.dispose();
			}
		}
	}

	private Gson initGson() {

		GsonBuilder builder = new GsonBuilder();
		builder.serializeNulls().registerTypeAdapter(JsonStyleMetaData.class, new MetaDataDeserializer())
				.registerTypeAdapter(FilterData.class, new FilterDataDeserializer())

		// TODO: register a Paint deserializer? ...
		// https://stackoverflow.com/questions/19478087/deserialization-of-sometimes-string-and-sometimes-object-with-gson
		;

		return builder.create();
	}

	private void probeUrlForSprites(URL spriteBaseUrl) throws ConversionException {

		if (spriteBaseUrl == null) {
			
			logger.info("No sprites to retrieve ... ");
			return;
		}

		try {

			String protocol = spriteBaseUrl.getProtocol();

			switch (protocol) {

			case "http":
			case "https":

				extractor.extract(spriteBaseUrl);
				break;
			default:
				logger.info("Unrecognized protocol when attempting to extract sprites: {}", protocol);
			}

		} catch (Throwable t) {
			throw new ConversionException(t);
		}
	}
}
