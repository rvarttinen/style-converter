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

public class AreaData implements ElementData {

	private String areaId;
	private String fadeStr;
	private String fillColorStr;
	private String fillPattern;
	private String opacityStr;

	private AreaData(String areaId, String fadeStr, String fillColorStr, String fillPattern, String opacityStr) {
		this.areaId = areaId;
		this.fadeStr = fadeStr;
		this.fillColorStr = fillColorStr;
		this.fillPattern = fillPattern;
		this.opacityStr = opacityStr;
	}

	@Override
	public String getId() {
		return getAreaId();
	}

	public String getAreaId() {
		return areaId;
	}

	public String getFillOpacity() {
		return opacityStr;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ElementData.Builder<AreaData> {

		private String areaId;
		private String fadeStr;
		private String fillColorStr;
		private String fillPattern;
		private String opacityStr;

		private Builder() {
		}

		public Builder id(String areaId) {
			this.areaId = areaId;
			return this;
		}

		public Builder fade(String fadeStr) {
			this.fadeStr = fadeStr;
			return this;
		}

		public Builder fill(String fillColorStr) {
			this.fillColorStr = fillColorStr;
			return this;
		}

		public Builder fillPattern(String fillPattern) {
			this.fillPattern = fillPattern;
			return this;
		}

		public Builder fillOpacity(String opacityStr) {
			this.opacityStr = opacityStr;
			return this;
		}

		@Override
		public AreaData build() {
			return new AreaData(areaId, fadeStr, fillColorStr, fillPattern, opacityStr);
		}
	}

	@Override
	public Element generate(Document document) {

		Element element;

		if (fillPattern != null) {

			element = document.createElement("style-symbol");
			element.setAttribute("src", fillPattern);

		} else {

			element = document.createElement("style-area");
		}

		element.setAttribute("id", areaId);

		// Add fill color if present and we are handling other areas than buildings
		if (fillColorStr != null && !areaId.equals("building")) {
			element.setAttribute("fill", fillColorStr);
		}

		if (fadeStr != null) {
			element.setAttribute("fade", fadeStr);
		}

		return element;
	}

	@Override
	public void accept(ElementDataVisitor visitor) {
		visitor.visit(this);
	}
}
