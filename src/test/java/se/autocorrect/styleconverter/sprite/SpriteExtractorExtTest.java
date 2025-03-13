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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.SocketConnection;

import se.autocorrect.styleconverter.internal.NetConstants;
import se.autocorrect.styleconverter.internal.sprite.SpriteExtractor;
import se.autocorrect.styleconverter.internal.sprite.cache.SpriteCache;

/**
 * Test case for the {@code SpriteExtractor} taking space in path into consideration. 
 */
class SpriteExtractorExtTest {
	
	/**
	 * The path with spaces to be used. 
	 */
	private static final String PATH_WITH_SPACES = "max sprite/total path/";
	
	/**
	 * We emulate how the URL is provided by the extension point, thus the 'style.json' will be appended
	 */
	private static final String JSON_SPRITE_DATA_BASEURL = "http://localhost:9999/" + PATH_WITH_SPACES;
	
	/**
	 * The local sprite meta data file. 
	 */
	private static final String JSON_SPRITE_DATA_FILENAME = "/json/osm-bright-gl-style/sprite.json"; //"/" + PATH_WITH_SPACES + "osm-bright-gl-style/sprite.json";
	
	/**
	 * The local sprite image file. 
	 */
	private static final String IMG_SPRITE_DATA_FILENAME = "/json/osm-bright-gl-style/sprite.png"; //"/" + PATH_WITH_SPACES + "osm-bright-gl-style/sprite.png";
	
	private SpriteExtractor eut;
	
	private static Server server;
	private static SocketConnection socketConnection;
	private static TestContainer testContainer;

	
	@BeforeAll
	static void beforeAll() throws IOException {
		
		testContainer = new TestContainer();
        server = new ContainerServer(testContainer);
        
        socketConnection = new SocketConnection(server);
        
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 9999);
        socketConnection.connect(inetSocketAddress);
	}
	
	@AfterAll
	static void afterAll() throws IOException {
		
		SpriteCache.getInstance().dispose();
		socketConnection.close();
		server.stop();
	}
	
	@BeforeEach
	void setUp() {
		this.eut = new SpriteExtractor();
	}


	@Test
	void testExtractFromUrlWithSpaces() throws Exception {
		
		eut.extract(new URL(JSON_SPRITE_DATA_BASEURL + "sprite"));

		SpriteCache cache = SpriteCache.getInstance();
		
		Optional<String> spriteFileOp = cache.getSpriteFileBySpriteName("airfield_11");
		
		assertTrue(spriteFileOp.isPresent());
	}	
	
	/**
	 * A test container handling the reponses necessary for this test, i.e. sending
	 * back a stream of data to be parsed and interpreted by the extractor under
	 * test.
	 */
	static class TestContainer implements Container {

		private int code = 200;

		@Override
		public void handle(Request request, Response response) {

			String method = request.getMethod();

			assertEquals("GET", method);
			
			try {

				Path path = request.getPath();

				InputStream is = null;
				OutputStream os = response.getOutputStream();
				
				response.setCode(code);
				
				if (path.getName().equals("sprite.json")) {

					is = SpriteExtractorBasicTest.class.getResourceAsStream(JSON_SPRITE_DATA_FILENAME);

					response.addValue(NetConstants.CONTENT_TYPE, NetConstants.APPLICATION_JSON);
					
				}else if (path.getName().equals("sprite.png")) {

					is = SpriteExtractorBasicTest.class.getResourceAsStream(IMG_SPRITE_DATA_FILENAME);
					
					response.addValue(NetConstants.CONTENT_TYPE, NetConstants.IMAGE_PNG);
				}
				
				assertNotNull(is);
				assertNotNull(os);

				writeToResponseStream(os, is);
				
				// The response wont be "sent" until the output stream is closed
				os.close();
				
				// Reset response code if it was altered
				resetResponseCode();

			} catch (IOException e) {
               fail(e.getMessage());
           }
		}

		private void resetResponseCode() {
			
			if(code != 200) {
				this.code = 200;
			}
		}
		
		void setResponseCode(int code) {
			this.code  = code;
		}

		private void writeToResponseStream(OutputStream os, InputStream is) throws IOException {
			
			byte[] buf = new byte[8192];
			int length;

			while ((length = is.read(buf)) != -1) {
				os.write(buf, 0, length);
			}
		}
    }
}
