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

public class TextData implements ElementData {

	private String textId;
	private String fonts;
	private String textSize;
	private String textColor;
	private String fillColor;
	private String textField;
	private String maxzoom;
	private String minzoom;
	private String priority;
	private String dy;

	private TextData(String textId, String fonts, String textSize, String textColor, String fillColor, String textField,
			String maxzoom, String minzoom, String priority, String dy) {
		this.textId = textId;
		this.fonts = fonts;
		this.textSize = textSize;
		this.textColor = textColor;
		this.fillColor = fillColor;
		this.textField = textField;
		this.maxzoom = maxzoom;
		this.minzoom = minzoom;
		this.priority = priority;
		this.dy = dy;
	}

	@Override
	public String getId() {
		return textId;
	}

	public String getTextId() {
		return textId;
	}

	public String getFonts() {
		return fonts;
	}

	public String getTextSize() {
		return textSize;
	}

	public String getTextColor() {
		return textColor;
	}

	public String getFillColor() {
		return fillColor;
	}

	public String getTextField() {
		return textField;
	}

	public String getMaxzoom() {
		return maxzoom;
	}

	public String getMinzoom() {
		return minzoom;
	}

	public String getPriority() {
		return priority;
	}

	public String getDy() {
		return dy;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ElementData.Builder<TextData> {

		private String textId;
		private String fonts;
		private String textSize;
		private String textColor;
		private String fillColor;
		private String textField;
		private String maxzoom;
		private String minzoom;
		private String priority;
		private String dy;

		private Builder() {
		}

		public Builder id(String textId) {
			this.textId = textId;
			return this;
		}

		public Builder fonts(String fonts) {
			this.fonts = fonts;
			return this;
		}

		public Builder textSize(String textSize) {
			this.textSize = textSize;
			return this;
		}

		public Builder textColor(String textColor) {
			this.textColor = textColor;
			return this;
		}

		public Builder fill(String fillColor) {
			this.fillColor = fillColor;
			return this;
		}

		public Builder name(String textField) {
			this.textField = textField;
			return this;
		}

		public Builder maxZoom(String maxzoom) {
			this.maxzoom = maxzoom;
			return this;
		}

		public Builder minZoom(String minzoom) {
			this.minzoom = minzoom;
			return this;
		}

		public Builder priority(String priority) {
			this.priority = priority;
			return this;
		}

		public Builder dy(String dy) {
			this.dy = dy;
			return this;
		}

		@Override
		public TextData build() {
			return new TextData(textId, fonts, textSize, textColor, fillColor, textField, maxzoom, minzoom, priority,
					dy);
		}
	}

	@Override
	public Element generate(Document document) {

		Element element = document.createElement("style-text");

		element.setAttribute("id", textId);

		if (textField != null) {
			element.setAttribute("k", textField);
		} else {
			element.setAttribute("k", "name");
		}

		if (fillColor != null) {
			element.setAttribute("fill", fillColor);
		}

		if (textColor != null) {
			element.setAttribute("fill", textColor);
		}

		if (fonts != null) {
			element.setAttribute("text-font", fonts);
		}

		if (textSize != null) {
			element.setAttribute("size", textSize);
		}

		if (minzoom != null) {
			element.setAttribute("minzoom", minzoom);
		}

		if (maxzoom != null) {
			element.setAttribute("maxzoom", maxzoom);
		}

		if (priority != null) {
			element.setAttribute("priority", priority);
		}

		return element;
	}

	@Override
	public void accept(ElementDataVisitor visitor) {
		visitor.visit(this);
	}
}
