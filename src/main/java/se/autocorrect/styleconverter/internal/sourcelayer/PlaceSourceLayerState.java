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


import static se.autocorrect.styleconverter.internal.layer.LayerType.SYMBOL;
import static se.autocorrect.styleconverter.internal.layer.SourceLayers.PLACE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.data.TagWithValues;
import se.autocorrect.styleconverter.internal.data.rule.CaptionElementGenerator;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData;
import se.autocorrect.styleconverter.internal.layer.LayerStateBase;
import se.autocorrect.styleconverter.internal.layer.RuleGeneratorContext;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;

public class PlaceSourceLayerState extends LayerStateBase {

	public PlaceSourceLayerState(ConversionContext context) {
		super(context);
	}

	@Override
	public void processLayer(JsonStyleLayerData layer) {

		String type = layer.getType();

		if(type != null) {

			if(type.equals(SYMBOL.getType())) {

				SymbolProcessor symbolProcessor = new SymbolProcessor(this);
				symbolProcessor.processSymbolLayer(layer);

				registry.addRuleConsumerForSourceLayer(PLACE, getRuleSpecificConsumer());

			}else{

				logUnexpectedLayerType(layer, type);
			}
		}
	}

	@Override
	public Collection<String> getClassesForLayer() {
		return new ArrayList<>(Arrays.asList(classesStr.split(", \\r\\n")));
	}

	private static final String classesStr =
			"continent, \r\n"
					+ "country, \r\n"
					+ "state, \r\n"
					+ "province, \r\n"
					+ "city, \r\n"
					+ "town, \r\n"
					+ "village, \r\n"
					+ "hamlet, \r\n"
					+ "borough, \r\n"
					+ "suburb, \r\n"
					+ "quarter, \r\n"
					+ "neighbourhood, \r\n"
					+ "isolated_dwelling, \r\n"
					+ "island, \r\n"
					+ "aboriginal_lands";

	@Override
	public Consumer<RuleGeneratorContext> getRuleSpecificConsumer() {

		return ruleGeneratorContext -> {

			Document document = ruleGeneratorContext.getDocument();
			Element ruleParentElement = ruleGeneratorContext.getRuleParentElement();
			List<RuleData> ruleDatas = ruleGeneratorContext.getRuleDatas();

			Element classRuleParent = document.createElement("m");
			classRuleParent.setAttribute("k", "class");

			ruleParentElement.appendChild(classRuleParent);

			ruleDatas.forEach(ruleData -> {

				Element element = generateRuleElements(document, SymbolRuleData.class.cast(ruleData));

				if(element != null) {
					classRuleParent.appendChild(element);
				}
			});
		};
	}

	private Element generateRuleElements(Document document, SymbolRuleData ruleData) {

		Element ruleClassElement = null;
		String joinedClasses = ruleData.getJoinedClasses();
		TagWithValues tagWithValues = ruleData.getTagWithValues();

		if(joinedClasses != null) {

			ruleClassElement = document.createElement("m");

			if(ruleData.isFilterNegate()) {

				ruleClassElement.setAttribute("v", "-|" + joinedClasses);

			}else{

				ruleClassElement.setAttribute("v", joinedClasses);
			}

			Element tagValElement = null;

			if(tagWithValues != null) {

				tagValElement = createTagValueElement(document, tagWithValues);
				ruleClassElement.appendChild(tagValElement);
			}

			List<Element> elements = ruleData.isRefTextStyles() ? generateUseElements(document, ruleData) :  generateCaptionElements(document, ruleData);

			for (Element element : elements) {

				if(tagValElement != null) {
					tagValElement.appendChild(element);
				}else {
					ruleClassElement.appendChild(element);
				}
			}
		}

		return ruleClassElement;
	}

	private Element createTagValueElement(Document document, TagWithValues tagWithValues) {

		Element tagValElement = document.createElement("m");

		boolean inclusive = tagWithValues.inclusive();
		String tag = tagWithValues.tag();

		tagValElement.setAttribute("k", tag);

		if(tag.equals("rank")) {

			// TODO: process 'rank'; >= & <=
			String tagValue = "";
			String[] values = tagWithValues.values();

			// TODO: currently we only process ranks according to country (1-6)
			// TODO: extend the record TagWithValues to cater for more data
			// .. and different ranges for different classes like country vs cities ...
			Integer val = Integer.valueOf(values[0]);

			if(inclusive) {

				// value -> 6
				tagValElement.setAttribute("v", String.join("|", values));

			}else {

				// 1 -> value
				tagValElement.setAttribute("v", "-|" + String.join("|", values));
			}

		}else {

			String tagValue = String.join("|", tagWithValues.values());
			tagValue = inclusive ? tagValue : "-|" + tagValue;

			tagValElement.setAttribute("v", tagValue);
		}


		return tagValElement;
	}

	private List<Element> generateUseElements(Document document, SymbolRuleData ruleData) {

		List<Element> useElements = new ArrayList<>();

		String id = ruleData.getId();
		JsonStyleLayerData layer = ruleData.getLayer();

		Object textColor = layer.getPaint().getTextColor();
		Object textSize = layer.getLayout().getTextSize();

		if (textSize instanceof Map textSizeMap) {

			Object stopArr = textSizeMap.get("stops");

			if (stopArr instanceof List stopArrList) {

				for (Object list : stopArrList) {

					List<?> zoomList = List.class.cast(list);

					Double zoomVal = Double.class.cast(zoomList.get(0));
					Double sizeVal = Double.class.cast(zoomList.get(1));

					Element zoomLevelElement = document.createElement("m");
					zoomLevelElement.setAttribute("zoom-min", String.valueOf(zoomVal.intValue()));

					Element textElement = document.createElement("text");

					textElement.setAttribute("use", id);
					textElement.setAttribute("size", String.valueOf(sizeVal.intValue()));

					//					if (textColor instanceof String textColorStr) {
					//
					//						textColorStr = ColorFormatter.checkAndFormatColorString(textColorStr);
					//						textElement.setAttribute("fill", textColorStr);
					//					}

					zoomLevelElement.appendChild(textElement);
					useElements.add(zoomLevelElement);
				}
			}
		}

		return useElements;
	}

	private List<Element> generateCaptionElements(Document document, SymbolRuleData ruleData) {

		String id = ruleData.getId();
		JsonStyleLayerData layer = ruleData.getLayer();
		String symbolStr = ruleData.getSymbolStr();

		CaptionElementGenerator gen = new CaptionElementGenerator(document, layer, symbolStr, id);
		List<Element> captionElements = gen.createCaptionElements();

		return captionElements;
	}
}
