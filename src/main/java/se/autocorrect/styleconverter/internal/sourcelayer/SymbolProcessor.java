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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import se.autocorrect.styleconverter.internal.CollectedDataRegistry;
import se.autocorrect.styleconverter.internal.data.ElementData;
import se.autocorrect.styleconverter.internal.data.SymbolData;
import se.autocorrect.styleconverter.internal.data.TextData;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleState;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData;
import se.autocorrect.styleconverter.internal.data.rule.TextRuleData;
import se.autocorrect.styleconverter.internal.layer.LayerState;
import se.autocorrect.styleconverter.internal.sprite.cache.SpriteCache;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.LayoutData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.PaintData;
import se.autocorrect.styleconverter.util.ColorFormatter;
import se.autocorrect.styleconverter.util.TextUtils;

class SymbolProcessor {

	private RuleExtractor ruleExtractor;

	// Used in the rule processing callback
	private Map<Double, Integer> textSizes = new HashMap<>();
	private String finalFonts;
	private RuleState<SymbolRuleData.Builder> startRuleState;

	private Map<SymbolRuleData, List<SymbolRuleData>> toBeReplacedWithRuleData = new HashMap<>();
	private boolean symbolForEachRule;

	private LayerState layerState;

	private Consumer<SymbolRuleData> sourceLayerSpecificRuleLogic;
	private Function<String, String> iconImageTransformer;

	public SymbolProcessor(LayerState layerState) {
		this(layerState, false, null);
	}

	public SymbolProcessor(LayerState layerState, boolean refTextStyles) {
		this(layerState, false, null);
	}

	public SymbolProcessor(LayerState layerState, boolean refTextStyles, Function<String, String> iconImageTransformer ) {

		this.layerState = layerState;
		this.ruleExtractor = new RuleExtractorImpl(layerState.getContext());
		this.startRuleState = new SymbolRuleStartState(this, layerState);
		this.iconImageTransformer = iconImageTransformer;
	}

	void processSymbolLayer(JsonStyleLayerData layer) {
		processSymbolLayer(layer,  null);
	}

	void processSymbolLayer(JsonStyleLayerData layer, Consumer<SymbolRuleData> sourceLayerSpecificRuleLogic) {

		this.sourceLayerSpecificRuleLogic = sourceLayerSpecificRuleLogic;

		processTextData(layer);
		processRuleData(layer);
		processSymbolData(layer);
	}

	private void processTextData(JsonStyleLayerData layer) {

		TextData.Builder tdb = TextData.builder();

		String id = layer.getId();

		tdb.id(id);

		LayoutData layout = layer.getLayout();
		PaintData paint = layer.getPaint();

		String[] textFont = layout.getTextFont();

		String fonts = null;

		if (textFont != null) {

			fonts = String.join(",", textFont);
			tdb.fonts(fonts);
		}

		this.finalFonts = fonts;
		extractAndSetTextSizes(layer, tdb);

		Object fillColor = paint.getFillColor();

		if(fillColor == null) {

			Object textColor = paint.getTextColor();

			if(textColor instanceof String textColorStr) {

				textColorStr = ColorFormatter.checkAndFormatColorString(textColorStr);
				tdb.textColor(textColorStr);
			}
		}

		if (fillColor instanceof String fillColorStr) {

			fillColorStr = ColorFormatter.checkAndFormatColorString(fillColorStr);
			tdb.fill(fillColorStr);
		}

		String textField = layout.getTextField();

		if (textField != null) {

			textField = TextUtils.cleanUpTextField(textField);
			tdb.name(textField);
		}

		int maxZoom = layer.getLayout().getMaxZoom();
		int minZoom = layer.getLayout().getMinZoom();

		if(maxZoom > 0) {
			tdb.maxZoom(String.valueOf(maxZoom));
		}

		if (minZoom > 0) {
			tdb.minZoom(String.valueOf(minZoom));
		}

		Object textAnchor = layer.getLayout().getTextAnchor();

		if(textAnchor instanceof String textAnchorStr) {

			tdb.dy( textAnchorStr.equals("top") ? "-20" : "20" );

		}else if(textAnchor instanceof Map textAnchorMap) {

			// TODO: fix this: "text-anchor": {"base": 1, "stops": [[0, "left"], [8, "center"]]},
			// TODO: but, we 'only' have top and bottom. there seems to be no way to do left, center nor right
		}

		String sourceLayer = layer.getSourceLayer();

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();

		TextData textData = tdb.build();

		// TODO: check if we should collect all data first prior to builidng ...multi phase approach ..

		registry.addToSourceLayer(sourceLayer, textData);
	}

