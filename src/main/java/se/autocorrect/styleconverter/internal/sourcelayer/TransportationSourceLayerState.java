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
import static se.autocorrect.styleconverter.internal.layer.LayerType.LINE;
import static se.autocorrect.styleconverter.internal.layer.LayerType.SYMBOL;
import static se.autocorrect.styleconverter.internal.layer.SourceLayers.TRANSPORTATION;
import static se.autocorrect.styleconverter.util.Collectors.toSingleElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.data.ElementData;
import se.autocorrect.styleconverter.internal.data.ElementDataVisitor;
import se.autocorrect.styleconverter.internal.data.TagWithValues;
import se.autocorrect.styleconverter.internal.data.rule.FillRuleData;
import se.autocorrect.styleconverter.internal.data.rule.LineRuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData;
import se.autocorrect.styleconverter.internal.layer.LayerStateBase;
import se.autocorrect.styleconverter.internal.layer.Resettable;
import se.autocorrect.styleconverter.internal.layer.RuleGeneratorContext;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.LayoutData;
import se.autocorrect.styleconverter.util.ColorFormatter;

public class TransportationSourceLayerState extends LayerStateBase implements Resettable {

	private ElementDataVisitor addRuleVisitor;
	private boolean onewayGenerated;

	private static Map<String, Map<Double, List<RuleData>>> normalizedData = new LinkedHashMap<>();
	private Function<String, String> spriteTransformer = spriteRef -> checkAndTransformOneWay(spriteRef);

	public TransportationSourceLayerState(ConversionContext context) {
		super(context);
		this.addRuleVisitor = new TransportationAddRuleVisitor(normalizedData);

		registry.addResettable(this);
	}

