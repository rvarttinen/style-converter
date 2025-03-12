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

public class LineData implements ElementData {

	private String lineId;
	private String lineColorStr;
	private String lineWidthStr;
	private String lineOpacityStr;
	private String lineCap;
	private String lineJoin;
	private String fadeStr;
	private String cap;
	private float[] stipple;

	private LineData(String lineId, String lineColorStr, String lineWidthStr, String lineOpacityStr, String lineCap,
			String lineJoin, String fadeStr, String cap, float[] stipple) {
		this.lineId = lineId;
		this.lineColorStr = lineColorStr;
		this.lineWidthStr = lineWidthStr;
		this.lineOpacityStr = lineOpacityStr;
		this.lineCap = lineCap;
		this.lineJoin = lineJoin;
		this.fadeStr = fadeStr;
		this.cap = cap;
		this.stipple = stipple;
	}

	// Only public method for now, we need to reference a line also when dealing
	// with buildings
	public String getlineId() {
		return lineId;
	}

	@Override
	public String getId() {
		return getlineId();
	}

	public String getStrokeColor() {
		return lineColorStr;
	}

	public void setLineColorStr(String lineColorStr) {
		this.lineColorStr = lineColorStr;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ElementData.Builder<LineData> {

		private String lineId;
		private String lineColorStr;
		private String lineWidthStr;
		private String lineOpacityStr;
		private String lineCap;
		private String lineJoin;
		private String fadeStr;
		private String cap;
		private float[] stipple;

		private Builder() {
		}

		public Builder id(String lineId) {
			this.lineId = lineId;
			return this;
		}

		public Builder strokeColor(String lineColorStr) {
			this.lineColorStr = lineColorStr;
			return this;
		}

		public Builder width(String lineWidthStr) {
			this.lineWidthStr = lineWidthStr;
			return this;
		}

		public Builder opacity(String lineOpacityStr) {
			this.lineOpacityStr = lineOpacityStr;
			return this;
		}

		public Builder lineCap(String lineCap) {
			this.lineCap = lineCap;
			return this;
		}

		public Builder lineJoin(String lineJoin) {
			this.lineJoin = lineJoin;
			return this;
		}

		public Builder fade(String fadeStr) {
			this.fadeStr = fadeStr;
			return this;
		}

		public Builder cap(String cap) {
			this.cap = cap;
			return this;
		}

		public Builder stipple(float[] stipple) {
			// TODO: avoid stipple for now, rendering does not look good.
//			this.stipple = stipple;
			return this;
		}

		@Override
		public LineData build() {

			return new LineData(lineId, lineColorStr, lineWidthStr, lineOpacityStr, lineCap, lineJoin, fadeStr, cap,
					stipple);
		}
	}

	@Override
	public Element generate(Document document) {

		Element element = document.createElement("style-line");

		element.setAttribute("id", lineId);

		if (lineColorStr != null && stipple == null) {
			element.setAttribute("stroke", lineColorStr);
		}

		if (lineWidthStr != null) {

			element.setAttribute("width", lineWidthStr);

		} else if (lineId.equals("building") || lineId.equals("building-top")) {

			// TODO: for now: only building outlines - make generic, update relevent other
			// code

			// Use fixed width if no width specified
			element.setAttribute("width", "1");
		}

		if (lineCap != null) {
			element.setAttribute("cap", lineCap);
		}

		if (fadeStr != null) {
			element.setAttribute("fade", fadeStr);
		}

		if (cap != null) {
			element.setAttribute("cap", cap);
		}

		if (stipple != null && stipple.length > 0) {

			element.setAttribute("stipple", String.valueOf(Float.valueOf(stipple[0]).intValue()));
			element.setAttribute("stipple-width", String.valueOf(stipple[1]));
			element.setAttribute("stipple-stroke", lineColorStr);
		}

		// TODO:: No such element recognized by parser
//		if(lineJoin != null) {
//			element.setAttribute("join", lineJoin);
//		}

		return element;
	}

	@Override
	public void accept(ElementDataVisitor visitor) {
		visitor.visit(this);
	}
}
