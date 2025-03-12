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
package se.autocorrect.styleconverter.internal.data.rule;


import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.internal.data.ElementDataVisitor;
import se.autocorrect.styleconverter.internal.data.TagWithValues;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.util.ColorFormatter;

public class FillRuleData extends RuleData {

	private Map<Double, String> fillColors;
	private String fillColorStr;
	private String symbol;
	private boolean refDirectly;

	private FillRuleData(String id, JsonStyleLayerData layer, String joinedClasses, String zoomMin, String sourcelayer,
			List<String> classNodes, Map<Double, String> fillColors, String fillColorStr, String symbol,
			boolean refDirectly, TagWithValues tagWithValues) {

		super(id, layer, joinedClasses, zoomMin, sourcelayer, classNodes, tagWithValues, false);

		this.fillColors = fillColors;
		this.fillColorStr = fillColorStr;
		this.symbol = symbol;
		this.refDirectly = refDirectly;
	}

	public String getFillColorStr() {
		return fillColorStr;
	}

	public boolean isRefDirectly() {
		return refDirectly;
	}

	public Map<Double, String> getFillColors() {
		return fillColors;
	}

	public String getSymbol() {
		return symbol;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public void accept(ElementDataVisitor visitor) {
		visitor.visit(this);
	}

	public static class Builder extends RuleData.Builder {

		private Map<Double, String> fillColors;
		private String fillOutLineColor;
		private String fillColorStr;
		private String symbol;
		private boolean refDirectly;

		private Builder() {
			super();
		}

		public Builder fillColors(Map<Double, String> fillColors) {
			this.fillColors = fillColors;
			return this;
		}

		public Builder fillOutLineColor(String fillOutLineColor) {
			this.fillOutLineColor = fillOutLineColor;
			return this;
		}

		public Builder fill(String fillColorStr) {
			this.fillColorStr = fillColorStr;
			return this;
		}

		public Builder symbol(String symbol) {
			this.symbol = symbol;
			return this;
		}

		public Builder refDirectly(boolean refDirectly) {
			this.refDirectly = refDirectly;
			return this;
		}

		@Override
		public FillRuleData build() {

			return new FillRuleData(id, layer, joinedClasses, zoomMin, sourceLayer, classNodes, fillColors,
					fillColorStr, symbol, refDirectly, tagWithValues);
		}
	}

	@Override
	public Element generate(Document document) {

		Element subClassElement = null;

		if (fillColors != null && !fillColors.isEmpty()) {

			subClassElement = null;

			if (joinedClasses != null) {
				subClassElement = document.createElement("m");
				subClassElement.setAttribute("v", joinedClasses);
			}

			final Element finalSubClassElement = subClassElement;

			fillColors.forEach((zoom, color) -> {

				color = ColorFormatter.checkAndFormatColorString(color);

				Element zoomLevelElement = document.createElement("m");
				zoomLevelElement.setAttribute("zoom-max", String.valueOf(zoom.intValue()));

				Element useElement = document.createElement("area");
				useElement.setAttribute("use", id);
				useElement.setAttribute("fill", color);
				useElement.setAttribute("fade", String.valueOf(zoom.intValue()));

				zoomLevelElement.appendChild(useElement);

				if (finalSubClassElement != null) {
					finalSubClassElement.appendChild(zoomLevelElement);
				}
			});

		} else if (symbol != null) {

			// We have ref to a symbol element
			// TODO: directly under source layer add use element unless we have classes
			// and/or zoom indicated ...
			Element useElement = document.createElement("area");
			useElement.setAttribute("id", id);
			useElement.setAttribute("src", symbol);

			ruleClassParentElement.appendChild(useElement);

		} else {

			subClassElement = super.generate(document);
		}

		if (ruleClassParentElement != null && subClassElement != null) {
			ruleClassParentElement.appendChild(subClassElement);
		}

		return subClassElement;
	}
}
