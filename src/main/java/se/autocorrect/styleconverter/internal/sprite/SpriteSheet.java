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


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

public class SpriteSheet {

	private final List<Sprite> sprites;

	SpriteSheet(List<Sprite> sprites) {
		this.sprites = new ArrayList<>(sprites);
	}

	public int count() {
		return sprites.size();
	}

	public Stream<Sprite> stream() {
		return sprites.stream();
	}

	public Optional<Sprite> getSpriteByName(String name) {
		return stream().filter(sprite -> sprite.getName().equals(name)).findFirst();
	}

	public static SpriteSheet load(String file, SpriteJsonDataObject spriteMetaData) throws IOException {

		BufferedImage bufferedImage = ImageIO.read(new File(file));
		return loadInternal(bufferedImage, spriteMetaData);
	}

	public static SpriteSheet load(URL url, SpriteJsonDataObject spriteMetaData) throws IOException {

		BufferedImage bufferedImage = ImageIO.read(url);
		return loadInternal(bufferedImage, spriteMetaData);
	}

	private static SpriteSheet loadInternal(BufferedImage bufferedImage, SpriteJsonDataObject spriteMetaData) {

		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		int[] pixels = new int[width * height];

		bufferedImage.getRGB(0, 0, width, height, pixels, 0, width);

		int count = spriteMetaData.size();

		List<Sprite> sprites = new ArrayList<>(count);

		spriteMetaData.stream().forEach(spriteJsonData -> {

			String name = spriteJsonData.getName();

			int x = spriteJsonData.getX();
			int y = spriteJsonData.getY();

			int spriteHeight = spriteJsonData.getHeight();
			int spriteWidth = spriteJsonData.getWidth();

			BufferedImage subImage = bufferedImage.getSubimage(x, y, spriteWidth, spriteHeight);

			sprites.add(new Sprite(name, subImage));

		});

		return new SpriteSheet(sprites);
	}
}
