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
package se.autocorrect.styleconverter.sprite;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import se.autocorrect.styleconverter.internal.sprite.Sprite;
import se.autocorrect.styleconverter.internal.sprite.SpriteJsonDataDeserializer;
import se.autocorrect.styleconverter.internal.sprite.SpriteJsonDataObject;
import se.autocorrect.styleconverter.internal.sprite.SpriteSheet;

/**
 * Testing the sprite sheet and sprite extraction capability. 
 */
class SpriteSheetTest {
	
	private static final String JSON_SPRITE_DATA_FILENAME = "/json/osm-bright-gl-style/sprite.json";
	
	private SpriteJsonDataObject spriteMetaData;
	private String spriteInputFile;
	private URL spriteURL;

	@BeforeEach
	void setUp() throws FileNotFoundException, IOException {
		
		String fileName = JSON_SPRITE_DATA_FILENAME;

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(SpriteSheetTest.class.getResourceAsStream(fileName)))) {

			String spriteJsonData = bufferedReader.lines().collect(Collectors.joining("\n"));

			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(SpriteJsonDataObject.class, new SpriteJsonDataDeserializer());
			Gson gson = builder.create();

			this.spriteMetaData = gson.fromJson(spriteJsonData, SpriteJsonDataObject.class);

			int idx = fileName.lastIndexOf('/');
			fileName = fileName.substring(0, idx) + "/sprite.png";

			this.spriteURL = SpriteSheetTest.class.getResource(fileName);
			this.spriteInputFile = getFilePath(fileName);
			
//			if(spriteInputFile.startsWith("/")) {
//				this.spriteInputFile = spriteInputFile.substring(1);
//			}
		}
	}

	@Test
	void testLoadStringSpriteJsonDataObject() throws IOException {
		
		SpriteSheet actual = SpriteSheet.load(spriteInputFile, spriteMetaData);
		
		assertAll(
				() -> assertNotNull(actual),
				() -> assertEquals(spriteMetaData.size(), actual.count())
				);
	}

	@Test
	void testLoadURLSpriteJsonDataObject() throws IOException {
		
		SpriteSheet actual = SpriteSheet.load(spriteURL, spriteMetaData);
		
		assertAll(
				() -> assertNotNull(actual),
				() -> assertEquals(spriteMetaData.size(), actual.count())
				);
	}
	
	@ParameterizedTest
	@MethodSource("spriteNameProvider")
	void testGetSprite(String spriteName) throws IOException {
		
		SpriteSheet spriteSheet = SpriteSheet.load(spriteInputFile, spriteMetaData);
		
		Optional<Sprite> actualOp = spriteSheet.getSpriteByName(spriteName);
		
		assertAll(
				() -> assertTrue(actualOp.isPresent()),
				() -> {
					
					Sprite actual = actualOp.get();
					
					assertAll(
							() -> assertEquals(spriteName, actual.getName()),
							() -> assertNotNull(actual.getBufferedImage())
							);
				}
				);
	}
	
	/**
	 * Method providing sprite names used in test method above.
	 * 
	 * @return a {@code Stream} of available sprite names
	 * @throws Exception if soemthing goes wrong
	 */
	static Stream<String> spriteNameProvider() throws Exception {

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(SpriteSheetTest.class.getResourceAsStream(JSON_SPRITE_DATA_FILENAME)))) {

			String spriteJsonData = bufferedReader.lines().collect(Collectors.joining("\n"));

			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(SpriteJsonDataObject.class, new SpriteJsonDataDeserializer());
			Gson gson = builder.create();

			SpriteJsonDataObject spriteData = gson.fromJson(spriteJsonData, SpriteJsonDataObject.class);

			return spriteData.stream().map(sprite -> sprite.getName());
		}
	}
	
	/**
	 * A safe way to retreive the actual file path by using a temporary file for
	 * testing, this works better on differing OS:es than using the resource loding
	 * and retroeivng the file path from there. The temp-file will be automatically
	 * removed.
	 * 
	 * @param fileName the file name
	 * @return thje absolute path to the a temporary file (for testing purposes)
	 * @implNote maybe a bit of a clumsy way to do this, but we avoid the
	 *           intricacies of the classloader reporting differently on different
	 *           OS.es
	 */
	private String getFilePath(String fileName) {
		
		InputStream is = SpriteSheetTest.class.getResourceAsStream(fileName);
		
		try {
			
			File tempFile = File.createTempFile("temp-img-", ".png");
			tempFile.deleteOnExit();
			
			try (OutputStream os = new FileOutputStream(tempFile)) {
				
				byte[] buffer = new byte[8 * 1024];
				int bytesRead;
				
				while ((bytesRead = is.read(buffer)) != -1) {
				    os.write(buffer, 0, bytesRead);
				}
			}
			
			return tempFile.getAbsolutePath();
			
		} catch (IOException e) {
			fail("Could not create temporary file: " + e.getMessage());
		}
		
		return null;	
	}
}
