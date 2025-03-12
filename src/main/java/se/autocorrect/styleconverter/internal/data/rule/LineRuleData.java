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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.internal.data.ElementDataVisitor;
import se.autocorrect.styleconverter.internal.data.TagWithValues;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.util.ColorFormatter;

public class LineRuleData extends RuleData {

	private Map<Double, String> lineColors;
	private Map<Double, Double> lineWidths;
	private String strokeColor;
	private List<RuleCondition> conditions;

	private LineRuleData(String id, JsonStyleLayerData layer, String joinedClasses, String zoomMin, String sourcelayer,
			List<String> classNodes, Map<Double, String> lineColors, Map<Double, Double> lineWidths, String strokeColor,
			TagWithValues tagWithValues) {

		super(id, layer, joinedClasses, zoomMin, sourcelayer, classNodes, tagWithValues, false);

		this.lineColors = lineColors;
		this.lineWidths = lineWidths;
		this.strokeColor = strokeColor;
	}

	public Map<Double, Double> getLineWidths() {
		return lineWidths;
	}

	public Map<Double, String> getLineColors() {
		return lineColors;
	}

	public String getStrokeColor() {
		return strokeColor;
	}

	void setConditions(List<RuleCondition> conditions) {
		this.conditions = conditions;
	}

	public boolean hasConditions() {
		return conditions != null;
	}

	public List<RuleCondition> getConditions() {
		return conditions;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public void accept(ElementDataVisitor visitor) {
		visitor.visit(this);
	}

	public static class Builder extends RuleData.Builder {

		private Map<Double, String> lineColors;
		private Map<Double, Double> lineWidths;
		private String strokeColor;
		private List<RuleCondition> conditions;

		private Builder() {
		}

		public Builder lineColors(Map<Double, String> lineColors) {
			this.lineColors = lineColors;
			return this;
		}

		public Builder lineWidths(Map<Double, Double> lineWidths) {
			this.lineWidths = lineWidths;
			return this;
		}

		public Builder strokeColor(String strokeColor) {
			this.strokeColor = strokeColor;
			return this;
		}

		public void addCondition(RuleCondition condition) {

			if (conditions == null) {
				conditions = new ArrayList<>();
			}

			conditions.add(condition);
		}

		@Override
		public LineRuleData build() {

			LineRuleData lineRuleData = new LineRuleData(id, layer, joinedClasses, zoomMin, sourceLayer, classNodes,
					lineColors, lineWidths, strokeColor, tagWithValues);

			if (conditions != null) {
				lineRuleData.setConditions(conditions);
			}

			return lineRuleData;
		}
	}

	@Override
	public Element generate(Document document) {

		Element subClassElement;

		if (!lineColors.isEmpty() || !lineWidths.isEmpty()) {

			subClassElement = document.createElement("m");

			if (joinedClasses != null) {
				subClassElement.setAttribute("v", joinedClasses);
			} else {
				subClassElement.setAttribute("v", id);
			}

			// TODO: lowest zoom level - use that entry's color as default stroke color?
			List<Double> potentialMergeKeys = calculatePotentialMergeKeys(lineColors, lineWidths);

			lineColors.forEach((zoom, color) -> {

				color = ColorFormatter.checkAndFormatColorString(color);

				Element zoomLevelElement = document.createElement("m");
				zoomLevelElement.setAttribute("zoom-max", String.valueOf(zoom.intValue()));

				Element useElement = document.createElement("line");
				useElement.setAttribute("outline", id);
				useElement.setAttribute("use", id);
				useElement.setAttribute("stroke", color);
				useElement.setAttribute("fade", String.valueOf(zoom.intValue()));

				if (!potentialMergeKeys.isEmpty() && potentialMergeKeys.contains(zoom)) {

					Double lineWidth = lineWidths.get(zoom);

					useElement.setAttribute("width", String.valueOf(lineWidth.intValue()));
				}

				zoomLevelElement.appendChild(useElement);
				subClassElement.appendChild(zoomLevelElement);
			});

			potentialMergeKeys.forEach(mk -> lineWidths.remove(mk));

			lineWidths.forEach((zoom, width) -> {

				Element zoomLevelElement = document.createElement("m");
				zoomLevelElement.setAttribute("zoom-min", String.valueOf(zoom.intValue()));

				Element useElement = document.createElement("line");
				useElement.setAttribute("outline", id);
				useElement.setAttribute("use", id);
				// useElement.setAttribute("stroke", color);
				useElement.setAttribute("width", String.valueOf(width.intValue()));
				useElement.setAttribute("fade", String.valueOf(zoom.intValue()));

				zoomLevelElement.appendChild(useElement);
				subClassElement.appendChild(zoomLevelElement);
			});

		} else {
			subClassElement = super.generate(document);
		}

		return subClassElement;
	}

	private List<Double> calculatePotentialMergeKeys(Map<Double, String> lineColors, Map<Double, Double> lineWidths) {

		List<Double> resultingKeys = new ArrayList<>();

		Set<Double> colorsKeySet = lineColors.keySet();
		Set<Double> widthsKeySet = lineWidths.keySet();

		if (colorsKeySet.equals(widthsKeySet)) {
			resultingKeys.addAll(colorsKeySet);
		} else {

			colorsKeySet.forEach(ck -> {

				if (widthsKeySet.contains(ck)) {
					resultingKeys.add(ck);
				}
			});
		}

		return resultingKeys;
	}
}
