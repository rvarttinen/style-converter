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
package se.autocorrect.styleconverter.json;


import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class JsonStyleLayerData {

	private String id;
	private String type;
	private String source;
	private PaintData paint;

	private int ordinal;

	private LayoutData layout;

	@SerializedName("source-layer")
	private String sourceLayer;

	@SerializedName("filter")
	private FilterData filterData;

	@SerializedName("text-size")
	private int textSize;

	@SerializedName("minzoom")
	private double minZoom;

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public PaintData getPaint() {
		return paint;
	}

	public LayoutData getLayout() {
		return layout;
	}

	public String getSourceLayer() {
		return sourceLayer;
	}

	public FilterData getFilterData() {
		return filterData;
	}

	public int getTextSize() {
		return textSize;
	}

	public Double getMinZoom() {
		return minZoom;
	}

	public static class PaintData {

		@SerializedName("background-color")
		private String backgroundColor;

		@SerializedName("line-color")
		private Object lineColor;

		@SerializedName("line-width")
		private Object lineWidth;

		@SerializedName("line-opacity")
		private Object lineOpacity;

		@SerializedName("fill-color")
		private Object fillColor;

		@SerializedName("fill-opacity")
		private Object fillOpacity;

		@SerializedName("fill-outline-color")
		private Object fillOutLineColor;

		@SerializedName("fill-pattern")
		private String fillPattern;

		@SerializedName("line-dasharray")
		private float[] dashArray;

		@SerializedName("text-color")
		private Object textColor;

		@SerializedName("text-halo-blur")
		private float textHaloBlur;

		@SerializedName("text-halo-color")
		private String textHaloColor;

		@SerializedName("text-halo-width")
		private float textHaloWidth;

		@SerializedName("text-font")
		private String[] textFont;

		public String getBackgroundColor() {
			return backgroundColor;
		}

		public Object getLineColor() {
			return lineColor;
		}

		public Object getFillColor() {
			return fillColor;
		}

		public float[] getDashArray() {
			return dashArray;
		}

		public Object getTextColor() {
			return textColor;
		}

		public float getTextHaloBlur() {
			return textHaloBlur;
		}

		public String getTextHaloColor() {
			return textHaloColor;
		}

		public float getTextHaloWidth() {
			return textHaloWidth;
		}

		public Object getFillOpacity() {
			return fillOpacity;
		}

		public Object getLineWidth() {
			return lineWidth;
		}

		public Object getLineOpacity() {
			return lineOpacity;
		}

		public String[] getTextFont() {
			return textFont;
		}

		public Object getFillOutLineColor() {
			return fillOutLineColor;
		}

		public String getFillPattern() {
			return fillPattern;
		}
	}

	public static class LayoutData {

		private String visibility; // "visibility": "visible" or "none"

		@SerializedName("line-cap")
		private String lineCap;

		@SerializedName("line-join")
		private String lineJoin;

		// TODO: check if appropriate as sub-object as not always present ...
		@SerializedName("minzoom")
		private int minZoom;

		@SerializedName("maxzoom")
		private int maxZoom;

		@SerializedName("text-offset") // dy?
		private double[] textOffset;

		@SerializedName("text-size")
		private Object textSize;

		@SerializedName("text-font")
		private String[] textFont;

		@SerializedName("text-field")
		private String textField;

		@SerializedName("text-anchor")
		private Object textAnchor;

		@SerializedName("symbol-placement")
		private Object symbolPlacement;

		@SerializedName("symbol-spacing")
		private Double symbolSpacing;

		@SerializedName("icon-image")
		private Object iconImage;

		public String getVisibility() {
			return visibility;
		}

		public String getLineCap() {
			return lineCap;
		}

		public String getLineJoin() {
			return lineJoin;
		}

		public int getMinZoom() {
			return minZoom;
		}

		public int getMaxZoom() {
			return maxZoom;
		}

		public double[] getTextOffset() {
			return textOffset;
		}

		public Object getTextSize() {
			return textSize;
		}

		public String[] getTextFont() {
			return textFont;
		}

		public String getTextField() {
			return textField;
		}

		public Object getTextAnchor() {
			return textAnchor;
		}

		public Object getSymbolPlacement() {
			return symbolPlacement;
		}

		public Double getSymbolSpacing() {
			return symbolSpacing;
		}

		public Object getIconImage() {
			return iconImage;
		}
	}

	public static class FillOpacity {

		private int base;
		private float[][] stops;
	}

	public static class FilterData {

		private String filter;
		private List<Object> nodes = new ArrayList<>();

		public FilterData() {
		}

		public FilterData(String filter) {
			this.filter = filter;
		}

		public void setFilter(String filter) {
			this.filter = filter;
		}

		public String getFilter() {
			return filter;
		}

		public void addNode(Object node) {
			nodes.add(node);
		}

		public List<Object> getNodes() {
			return nodes;
		}
	}
}
