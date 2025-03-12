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


import static se.autocorrect.styleconverter.internal.layer.LayerType.FILL;
import static se.autocorrect.styleconverter.internal.layer.SourceLayers.BUILDING;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.data.AreaData;
import se.autocorrect.styleconverter.internal.data.ElementData;
import se.autocorrect.styleconverter.internal.data.LineData;
import se.autocorrect.styleconverter.internal.data.rule.FillRuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.layer.LayerStateBase;
import se.autocorrect.styleconverter.internal.layer.RuleGeneratorContext;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.util.ColorFormatter;

public class BuildingSourceLayerState extends LayerStateBase {

	//	private Element ruleClassParentElement;
	//	private Element ruleParentElement;

	public BuildingSourceLayerState(ConversionContext context) {
		super(context);
	}

	@Override
	public void processLayer(JsonStyleLayerData layer) {

		String type = layer.getType();

		if(type != null) {

			if(type.equals(FILL.getType())) {

				FillProcessor fillProcessor = new FillProcessor(this);
				fillProcessor.processFillLayer(layer);

				registry.addRuleConsumerForSourceLayer(BUILDING, getRuleSpecificConsumer());

			}else{

				logUnexpectedLayerType(layer, type);
			}
		}
	}

	@Override
	public ConversionContext getContext() {
		return context;
	}

	@Override
	public Consumer<RuleGeneratorContext> getRuleSpecificConsumer() {

		return ruleGeneratorContext -> {

			Document document = ruleGeneratorContext.getDocument();
			Element ruleParentElement = ruleGeneratorContext.getRuleParentElement();
			List<RuleData> ruleDatas = ruleGeneratorContext.getRuleDatas();

			ruleDatas.forEach(
					ruleData -> generateRuleElement(document, ruleParentElement, FillRuleData.class.cast(ruleData)));
		};
	}

	private void generateRuleElement(Document document, Element ruleParentElement, FillRuleData ruleData) {

		Map<Double,String> fillColors = ruleData.getFillColors();
		String joinedClasses = ruleData.getJoinedClasses();

		if (fillColors != null && !fillColors.isEmpty()) {

			String id = ruleData.getId();
			Element subClassElement = null;

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

				if(finalSubClassElement != null) {
					finalSubClassElement.appendChild(zoomLevelElement);
				}

				List<ElementData> nonRuleElements = registry.getElementDataForSourceLayer(ruleData.getSourcelayer());

				List<LineData> lineDataElements = nonRuleElements.stream()
						.filter(LineData.class::isInstance)
						.map(LineData.class::cast)
						.toList();

				List<AreaData> areaDataElements = nonRuleElements.stream()
						.filter(AreaData.class::isInstance)
						.map(AreaData.class::cast)
						.filter(ad -> !ad.getAreaId().equals(id))
						.toList();

				lineDataElements.forEach(line -> {

					Element useElem = document.createElement("line");
					useElem.setAttribute("use", line.getlineId());


					zoomLevelElement.appendChild(useElem);
				});

				areaDataElements.forEach(area -> {

					Element useElem = document.createElement("area");
					useElem.setAttribute("use", area.getAreaId());
					zoomLevelElement.appendChild(useElem);
				});

				if(finalSubClassElement == null) {
					ruleParentElement.appendChild( zoomLevelElement );
				}else {
					finalSubClassElement.appendChild(zoomLevelElement);
					ruleParentElement.appendChild(finalSubClassElement);
				}
			});
		}
	}
}
