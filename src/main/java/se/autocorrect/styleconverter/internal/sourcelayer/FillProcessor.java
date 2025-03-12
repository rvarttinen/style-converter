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


import static se.autocorrect.styleconverter.util.RGBUtils.FORMATTED_HEX_RGBSTR_LENGTH;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import se.autocorrect.styleconverter.internal.CollectedDataRegistry;
import se.autocorrect.styleconverter.internal.data.AreaData;
import se.autocorrect.styleconverter.internal.data.LineData;
import se.autocorrect.styleconverter.internal.data.rule.FillRuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleState;
import se.autocorrect.styleconverter.internal.layer.LayerState;
import se.autocorrect.styleconverter.internal.sprite.cache.SpriteCache;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.PaintData;
import se.autocorrect.styleconverter.util.ColorFormatter;
import se.autocorrect.styleconverter.util.RGB;
import se.autocorrect.styleconverter.util.RGBUtils;

class FillProcessor {

	private RuleExtractorImpl ruleExtractor;
	private RuleState<FillRuleData.Builder> startRuleState;

	private Map<Double, String> fillColors = new HashMap<>();
	private Consumer<FillRuleData> layerSpecificRuleConsumer;

	public FillProcessor(LayerState layerState) {

		this.ruleExtractor = new RuleExtractorImpl(layerState.getContext());
		this.startRuleState = new FillRuleStartState(this);
	}

	public void processFillLayer(JsonStyleLayerData layer) {
		processFillLayer(layer, null);
	}

	public void processFillLayer(JsonStyleLayerData layer, Consumer<FillRuleData> layerSpecificRuleConsumer) {

		this.layerSpecificRuleConsumer = layerSpecificRuleConsumer;

		String areaId = layer.getId();

		AreaData.Builder ad = AreaData.builder();
		ad.id(areaId);

		PaintData paint = layer.getPaint();
		Object fillOpacity = paint.getFillOpacity();

		if(fillOpacity instanceof Double opacity) {

			ad.fillOpacity(String.valueOf(opacity));

		}else if (fillOpacity instanceof Map opactiyMap) {

			// TODO: replace with map holding all values for zoom-levels and create rule(s)
			// accordingly
			List<List<Double>> stops = (List<List<Double>>) opactiyMap.get("stops");

			// TODO: use in color values instead ...
			Optional<Double> max = stops.stream().flatMap(l -> l.stream()).max(Double::compareTo);
			max.ifPresent(d -> ad.fade(String.valueOf(d.intValue())));
		}

		Object fillColor = paint.getFillColor();

		if (fillColor instanceof String fillColorStr) {

			fillColorStr = ColorFormatter.checkAndFormatColorString(fillColorStr);

			if(fillOpacity instanceof Double opacity && fillColorStr.length() == FORMATTED_HEX_RGBSTR_LENGTH) {

				RGB rgb = RGBUtils.parse(fillColorStr);
				fillColorStr = RGBUtils.rgbaToHexString(opacity.floatValue(), rgb);
			}

			ad.fill(fillColorStr);

		}else if(fillColor instanceof Map fillColorMap) {

			Object stopArr = fillColorMap.get("stops");

			if(stopArr instanceof List<?> stopArrayList) {

				if(stopArr instanceof List stopArrList) {

					for (Object list : stopArrList) {

						List<?> zoomList = List.class.cast(list);

						Double zoomVal = Double.class.cast(zoomList.get(0));
						String colorStr = String.class.cast(zoomList.get(1));

						fillColors.put(zoomVal, colorStr);
					}
				}
			}
		}

		String fillPattern = paint.getFillPattern();

		if(fillPattern != null) {

			SpriteCache cache = SpriteCache.getInstance();

			Optional<String> spriteOp = cache.getSpriteFileBySpriteName(fillPattern);
			spriteOp.ifPresent(ad::fillPattern);
		}

		Object fillOutLineColor = paint.getFillOutLineColor();

		if (fillOutLineColor != null) {
			processFillOutLineColors(layer, fillOutLineColor, fillColor);
		}

		ruleExtractor.processRuleDataForLayer(startRuleState, layer, rd -> {

			if (rd instanceof FillRuleData.Builder frd) {
				frd.fillColors(fillColors);
			}
		});

		String sourceLayer = layer.getSourceLayer();
		AreaData areaData = ad.build();

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();
		registry.addToSourceLayer(sourceLayer, areaData);
	}

	private void processFillOutLineColors(JsonStyleLayerData layer, Object fillOutLineColor, Object fillColor) {

		// TODO: also, if present in mapping data -> add extrusion rendering rule (e.g. 3D buildings)

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();
		String sourceLayer = layer.getSourceLayer();

		if(fillOutLineColor instanceof String fillOutLineColorStr) {

			String id = layer.getId();

			fillOutLineColorStr = ColorFormatter.checkAndFormatColorString(fillOutLineColorStr);

			if (fillOutLineColorStr != null) {

				LineData.Builder ld = LineData.builder();
				ld.id(id);
				ld.strokeColor(fillOutLineColorStr);
				LineData lineData = ld.build();
				registry.addToSourceLayer(sourceLayer, lineData);

			} else {

				AreaData.Builder ad = AreaData.builder();
				ad.id(id);

				if (fillColor instanceof String fillColorStr) {
					ad.fill(fillColorStr);
				}

				AreaData areaData = ad.build();
				registry.addToSourceLayer(sourceLayer, areaData);
			}

		}else if(fillOutLineColor instanceof Map outLineColorMap) {

			Object stopArr = outLineColorMap.get("stops");

			if(stopArr instanceof List<?> stopArrayList) {

				List<List<?>> stopArrayListOfList = (List<List<?>>) stopArrayList;

				boolean addedLineDataAndAreaData = false;

				for (List<?> listOfStop : stopArrayListOfList) {

					Double zoomLevel = (Double) listOfStop.get(0);
					String colorStr = (String) listOfStop.get(1);

					colorStr = ColorFormatter.checkAndFormatColorString(colorStr);

					String lineId = layer.getId();

					if(!addedLineDataAndAreaData) {

						LineData.Builder ld = LineData.builder(); // Use ref from rule
						ld.id(lineId);

						ld.strokeColor(colorStr);
						ld.width("1.0"); // TODO: look up and ref to a "parent" (see "fix" in openmaptiles.xml)
						ld.cap("butt");
						ld.fade(String.valueOf( zoomLevel.intValue() + 1));

						AreaData.Builder ad = AreaData.builder();
						ad.id(lineId);

						ad.fade(String.valueOf(zoomLevel.intValue() + 1));

						if(fillColor instanceof String fillColorStr) {
							ad.fill(fillColorStr);
						}

						AreaData areaData = ad.build();
						LineData lineData = ld.build();

						registry.addToSourceLayer(sourceLayer, areaData);
						registry.addToSourceLayer(sourceLayer, lineData);

						addedLineDataAndAreaData = true;
					}
				}
			}
		}
	}

	void fillRuleDataBuilt(FillRuleData fillRuleData) {

		if(layerSpecificRuleConsumer != null) {
			layerSpecificRuleConsumer.accept(fillRuleData);
		}
	}
}
