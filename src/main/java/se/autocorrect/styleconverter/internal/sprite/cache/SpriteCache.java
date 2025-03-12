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
package se.autocorrect.styleconverter.internal.sprite.cache;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import se.autocorrect.styleconverter.internal.sprite.Sprite;

public class SpriteCache {

	private static final String DEFAULT_DIRECTORY = ".sprites";
	private static Map<String, String> spriteFilesByNames = new HashMap<>();

	private File directory;

	private static SpriteCache instance = new SpriteCache();

	private SpriteCache() {

		String userHomeDir = System.getProperty("user.home");

		this.directory = new File(userHomeDir, DEFAULT_DIRECTORY);

		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public static SpriteCache getInstance() {
		return instance;
	}

	public String storeSprite(Sprite sprite) throws IOException {

		String name = sprite.getName();
		BufferedImage bufferedImage = sprite.getBufferedImage();

		File file = new File(directory, name + ".png");

		boolean write = ImageIO.write(bufferedImage, "png", file);

		String path = file.getCanonicalPath();

		path = makeRelativeOnWindows(path);

		spriteFilesByNames.put(name, path);

		return write ? path : null;
	}

	public Optional<String> getSpriteFileBySpriteName(String name) {
		return Optional.ofNullable(spriteFilesByNames.get(name));
	}

	public void dispose() {

		for (File file : directory.listFiles()) {

			if (!file.isDirectory()) {
				file.delete();
			}
		}

		spriteFilesByNames.clear();
	}

	private String makeRelativeOnWindows(String path) {

		// TODO: check out Apache FilenameUtils getPrefix(..) in order to use something
		// generic ...
		// TODO: "only" C: for now ...

		if (path.startsWith("C:\\")) {
			path = path.substring("C:\\".length());
		}

		return path;
	}
}