	private void processRuleData(JsonStyleLayerData layer) {

		Object textColor = layer.getPaint().getTextColor();

		ruleExtractor.processRuleDataForLayer(startRuleState, layer, rd -> {

			if(rd instanceof TextRuleData.Builder trd) {

				trd.textSizes(textSizes);

				if(textColor instanceof String textColorStr) {
					trd.textColor(textColorStr);
				}

				trd.fonts(finalFonts);

			}else if(rd instanceof SymbolRuleData.Builder srd) {

				//				srd.referenceTextStyles(refTextStyles);

				// TODO: replace with Consumer<?> ...
				//				checkAndSetIfSpecificGenProcessor(layer, srd);
			}
		});
	}

	private void processSymbolData(JsonStyleLayerData layer) {

		SymbolData.Builder sdb = SymbolData.builder();

		String id = layer.getId();

		sdb.id(id);

		LayoutData layout = layer.getLayout();
		PaintData paint = layer.getPaint();

		Object lineColor = paint.getLineColor();

		// TODO: also process the case when it is a map
		if (lineColor instanceof String lineColorStr) {

			lineColorStr = ColorFormatter.checkAndFormatColorString(lineColorStr);
			sdb.stroke(lineColorStr);
		}

		Object placement = layer.getLayout().getSymbolPlacement();

		if(placement instanceof String placementStr) {

			sdb.repeat(placementStr.equals("line")); // Can also be "point"

		}else if(placement instanceof Map placementMap) {

			// TODO: extract zoom levels and their placements ...
		}

		Object iconImage = layout.getIconImage();

		if(iconImage instanceof String iconImageStr) {

			String spriteRef = findSpriteRef(iconImageStr);

			if(iconImageTransformer != null) {
				spriteRef = iconImageTransformer.apply(spriteRef);
			}

			sdb.src(spriteRef);

		}else if(iconImage instanceof Map iconImageMap) {

			// TODO: extract zoom levels and their placements ...
			//System.out.println("+++ iconImage map"); // TODO: <- fix case!

		}else{

			// No icon image indicated
			// TODO: check if we should use default? User setting in preferences?
		}

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();

		if (!sdb.isRepeat()) {

			// Rule data is already extracted so we can process
			List<RuleData> ruleDatas = registry.getRuleDataForSourceLayer(layer.getSourceLayer());

			ruleDatas.forEach(ruleData -> {

				if (SymbolRuleData.class.isInstance(ruleData) && !registry.isProcessed(ruleData.getId())) {

					SymbolRuleData symbolRuleData = SymbolRuleData.class.cast(ruleData);

					if (symbolRuleData.getClassNodes() != null && symbolRuleData.getClassNodes().size() > 0) {

						String symbolStr = symbolRuleData.getSymbolStr();
						List<String> classNodes = symbolRuleData.getClassNodes();

						if(classNodes.size() == 1) {

							if(symbolStr != null && symbolStr.startsWith("{class}")) {

								String name = classNodes.get(0);
								symbolStr = symbolStr.replace("{class}", name);

								String spriteRef = findSpriteRef(symbolStr);

								SymbolRuleData replaceRuleData = symbolRuleData.toBuilder()
										.symbol(spriteRef)
										.build();

								sdb.src(spriteRef);

								addToReplaceWithData(symbolRuleData, replaceRuleData);
								registry.addNaturalClassNode(layer.getSourceLayer(), name);
							}

						}else {

							// TODO: figure out extra elements to generate
							// TODO: Collect all non used classes and indicate level from tag or other tag values ...
						}

					} else {

						// We have no class indications but maybe "level", rank" or something else, etc.

						String symbolStr = symbolRuleData.getSymbolStr();

						if(symbolStr != null && symbolStr.startsWith("{class}") && symbolRuleData.getTagWithValues() != null) {

							symbolForEachRule = true;

							// No classes indicated, but we have a symbol indicated ... and a tag
							SymbolData symbolData = sdb.build();

							layerState.getClassesForLayer().forEach(clz -> {

								String newSymbolsTr = symbolStr.replace("{class}", clz);
								String spriteRef = findSpriteRef(newSymbolsTr);

								SymbolRuleData replaceRuleData = (SymbolRuleData) symbolRuleData.toBuilder()
										.symbol(spriteRef)
										.id(id + "-" + clz)
										.classNodes(Collections.singletonList(clz))
										.build();

								SymbolData sd = symbolData.toBuilder()
										.src(spriteRef)
										.id(id + "-" + clz)
										.build()
										;

								registry.addToSourceLayer(layer.getSourceLayer(), sd);
								registry.addSyntheticClassNode(layer.getSourceLayer(), replaceRuleData, sd);

								addToReplaceWithData(symbolRuleData, replaceRuleData);
							});
						}
					}

					registry.addProcessed(ruleData.getId());
				}
			});
		}

		if(!symbolForEachRule) {

			SymbolData symbolData = sdb.build();
			registry.addToSourceLayer(layer.getSourceLayer(), symbolData);
		}

		symbolForEachRule = false;

		registry.addReplacementsForSourceLayer(layer.getSourceLayer(), toBeReplacedWithRuleData);
	}

