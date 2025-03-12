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
package se.autocorrect.styleconverter.internal.data;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SymbolData implements ElementData {

	private String symbolId;
	private String lineColorStr;
	private String srcStr;
	private String maxZoom;
	private String minZoom;
	private boolean repeat;
	private String textSize;

	private SymbolData(String symbolId, String lineColorStr, String srcStr, String maxZoom, String minZoom,
			boolean repeat, String textSize) {
		this.symbolId = symbolId;
		this.lineColorStr = lineColorStr;
		this.srcStr = srcStr;
		this.maxZoom = maxZoom;
		this.minZoom = minZoom;
		this.repeat = repeat;
		this.textSize = textSize;
	}

	@Override
	public String getId() {
		return symbolId;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {

		return new Builder(symbolId, lineColorStr, srcStr, maxZoom, minZoom, repeat, textSize);
	}

	public static class Builder extends ElementData.Builder<SymbolData> {

		private String symbolId;
		private String lineColorStr;
		private String srcStr;
		private String maxZoom;
		private String minZoom;
		private boolean repeat;

		private Builder() {
		}

		Builder(String symbolId, String lineColorStr, String srcStr, String maxZoom, String minZoom, boolean repeat,
				String textSize) {

			this.symbolId = symbolId;
			this.lineColorStr = lineColorStr;
			this.srcStr = srcStr;
			this.maxZoom = maxZoom;
			this.minZoom = minZoom;
			this.repeat = repeat;
			this.textSize = textSize;
		}

		public Builder id(String symbolId) {
			this.symbolId = symbolId;
			return this;
		}

		public Builder stroke(String lineColorStr) {
			this.lineColorStr = lineColorStr;
			return this;
		}

		public Builder src(String srcStr) {
			this.srcStr = srcStr;
			return this;
		}

		public Builder maxZoom(String maxZoom) {
			this.maxZoom = maxZoom;
			return this;
		}

		public Builder minZoom(String minZoom) {
			this.minZoom = minZoom;
			return this;
		}

		public Builder repeat(boolean repeat) {
			this.repeat = repeat;
			return this;
		}

		public boolean isRepeat() {
			return repeat;
		}

		@Override
		public SymbolData build() {
			return new SymbolData(symbolId, lineColorStr, srcStr, maxZoom, minZoom, repeat, textSize);
		}
	}

	@Override
	public Element generate(Document document) {

		if (srcStr == null) {
			return null;
		}

		Element element = document.createElement("style-symbol");

		element.setAttribute("id", symbolId);

		if (lineColorStr != null) {
			element.setAttribute("stroke", lineColorStr);
		}

		if (maxZoom != null) {
			element.setAttribute("maxzoom", maxZoom);
		}

		if (minZoom != null) {
			element.setAttribute("minzoom", minZoom);
		}

		if (repeat) {
			element.setAttribute("repeat", "true");
		}

		if (textSize != null) {
			element.setAttribute("size", textSize);
		}

		element.setAttribute("src", srcStr);

		return element;
	}

	@Override
	public void accept(ElementDataVisitor visitor) {
		visitor.visit(this);
	}
}
