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
import se.autocorrect.styleconverter.internal.data.rule.FillRuleData;
import se.autocorrect.styleconverter.internal.data.rule.FillRuleData.Builder;
import se.autocorrect.styleconverter.internal.data.rule.RuleState;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.FilterData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.PaintData;
import se.autocorrect.styleconverter.util.ColorFormatter;

public class FillRuleStartState implements RuleState<FillRuleData.Builder> {

	private FillProcessor fillProcessor;

	FillRuleStartState(FillProcessor fillProcessor) {
		this.fillProcessor = fillProcessor;
	}

	@Override
	public void extractRuleData(JsonStyleLayerData layer, Consumer<FillRuleData.Builder> processingRuleCallback) {

		FilterData filterData = layer.getFilterData();

		if (filterData != null) {

			processFilterData(layer, processingRuleCallback, filterData);

		} else {

			processPaintDataForFill(layer, processingRuleCallback);
		}
	}

	private void processFilterData(JsonStyleLayerData layer, Consumer<FillRuleData.Builder> processingRuleCallback,
			FilterData filterData) {


		// TODO: read filter data and create structure to produce 'm' element(s)
		// .. also check ref to style elements ...
		// .... and generate for each source layer .. and some hard coded data for this.

		CollectedDataRegistry registry =  CollectedDataRegistry.getInstance();

		FillRuleData.Builder rd = FillRuleData.builder();
		String sourceLayer = layer.getSourceLayer();

		rd.id(layer.getId())
		.sourceLayer(sourceLayer)
		.layerType(layer.getType())
		.layer(layer);

		//		Optional<LayerClasses> layerClasses = LayerClasses.getLayerClassesForLayer(sourceLayer);
		//
		//		if (layerClasses.isPresent()) {
		//
		//			LayerClasses classes = layerClasses.get();
		//
		//			Optional<LayerProcessor<? extends RuleData>> processor = classes.getLayerSpecificProcessor();
		//
		//			processor.ifPresent(p -> finalRd.processor(p));
		//		}

		String filter = filterData.getFilter();
		List<Object> nodes = filterData.getNodes();

		RuleState filterState = null;

		// TODO: cover more cases

		if (filter.equals("==") && nodes.get(0).equals("class")) {

			filterState = new SimpleClassFilterState(rd, nodes);

		} else if (filter.equals("==") && nodes.get(0).equals("subclass")) {

			filterState = new SimpleSubClassFilterState(rd, nodes);

		} else if (filter.equals("all")) {

			filterState = new AllFilterState(rd, nodes);

		} else if (filter.equals("in")) {

			filterState = new InFilterState(rd, nodes);

		}else if (filter.equals("!in")) {

			//filterState = new NotInFilterState(rd, nodes, layerClasses.orElse(null));
		}

		// TODO: cover more cases so we can remove this check
		if(filterState != null) {
			filterState.extractRuleData(layer, processingRuleCallback);
		}

		if(processingRuleCallback != null) {
			processingRuleCallback.accept(rd);
		}

		//		List<ElementData> elementData = registry.getSourceLayers().get(sourceLayer);

		FillRuleData fillRuleData = rd.build();

		fillProcessor.fillRuleDataBuilt(fillRuleData);

		registry.addToSourceLayer(sourceLayer, fillRuleData);

		// TODO: check if joined classes same for two entries...?. or during generation ...
	}

	private void processPaintDataForFill(JsonStyleLayerData layer, Consumer<Builder> processingRuleCallback) {

		FillRuleData.Builder builder = FillRuleData.builder();

		String sourceLayer = layer.getSourceLayer();

		builder.id(layer.getId())
		.sourceLayer(sourceLayer)
		.layer(layer);

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();

		PaintData paint = layer.getPaint();

		Object fillColor = paint.getFillColor();

		if(fillColor instanceof String fillColorStr) {

			builder.fill(fillColorStr);

		}else if(fillColor instanceof Map fillColorMap ) {

			Map<Double, String> fillColors = new HashMap<>();

			Object stopArr = fillColorMap.get("stops");

			if (stopArr instanceof List stopArrList) {

				for (Object list : stopArrList) {

					List<?> zoomList = List.class.cast(list);

					Double zoomVal = Double.class.cast(zoomList.get(0));
					String colorVal = String.class.cast(zoomList.get(1));

					colorVal = ColorFormatter.checkAndFormatColorString(colorVal);
					fillColors.put(zoomVal, colorVal);
				}

				builder.fillColors(fillColors);
			}
		}

		Object fillOutLineColor = paint.getFillOutLineColor();

		if(fillOutLineColor instanceof String fillOutLineColorStr) {

			builder.fillOutLineColor(fillOutLineColorStr);

		}else if(fillColor instanceof Map fillOutLineColorMap ) {

			// TODO: check if we will ever get to this branch?
		}

		registry.addToSourceLayer(sourceLayer, builder.build());
	}
}
