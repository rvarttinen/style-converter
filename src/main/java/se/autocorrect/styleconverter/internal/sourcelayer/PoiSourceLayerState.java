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
import static se.autocorrect.styleconverter.internal.layer.SourceLayers.POI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.data.rule.CaptionElementGenerator;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData;
import se.autocorrect.styleconverter.internal.layer.LayerStateBase;
import se.autocorrect.styleconverter.internal.layer.RuleGeneratorContext;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.util.ColorFormatter;

public class PoiSourceLayerState extends LayerStateBase {

	public PoiSourceLayerState(ConversionContext context) {
		super(context);
	}

	@Override
	public void processLayer(JsonStyleLayerData layer) {

		String type = layer.getType();

		if(type != null) {

			if(type.equals(SYMBOL.getType())) {

				SymbolProcessor symbolProcessor = new SymbolProcessor(this);
				symbolProcessor.processSymbolLayer(layer);

				registry.addRuleConsumerForSourceLayer(POI, getRuleSpecificConsumer());

			}else{

				logUnexpectedLayerType(layer, type);
			}
		}
	}

	public Optional<String> getDefaultSymbolForClass(String className) {

		String symbol = null;

		switch (className) {
		case "railway":
			symbol = "assets:symbols/transport/train_station2.svg";
			break;
		case "bus":
			symbol ="assets:symbols/transport/bus_stop.svg";
			break;
		case "lodging":
			symbol = "assets:symbols/accomodation/hotel2.svg";
			break;
		case "alcohol_shop":
			symbol = "assets:symbols/shopping/alcohol.svg";
			break;
		case "grocery":
			symbol ="assets:symbols/shopping/greengrocer.svg";
			break;
		case "beer":
			symbol ="assets:symbols/food/biergarten.svg";
			break;
		case "cafe":
			symbol ="assets:symbols/food/cafe.svg";
			break;
		case "school":
			symbol ="assets:symbols/education/school.svg";
			break;
		}

		return Optional.ofNullable(symbol);
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
			"shop, \r\n"
					+ "office, \r\n"
					+ "town_hall, \r\n"
					+ "golf, \r\n"
					+ "fast_food, \r\n"
					+ "park, \r\n"
					+ "bus, \r\n"
					+ "railway, \r\n"
					+ "aerialway, \r\n"
					+ "entrance, \r\n"
					+ "campsite, \r\n"
					+ "laundry, \r\n"
					+ "grocery, \r\n"
					+ "library, \r\n"
					+ "college, \r\n"
					+ "lodging, \r\n"
					+ "ice_cream, \r\n"
					+ "post, \r\n"
					+ "cafe, \r\n"
					+ "school, \r\n"
					+ "alcohol_shop, \r\n"
					+ "bar, \r\n"
					+ "harbor, \r\n"
					+ "car, \r\n"
					+ "hospital, \r\n"
					+ "cemetery, \r\n"
					+ "attraction, \r\n"
					+ "beer, \r\n"
					+ "music, \r\n"
					+ "stadium, \r\n"
					+ "art_gallery, \r\n"
					+ "clothing_store, \r\n"
					+ "swimming, \r\n"
					+ "castle, \r\n"
					+ "atm, \r\n"
					+ "fuel";

	@Override
	public Consumer<RuleGeneratorContext> getRuleSpecificConsumer() {

		return ruleGeneratorContext -> {

			Document document = ruleGeneratorContext.getDocument();
			Element ruleParentElement = ruleGeneratorContext.getRuleParentElement();
			List<RuleData> ruleDatas = ruleGeneratorContext.getRuleDatas();

			Element classRuleParent = document.createElement("m");
			classRuleParent.setAttribute("k", "class");

			ruleParentElement.appendChild(classRuleParent);

			// TODO: add element for every class there is except those already refeenced

			ruleDatas.forEach(ruleData -> generateRuleElements(document, classRuleParent, SymbolRuleData.class.cast(ruleData)));
		};
	}

	private void generateRuleElements(Document document, Element classRuleParent, SymbolRuleData ruleData) {

		Element subClassElement = null;
		List<String> classNodes = ruleData.getClassNodes();

		String joinedClasses = ruleData.getJoinedClasses();
		JsonStyleLayerData layer = ruleData.getLayer();
		boolean refTextStyles = ruleData.isRefTextStyles();

		String id = ruleData.getId();
		String zoomMin = ruleData.getZoomMin();

		if (classNodes != null && classNodes.size() > 0) {

			for (String classStr : classNodes) {

				subClassElement = document.createElement("m");

				if(ruleData.isFilterNegate()) {

					subClassElement.setAttribute("k", "class");
					subClassElement.setAttribute("v", "-|" + classStr);

					final Element finalSubClassElement = subClassElement;

					List<Element> elements = refTextStyles ? generateUseElements(document, ruleData) :  generateCaptionElements(document, ruleData);
					elements.forEach(element -> finalSubClassElement.appendChild(element));

				}else {

					subClassElement.setAttribute("v", classStr);

					List<Element> elements = refTextStyles ? generateUseElements(document, ruleData) :  generateCaptionElements(document, ruleData);

					for (Element element : elements) {
						subClassElement.appendChild(element);
					}
				}

				classRuleParent.appendChild(subClassElement);
			}


		} else if (refTextStyles) {

			// TODO: if there are several references to other text-styles
			// TODO: map the classes to relevant text-styles ...

			String layerId = layer.getId();

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
						zoomLevelElement.setAttribute("zoom-max", String.valueOf(zoomVal.intValue()));

						Element textElement = document.createElement("text");

						textElement.setAttribute("use", id);
						textElement.setAttribute("size", String.valueOf(sizeVal.intValue()));
						textElement.setAttribute("priority", "1");

						if (textColor instanceof String textColorStr) {

							textColorStr = ColorFormatter.checkAndFormatColorString(textColorStr);
							textElement.setAttribute("stroke", textColorStr);
						}

						zoomLevelElement.appendChild(textElement);
						classRuleParent.appendChild(zoomLevelElement);
					}
				}

			}else {

				Element useElement = document.createElement("text");
				useElement.setAttribute("use", layerId);

				subClassElement = document.createElement("m");

				if(zoomMin != null) {
					subClassElement.setAttribute("zoom-min", zoomMin);
				}

				if(joinedClasses != null && joinedClasses.length() > 0) {

					subClassElement.setAttribute("v", joinedClasses);

				}else {

					// TODO: "move" use element "up" in the hierarchy?
					// TODO: ... or, assume, id is the same as db class .?..
					//					subClassElement.setAttribute("v", "");
				}

				subClassElement.appendChild(useElement);
			}

		} else {

			// Only one ref and no filter data ...
			String layerId = layer.getId();

			Node parentNode = classRuleParent.getParentNode();

			if (parentNode != null) {

				Element useElement = document.createElement("text");
				useElement.setAttribute("use", layerId);

				// TODO: fix this one ..!.
				//				parentNode.removeChild(classRuleParent);
				//				parentNode.appendChild(useElement);
			}
		}

		// TODO: it sould be enough up until here, but: what criterea determines next section?
		// TODO: "old" code - remove?

		Object textColor = layer.getPaint().getTextColor();
		Object textSize = layer.getLayout().getTextSize();

		if (subClassElement == null && textSize instanceof Map textSizeMap) {

			Object stopArr = textSizeMap.get("stops");

			if (stopArr instanceof List stopArrList) {

				if (classNodes != null && classNodes.size() > 0) {

					for (String classStr : classNodes) {

						subClassElement = document.createElement("m");
						subClassElement.setAttribute("v", classStr);

						for (Object list : stopArrList) {

							List<?> zoomList = List.class.cast(list);

							Double zoomVal = Double.class.cast(zoomList.get(0));
							Double sizeVal = Double.class.cast(zoomList.get(1));

							Element zoomLevelElement = document.createElement("m");
							zoomLevelElement.setAttribute("zoom-max", String.valueOf(zoomVal.intValue()));

							Element captionElement = document.createElement("caption");

							captionElement.setAttribute("size", String.valueOf(sizeVal.intValue()));
							captionElement.setAttribute("k", "name");
							//							captionElement.setAttribute("priority", "1"); // TODO: extract from filter somehow?

							if (textColor instanceof String textColorStr) {

								textColorStr = ColorFormatter.checkAndFormatColorString(textColorStr);
								captionElement.setAttribute("stroke", textColorStr);
							}

							zoomLevelElement.appendChild(captionElement);
							subClassElement.appendChild(zoomLevelElement);
						}
					}
				}
			}
		}

		if(subClassElement != null) {
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