	@Override
	public void processLayer(JsonStyleLayerData layer) {

		String type = layer.getType();

		if(type != null) {

			if(type.equals(LINE.getType())) {

				LineProcessor lineProcessor = new LineProcessor(this);
				lineProcessor.processLineLayer(layer, lineRuleData  -> {
					lineRuleData.accept(addRuleVisitor);
				});

			}else if(type.equals(SYMBOL.getType())) {

				SymbolProcessor symbolProcessor = new SymbolProcessor(this, false, spriteTransformer);
				symbolProcessor.processSymbolLayer(layer, symbolRuleData  -> {
					symbolRuleData.accept(addRuleVisitor);
				});

			}else if(type.equals(FILL.getType())) {

				FillProcessor fillProcessor = new FillProcessor(this);
				fillProcessor.processFillLayer(layer, fillRuleData  -> {
					fillRuleData.accept(addRuleVisitor);
				});

			}else{

				logUnexpectedLayerType(layer, type);
			}

			registry.addRuleConsumerForSourceLayer(TRANSPORTATION, getRuleSpecificConsumer());
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

	@Override
	public void reset() {
		normalizedData.clear();
	}

	private String checkAndTransformOneWay(String spriteRef) {

		if (spriteRef.contains("oneway") && spriteRef.endsWith(".png")) {

			// Use default oneway icon image if the sprite is of type PNG, which is not
			// possible to be rotated for some obscure reason ...
			String onewaySvg = "assets:symbols/oneway.svg";

//			logger.info(String.format("Replacing PNG, %s, sprite with scalabe SVG: %s", spriteRef, onewaySvg));

			spriteRef = onewaySvg;
		}

		return spriteRef;
	}

	private static String classesStr =

			"motorway, \r\n"
					+ "trunk, \r\n"
					+ "primary, \r\n"
					+ "secondary, \r\n"
					+ "tertiary, \r\n"
					+ "minor, \r\n"
					+ "path, \r\n"
					+ "service, \r\n"
					+ "track, \r\n"
					+ "raceway, \r\n"
					+ "busway, \r\n"
					+ "bus_guideway, \r\n"
					+ "ferry, \r\n"
					+ "motorway_construction, \r\n"
					+ "trunk_construction, \r\n"
					+ "primary_construction, \r\n"
					+ "secondary_construction, \r\n"
					+ "tertiary_construction, \r\n"
					+ "minor_construction, \r\n"
					+ "path_construction, \r\n"
					+ "service_construction, \r\n"
					+ "track_construction, \r\n"
					+ "raceway_construction, \r\n"
					;

	@Override
	public Consumer<RuleGeneratorContext> getRuleSpecificConsumer() {

		return ruleGeneratorContext -> {

			Document document = ruleGeneratorContext.getDocument();
			Element ruleParentElement = ruleGeneratorContext.getRuleParentElement();

			Element classRuleParent = document.createElement("m");
			classRuleParent.setAttribute("k", "class");
			ruleParentElement.appendChild(classRuleParent);

			generateRuleElements(document, classRuleParent);
		};
	}

	private void generateRuleElements(Document document, Element classRuleParent) {

		generateOneWayIfPresent(document, classRuleParent);

		List<String> classKeys = new ArrayList<>(normalizedData.keySet());

		for (String joinedClasses : classKeys) {

			Map<Double, List<RuleData>> ruleDatas = normalizedData.get(joinedClasses);

			ruleDatas = mergeSameIntZoomLevels(ruleDatas);

			if(joinedClasses.indexOf(':') > 0) {
				joinedClasses = joinedClasses.split(":")[0];
			}

			Element subClassElement = generateElementForZoomLevel(document, joinedClasses, ruleDatas);
			classRuleParent.appendChild(subClassElement);
		}
	}

	private Map<Double, List<RuleData>> mergeSameIntZoomLevels(Map<Double, List<RuleData>> ruleDatas) {

		Map<Integer, Double> encountered = new HashMap<>();
		Map<Double, List<RuleData>> mergeCandidates = new HashMap<>();
		List<Double> toBeRemoved = new ArrayList<>();

		ruleDatas.entrySet().stream().forEach(entry -> {

			Double key = entry.getKey();
			int zoomAsInt = key.intValue();

			List<RuleData> rulesForZoom = entry.getValue();

			if(encountered.containsKey(zoomAsInt)) {

				mergeCandidates.put(encountered.get(zoomAsInt), rulesForZoom);
				toBeRemoved.add(key);

			}else{

				encountered.put(zoomAsInt, key);
			}
		});

		mergeCandidates.forEach((zoom, rds) -> {

			List<RuleData> originalRds = ruleDatas.get(zoom);

			// Remove old zoom to new in linewidths, same linewidth ...
			rds.stream().map(LineRuleData.class::cast).toList().forEach(lrd -> {

				Map<Double,Double> lineWidths = lrd.getLineWidths();

				Entry<Double,Double> replaceCandidate = lineWidths.entrySet().stream()
						.filter(e -> e.getKey().intValue() == zoom.intValue())
						.findFirst()
						.orElse(null);

				if(replaceCandidate != null) {

					lineWidths.remove(replaceCandidate.getKey());
					lineWidths.put(zoom, replaceCandidate.getValue());
				}
			});

			originalRds.addAll(rds);
		});

		toBeRemoved.forEach(zoom2Remove -> ruleDatas.remove(zoom2Remove));

		return ruleDatas;
	}

	private void generateOneWayIfPresent(Document document, Element ruleClassParent) {

		if(onewayGenerated) {
			return ;
		}

		List<ElementData> elements = registry.getElementDataForSourceLayer(TRANSPORTATION.getSourceLayer());

		// Normally only one ...
		SymbolRuleData onewayData = elements.stream()
				.filter(SymbolRuleData.class::isInstance)
				.map(SymbolRuleData.class::cast)
				.filter(srd -> srd.isOneway())
				.findFirst()
				.orElse(null);

		if(onewayData != null) {

			if(ruleClassParent != null) {

				Element oneWayElement = document.createElement("m");
				oneWayElement.setAttribute("k", "oneway");
				oneWayElement.setAttribute("v", "1");
				oneWayElement.setAttribute("zoom-min", onewayData.getZoomMin());

				String joinedClasses = onewayData.getClassNodes().stream()
						.collect(Collectors.joining("|"));

				Element classElement = document.createElement("m");
				classElement.setAttribute("k", "class");
				classElement.setAttribute("v", joinedClasses);

				Element symboleRefElement = document.createElement("symbol");
				symboleRefElement.setAttribute("use", onewayData.getId());

				classElement.appendChild(symboleRefElement);
				oneWayElement.appendChild(classElement);
				ruleClassParent.appendChild(oneWayElement);

				onewayGenerated = true;
			}
		}
	}

	private Element generateElementForZoomLevel(Document document, String joinedClasses, Map<Double, List<RuleData>> ruleDatas) {

		Element subClassElement = document.createElement("m");
		subClassElement.setAttribute("v", joinedClasses);

		TagWithValues tagWithValues = findTagWithValues(ruleDatas);
		Element tagRuleElemnt = null;

		if(tagWithValues != null) {

			tagRuleElemnt = document.createElement("m");
			tagRuleElemnt.setAttribute("k", tagWithValues.tag());

			String values = String.join("|", tagWithValues.values());

			String attributeValue = tagWithValues.inclusive() ? values : "-|" + values;

			tagRuleElemnt.setAttribute("v", attributeValue );
			subClassElement.appendChild(tagRuleElemnt);
		}

		final Element zoomParent = tagRuleElemnt != null ? tagRuleElemnt : subClassElement;

		ruleDatas.forEach((zoom, datas) -> {

			generateZoomElementForDatas(document, zoomParent, zoom, datas, joinedClasses);
		});

		return subClassElement;
	}

	private void generateZoomElementForDatas(Document document, Element subClassElement, Double zoom, List<RuleData> datas, String joinedClasses) {

		if (zoom > 0.0) {

			Element zoomLevelElement = document.createElement("m");
			zoomLevelElement.setAttribute("zoom-min", String.valueOf(zoom.intValue()));

			datas.forEach(data -> generateElementForRuleData(document, zoom, joinedClasses, zoomLevelElement, data));

			subClassElement.appendChild(zoomLevelElement);

		}else{

			datas.forEach(data -> generateElementForRuleData(document, zoom, joinedClasses, subClassElement, data));
		}
	}

	private void generateElementForRuleData(Document document, Double zoom, String joinedClasses,
			Element zoomLevelElement, RuleData data) {

		if (data instanceof LineRuleData lineRuleData) {

			Map<Double, Double> lineWidths = lineRuleData.getLineWidths();
			Double lineWidth = lineWidths.get(zoom);

			if (lineWidth != null) {
				generateLineElement(document, zoomLevelElement, zoom, lineRuleData, lineWidth, joinedClasses);
			}

		} else if (data instanceof SymbolRuleData symbolRuleData) {

			String symbolStr = symbolRuleData.getSymbolStr();

			generateSymbolElement(document, zoomLevelElement, zoom, data.getId(), symbolStr, joinedClasses);

		}else if(data instanceof FillRuleData){

			generateFillElement(document, zoomLevelElement, zoom, data.getId(), null, joinedClasses);
		}
	}

	private void generateLineElement(Document document, Element zoomLevelElement, Double zoom, LineRuleData data,
			Double lineWidth, String joinedClasses) {

		Element useElement = document.createElement("line");
		useElement.setAttribute("outline", data.getId());
		//		useElement.setAttribute("use", id);

		Object lineColor = data.getLayer().getPaint().getLineColor();

		if(lineColor instanceof String lineColorStr) {

			lineColorStr = ColorFormatter.checkAndFormatColorString(lineColorStr);

			useElement.setAttribute("stroke", lineColorStr);

		}else if(lineColor instanceof Map lineColorMap) {

			Object stopArr = lineColorMap.get("stops");

			if(stopArr instanceof List stopArrList) {

				for (Object list : stopArrList) {

					List<?> zoomList = List.class.cast(list);

					Double zoomVal = Double.class.cast(zoomList.get(0));

					if(Math.abs(zoom - zoomVal) < 0.01) {

						String colorStr = String.class.cast(zoomList.get(1));
						colorStr = ColorFormatter.checkAndFormatColorString(colorStr);

						useElement.setAttribute("stroke", colorStr);
					}
				}
			}
		}

		LayoutData layout = data.getLayer().getLayout();

		if(layout != null && layout.getLineCap() != null) {
			useElement.setAttribute("cap", layout.getLineCap());
		}

		// TODO: find a better, generic, way ...
		if(!joinedClasses.contains("path") && !joinedClasses.contains("minor")) {

			int intValue = lineWidth.intValue();

			if(intValue <= 0) {
				intValue = 1;
			}

			useElement.setAttribute("width", String.valueOf(intValue));
		}

		useElement.setAttribute("fade", String.valueOf(zoom.intValue()));

		zoomLevelElement.appendChild(useElement);
	}

	private void generateSymbolElement(Document document, Element zoomLevelElement, Double zoom, String id,
			Object object, String joinedClasses) {

		Element useElement = document.createElement("symbol");
		useElement.setAttribute("use", id);

		zoomLevelElement.appendChild(useElement);
	}

	private void generateFillElement(Document document, Element zoomLevelElement, Double zoom, String id, Object object,
			String joinedClasses) {

		Element useElement = document.createElement("area");
		useElement.setAttribute("use", id);

		zoomLevelElement.appendChild(useElement);
	}

	private TagWithValues findTagWithValues(Map<Double, List<RuleData>> ruleDatas) {

		return ruleDatas.entrySet().stream()
				.flatMap(e -> e.getValue().stream())
				.map(v -> v.getTagWithValues())
				.filter(Objects::nonNull)
				.distinct()
				.collect(toSingleElement());
	}
}
