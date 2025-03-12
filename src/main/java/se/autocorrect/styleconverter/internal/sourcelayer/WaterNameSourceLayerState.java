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
import static se.autocorrect.styleconverter.internal.layer.SourceLayers.WATER_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.data.rule.CaptionElementGenerator;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData;
import se.autocorrect.styleconverter.internal.layer.LayerStateBase;
import se.autocorrect.styleconverter.internal.layer.RuleGeneratorContext;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;

public class WaterNameSourceLayerState extends LayerStateBase {

	public WaterNameSourceLayerState(ConversionContext context) {
		super(context);
	}

	@Override
	public void processLayer(JsonStyleLayerData layer) {

		String type = layer.getType();

		if(type.equals(SYMBOL.getType())) {

			SymbolProcessor symbolProcessor = new SymbolProcessor(this);
			symbolProcessor.processSymbolLayer(layer);

			registry.addRuleConsumerForSourceLayer(WATER_NAME, getRuleSpecificConsumer());

		}else{

			logUnexpectedLayerType(layer, type);
		}
	}

	@Override
	public Collection<String> getClassesForLayer() {
		return Arrays.asList(classesStr.split(", \\r\\n"));
	}

	@Override
	public ConversionContext getContext() {
		return context;
	}

	private static String classesStr =
			"lake, \r\n"
					+ "bay, \r\n"
					+ "strait, \r\n"
					+ "sea, \r\n"
					+ "ocean, \r\n";

	@Override
	public Consumer<RuleGeneratorContext> getRuleSpecificConsumer() {

		return ruleGeneratorContext -> {

			Document document = ruleGeneratorContext.getDocument();
			Element ruleParentElement = ruleGeneratorContext.getRuleParentElement();
			List<RuleData> ruleDatas = ruleGeneratorContext.getRuleDatas();

			Element classRuleParent = document.createElement("m");
			classRuleParent.setAttribute("k", "class");

			ruleParentElement.appendChild(classRuleParent);

			ruleDatas.forEach(ruleData -> generateRuleElement(document, classRuleParent, SymbolRuleData.class.cast(ruleData)));
		};
	}

	private void generateRuleElement(Document document, Element classRuleParent, SymbolRuleData ruleData) {

		Element subClassElement = null;
		List<String> classNodes = ruleData.getClassNodes();

		if (classNodes != null && classNodes.size() > 0) {

			for (String classStr : classNodes) {

				subClassElement = document.createElement("m");

				if(ruleData.isFilterNegate()) {

					subClassElement.setAttribute("k", "class");
					subClassElement.setAttribute("v", "-|" + classStr);

					final Element finalSubClassElement = subClassElement;

					List<Element> elements = ruleData.isRefTextStyles() ? generateUseElements(document, ruleData) :  generateCaptionElements(document, ruleData);
					elements.forEach(element -> finalSubClassElement.appendChild(element));

				}else {

					subClassElement.setAttribute("v", classStr);

					List<Element> elements = ruleData.isRefTextStyles() ? generateUseElements(document, ruleData) :  generateCaptionElements(document, ruleData);

					for (Element element : elements) {
						subClassElement.appendChild(element);
					}
				}
			}

			classRuleParent.appendChild(subClassElement);
		}
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
