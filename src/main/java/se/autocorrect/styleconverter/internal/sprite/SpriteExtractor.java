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


import static se.autocorrect.styleconverter.NetConstants.ACCEPT;
import static se.autocorrect.styleconverter.NetConstants.APPLICATION_JSON;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import se.autocorrect.styleconverter.internal.sprite.cache.SpriteCache;
import se.autocorrect.styleconverter.util.UriUtils;

public class SpriteExtractor {

//	private final ILog logger;

	public SpriteExtractor() {
//		this.logger = Platform.getLog(getClass());
	}

	public void extract(URL url) {

		String proto = url.getProtocol();
		int port = url.getPort();
		String host = url.getHost();
		String hostFile = url.getFile();

//		logger.info("Retrieving sprites from: " + url);

		try {

			hostFile = UriUtils.encodePathSegments(hostFile);
			hostFile = hostFile + ".json";

			URL modUrl = new URL(proto, host, port, hostFile);

			HttpRequest request = HttpRequest.newBuilder().version(Version.HTTP_1_1)
					.uri(URI.create(modUrl.toExternalForm())).GET().header(ACCEPT, APPLICATION_JSON).build();

			HttpClient httpClient = HttpClient.newHttpClient();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {

				String spriteJsonData = response.body();
				SpriteJsonDataObject spriteMetaData = extractSpriteMetaData(spriteJsonData);

				hostFile = url.getFile();

				hostFile = UriUtils.encodePathSegments(hostFile);
				hostFile = hostFile + ".png";

				modUrl = new URL(proto, host, port, hostFile);

				SpriteSheet spriteSheet = SpriteSheet.load(modUrl, spriteMetaData);

				populateSpriteCache(spriteSheet);

			} else if (response.statusCode() == 404) {

//				logger.info("No sprite data at: " + url.toExternalForm());

			} else {
//				logger.error(String.format("Could not extract sprite data from \"%s\", server responded with code: %d",
//						modUrl.toExternalForm(), response.statusCode()));
			}

		} catch (Throwable t) {
//			logger.error("Error when extracting sprites", t);
		}
	}

	private SpriteJsonDataObject extractSpriteMetaData(String spriteJsonData) {

		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(SpriteJsonDataObject.class, new SpriteJsonDataDeserializer());
		Gson gson = builder.create();

		return gson.fromJson(spriteJsonData, SpriteJsonDataObject.class);
	}

	private void populateSpriteCache(SpriteSheet spriteSheet) {

		SpriteCache cache = SpriteCache.getInstance();

		spriteSheet.stream().forEach(sprite -> {

			try {
				cache.storeSprite(sprite);
			} catch (IOException e) {
				throw new UncheckedIOExcpetion(e);
			}
		});
	}

	private static class UncheckedIOExcpetion extends RuntimeException {

		@java.io.Serial
		private static final long serialVersionUID = 7858668344396253109L;

		private UncheckedIOExcpetion() {
			super();
		}

		private UncheckedIOExcpetion(Throwable cause) {
			super(cause);
		}
	}
}
