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
package se.autocorrect.styleconverter.internal.sourcelayer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import se.autocorrect.styleconverter.internal.data.ElementDataVisitorAdapter;
import se.autocorrect.styleconverter.internal.data.TagWithValues;
import se.autocorrect.styleconverter.internal.data.rule.FillRuleData;
import se.autocorrect.styleconverter.internal.data.rule.LineRuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;

class TransportationAddRuleVisitor extends ElementDataVisitorAdapter {

	private Map<String, Map<Double, List<RuleData>>> normalizedData;
	private SymbolRuleData onewayData;

	TransportationAddRuleVisitor(Map<String, Map<Double, List<RuleData>>> normalizedData) {
		this.normalizedData = normalizedData;
	}

	@Override
	public void visit(LineRuleData lineRuleData) {

		Map<Double, Double> lineWidths = lineRuleData.getLineWidths();
		String joinedClasses = lineRuleData.getJoinedClasses();

		if(joinedClasses == null) {
			joinedClasses = lineRuleData.getId();
		}

		// Add e.g 'brunnel' if present ... xtra layer over zoom.
		// class - 'brunnel' -> all zooms for classe/brunnel "joindclasses + brunnel-filter"
		TagWithValues tagWithValues = lineRuleData.getTagWithValues();

		if(tagWithValues != null) {

			joinedClasses += ":" + tagWithValues.tag() + ( tagWithValues.inclusive() ? "==" : "!=" ) + String.join("|", tagWithValues.values());
		}

		// classes -> zoom -> data
		Map<Double, List<RuleData>> zoom2Data = normalizedData.get(joinedClasses);

		if (zoom2Data != null) {

			lineWidths.forEach((zoom, widths) -> {

				List<RuleData> list = zoom2Data.get(zoom);

				if (list != null) {
					list.add(lineRuleData);
				}else {

					list = new ArrayList<>();
					list.add(lineRuleData);

					zoom2Data.put(zoom, list);
				}
			});

		} else {

			Map<Double, List<RuleData>> zoomData = new HashMap<>();

			lineWidths.entrySet().forEach(entry -> {

				Double zoom = entry.getKey();
				List<RuleData> ruleDatas = new ArrayList<>();
				ruleDatas.add(lineRuleData);

				zoomData.put(zoom, ruleDatas);
			});

			normalizedData.put(joinedClasses, zoomData);
		}
	}

	@Override
	public void visit(FillRuleData fillRuleData) {

		String joinedClasses = fillRuleData.getJoinedClasses();

		JsonStyleLayerData layer = fillRuleData.getLayer();
		Double minZoom = 0.0;

		if(layer != null) {
			minZoom = layer.getMinZoom();
		}

		//		if(joinedClasses == null && LayerClasses.validDbLayerClassNames(fillRuleData.getId())) {
		//			joinedClasses = fillRuleData.getId();
		//		}

		if(joinedClasses == null) {
			// Cannot generate rule for this ...
			return;
		}

		// classes -> zoom -> data
		Map<Double, List<RuleData>> zoom2Data = normalizedData.get(joinedClasses);

		if (zoom2Data != null) {

			List<RuleData> list = zoom2Data.get(minZoom);

			if (list != null) {
				list.add(fillRuleData);
			}

		} else {

			Map<Double, List<RuleData>> zoomData = new HashMap<>();

			List<RuleData> ruleDatas = new ArrayList<>();
			ruleDatas.add(fillRuleData);

			zoomData.put(minZoom, ruleDatas);

			normalizedData.put(joinedClasses, zoomData);
		}
	}

	@Override
	public void visit(SymbolRuleData symbolRuleData) {

		String joinedClasses = symbolRuleData.getJoinedClasses();

		JsonStyleLayerData layer = symbolRuleData.getLayer();
		Double minZoom = layer.getMinZoom();

		//		if(joinedClasses == null && LayerClasses.validDbLayerClassNames(symbolRuleData.getId())) {
		//			joinedClasses = symbolRuleData.getId();
		//		}

		List<String> classNodes = symbolRuleData.getClassNodes();

		if(joinedClasses == null && classNodes != null) {

			joinedClasses = classNodes.stream().collect(Collectors.joining("|"));
		}

		if(joinedClasses == null) {
			// Cannot generate rule for this ...
			return;
		}

		if (symbolRuleData.isOneway()) {

			// Special treatment for oneway
			this.onewayData = symbolRuleData;

		} else {

			// classes -> zoom -> data
			Map<Double, List<RuleData>> zoom2Data = normalizedData.get(joinedClasses);

			if (zoom2Data != null) {

				List<RuleData> list = zoom2Data.get(minZoom);

				if (list != null) {
					list.add(symbolRuleData);
				}

			} else {

				Map<Double, List<RuleData>> zoomData = new HashMap<>();

				List<RuleData> ruleDatas = new ArrayList<>();
				ruleDatas.add(symbolRuleData);

				zoomData.put(minZoom, ruleDatas);

				normalizedData.put(joinedClasses, zoomData);
			}
		}
	}

	SymbolRuleData getOneWay() {
		return onewayData;
	}
}