	private void addToReplaceWithData(SymbolRuleData symbolRuleData, SymbolRuleData replaceRuleData) {

		toBeReplacedWithRuleData.computeIfPresent(symbolRuleData, (k, v) -> {

			v.add(replaceRuleData);
			return v;
		});

		toBeReplacedWithRuleData.computeIfAbsent(symbolRuleData, k -> {

			List<SymbolRuleData> newRuleData = new ArrayList<>();
			newRuleData.add(replaceRuleData);

			return newRuleData;
		});
	}

	private String findSpriteRef(String name) {

		SpriteCache spriteCache = SpriteCache.getInstance();

		return spriteCache.getSpriteFileBySpriteName(name).orElse(name);
	}

	private void extractAndSetTextSizes(JsonStyleLayerData layer, ElementData.Builder<? extends ElementData> edb) {

		Object textSize = layer.getLayout().getTextSize();

		if (textSize instanceof Double textSizeD) {

			int fontSizeVal = (int) Math.round(textSizeD);

			edb.textSize(String.valueOf(fontSizeVal));

		} else if (textSize instanceof Map textSizeMap) {

			Object stopArr = textSizeMap.get("stops");

			if (stopArr instanceof List stopArrList) {

				for (Object list : stopArrList) {

					List<?> zoomList = List.class.cast(list);

					Double zoomVal = Double.class.cast(zoomList.get(0));
					Double sizeVal = Double.class.cast(zoomList.get(1));

					int fontSizeVal = (int) Math.round(sizeVal);

					textSizes.put(zoomVal, fontSizeVal);
				}
			}
		}
	}

	void symbolRuleDataBuilt(SymbolRuleData symbolRuleData) {

		if(sourceLayerSpecificRuleLogic != null) {
			sourceLayerSpecificRuleLogic.accept(symbolRuleData);
		}
	}
}
