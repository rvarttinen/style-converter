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


import static se.autocorrect.styleconverter.internal.layer.LayerType.LINE;
import static se.autocorrect.styleconverter.internal.layer.SourceLayers.WATERWAY;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.data.TagWithValues;
import se.autocorrect.styleconverter.internal.data.rule.LineRuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.layer.LayerStateBase;
import se.autocorrect.styleconverter.internal.layer.RuleGeneratorContext;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.util.ColorFormatter;

public class WaterWaySourceLayerState extends LayerStateBase {

	public WaterWaySourceLayerState(ConversionContext context) {
		super(context);
	}

	@Override
	public void processLayer(JsonStyleLayerData layer) {

		String type = layer.getType();

		if(type.equals(LINE.getType())) {

			LineProcessor lineProcessor = new LineProcessor(this);
			lineProcessor.processLineLayer(layer);

			registry.addRuleConsumerForSourceLayer(WATERWAY, getRuleSpecificConsumer());

		}else{

			logUnexpectedLayerType(layer, type);
		}
	}

	@Override
	public Consumer<RuleGeneratorContext> getRuleSpecificConsumer() {

		return ruleGeneratorContext -> {

			Document document = ruleGeneratorContext.getDocument();
			Element ruleParentElement = ruleGeneratorContext.getRuleParentElement();

			Element classRuleParent = document.createElement("m");
			classRuleParent.setAttribute("k", "class");
			ruleParentElement.appendChild(classRuleParent);

			List<RuleData> ruleDatas = ruleGeneratorContext.getRuleDatas();

			ruleDatas.forEach(ruleData -> generateRuleElements(document, LineRuleData.class.cast(ruleData), classRuleParent));
		};
	}

	private void generateRuleElements(Document document, LineRuleData ruleData, Element classRuleParent) {

		Map<Double,String> lineColors = ruleData.getLineColors();
		Map<Double,Double> lineWidths = ruleData.getLineWidths();

		Element subClassElement = null;
		Element tagElement = null;

		if (!lineColors.isEmpty() || !lineWidths.isEmpty()) {

			subClassElement = document.createElement("m");

			String id = ruleData.getId();
			String joinedClasses = ruleData.getJoinedClasses();

			TagWithValues tagWithValues = ruleData.getTagWithValues();

			if (joinedClasses != null) {
				subClassElement.setAttribute("v", joinedClasses);
			}

			if(tagWithValues != null) {

				tagElement = document.createElement("m");
				tagElement.setAttribute("k", tagWithValues.tag());

				String tagAttributeVal = String.join("|", tagWithValues.values());
				tagAttributeVal = tagWithValues.inclusive() ? tagAttributeVal : "-|" + tagAttributeVal;

				tagElement.setAttribute("v", tagAttributeVal);
				subClassElement.appendChild(tagElement);
			}

			final Element finalSubClassElement = tagElement == null ? subClassElement : tagElement;

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

				if(!potentialMergeKeys.isEmpty() && potentialMergeKeys.contains(zoom)) {

					Double lineWidth = lineWidths.get(zoom);

					useElement.setAttribute("width", String.valueOf(lineWidth.intValue()));
				}

				zoomLevelElement.appendChild(useElement);
				finalSubClassElement.appendChild(zoomLevelElement);
			});

			potentialMergeKeys.forEach(mk -> lineWidths.remove(mk));

			lineWidths.forEach((zoom, width) -> {

				Element zoomLevelElement = document.createElement("m");
				zoomLevelElement.setAttribute("zoom-min", String.valueOf(zoom.intValue()));

				Element useElement = document.createElement("line");
				useElement.setAttribute("outline", id);
				useElement.setAttribute("use", id);
				//				useElement.setAttribute("stroke", color);
				useElement.setAttribute("width", String.valueOf(width.intValue()));
				useElement.setAttribute("fade", String.valueOf(zoom.intValue()));

				zoomLevelElement.appendChild(useElement);
				finalSubClassElement.appendChild(zoomLevelElement);
			});
		}

		if(subClassElement != null) {
			classRuleParent.appendChild(subClassElement);
		}
	}

	private List<Double> calculatePotentialMergeKeys(Map<Double, String> lineColors, Map<Double, Double> lineWidths) {

		List<Double> resultingKeys = new ArrayList<>();

		Set<Double> colorsKeySet = lineColors.keySet();
		Set<Double> widthsKeySet = lineWidths.keySet();

		if(colorsKeySet.equals(widthsKeySet)) {
			resultingKeys.addAll(colorsKeySet);
		}else{

			colorsKeySet.forEach(ck -> {

				if(widthsKeySet.contains(ck)) {
					resultingKeys.add(ck);
				}
			});
		}

		return resultingKeys;
	}
}
