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


public class SpriteJsonData {

	private String name;

	private int x;
	private int y;

	private int height;
	private int width;

	private int pixelRatio;

	private SpriteJsonData(String name, int x, int y, int height, int width, int pixelRatio) {

		this.name = name;
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.pixelRatio = pixelRatio;
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int getPixelRatio() {
		return pixelRatio;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String name;
		private int x;
		private int y;
		private int height;
		private int width;
		private int pixelRatio;

		private Builder() {
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder x(int x) {
			this.x = x;
			return this;
		}

		public Builder y(int y) {
			this.y = y;
			return this;
		}

		public Builder height(int height) {
			this.height = height;
			return this;
		}

		public Builder width(int width) {
			this.width = width;
			return this;
		}

		public Builder pixelRatio(int pixelRatio) {
			this.pixelRatio = pixelRatio;
			return this;
		}

		public SpriteJsonData build() {
			return new SpriteJsonData(name, x, y, height, width, pixelRatio);
		}
	}
}
