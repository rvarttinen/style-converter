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


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import se.autocorrect.styleconverter.internal.CollectedDataRegistry;
import se.autocorrect.styleconverter.internal.data.LineData;
import se.autocorrect.styleconverter.internal.data.rule.LineRuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleState;
import se.autocorrect.styleconverter.internal.layer.LayerState;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.LayoutData;
import se.autocorrect.styleconverter.util.ColorFormatter;
import se.autocorrect.styleconverter.util.StringChecks;

public class LineProcessor {

	private LayerState layerState;
	private RuleExtractor ruleExtractor;

	private Map<Double, String> lineColors = new HashMap<>();
	private Map<Double, Double> lineWidths = new HashMap<>();

	private RuleState<LineRuleData.Builder> startRuleState;
	private Consumer<LineRuleData> ruleDataConsumer;

	public LineProcessor(LayerState layerState) {

		this.layerState = layerState;
		this.ruleExtractor = new RuleExtractorImpl(layerState.getContext());
		this.startRuleState = new LineRuleStartState(this);
	}

	public void processLineLayer(JsonStyleLayerData layer) {
		processLineLayer(layer, null);
	}

	public void processLineLayer(JsonStyleLayerData layer, Consumer<LineRuleData> ruleDataConsumer) {

		this.ruleDataConsumer = ruleDataConsumer;

		String lineId = layer.getId();

		LineData.Builder ld = LineData.builder();
		ld.id(lineId);

		Object lineColor = layer.getPaint().getLineColor();

		if (lineColor instanceof String lineColorStr) {

			lineColorStr = ColorFormatter.checkAndFormatColorString(lineColorStr);
			ld.strokeColor(lineColorStr);

		}else if(lineColor instanceof Map lineColorMap) {

			Object stopArr = lineColorMap.get("stops");

			if(stopArr instanceof List stopArrList) {

				for (Object list : stopArrList) {

					List<?> zoomList = List.class.cast(list);

					Double zoomVal = Double.class.cast(zoomList.get(0));
					String colorStr = String.class.cast(zoomList.get(1));

					lineColors.put(zoomVal, colorStr);
				}
			}
		}

		Object lineWidth = layer.getPaint().getLineWidth();

		if (lineWidth instanceof String lineWidthStr && StringChecks.isNotNullNorEmpty(lineWidthStr)) {

			ld.width(lineWidthStr);

		}else if(lineWidth instanceof Map widthMap){

			Object stopArr = widthMap.get("stops");

			if(stopArr instanceof List stopArrList) {

				for (Object list : stopArrList) {

					List<?> zoomList = List.class.cast(list);

					Double zoomVal = Double.class.cast(zoomList.get(0));
					Double widthVal = Double.class.cast(zoomList.get(1));

					lineWidths.put(zoomVal, widthVal);
				}
			}
		}

		Object lineOpacity = layer.getPaint().getLineOpacity();

		if (lineOpacity instanceof String lineOpacityStr) {

			ld.opacity(lineOpacityStr);

		}else if(lineOpacity instanceof Double lineOpacityD) {

			ld.opacity(String.valueOf(lineOpacityD));

		} else if (lineOpacity instanceof Map lineOpacityMap) {

			// TODO: read stops and add to potential rules different values for different
			// zoom levels
		}

		float[] dashArray = layer.getPaint().getDashArray();

		if(dashArray != null && dashArray.length > 0) {
			ld.stipple(dashArray);
		}

		LayoutData layout = layer.getLayout();

		if (layout != null) {

			String lineCap = layout.getLineCap();
			ld.lineCap(lineCap);

			String lineJoin = layout.getLineJoin();
			ld.lineJoin(lineJoin);
		}

		String sourceLayer = layer.getSourceLayer();

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();

		LineData lineData = ld.build();

		registry.addToSourceLayer(sourceLayer, lineData);

		ruleExtractor.processRuleDataForLayer(startRuleState, layer, rd -> {

			if(rd instanceof LineRuleData.Builder lrd) {

				lrd.lineColors(lineColors);
				lrd.lineWidths(lineWidths);
				lrd.strokeColor(lineData.getStrokeColor());
			}
		});
	}

	void lineDataBuilt(LineRuleData lineRuleData) {

		if(ruleDataConsumer != null) {
			ruleDataConsumer.accept(lineRuleData);
		}
	}
}
