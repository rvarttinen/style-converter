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


import java.util.List;
import java.util.function.Consumer;

import se.autocorrect.styleconverter.internal.CollectedDataRegistry;
import se.autocorrect.styleconverter.internal.data.rule.LineRuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleState;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.FilterData;

class LineRuleStartState implements RuleState<LineRuleData.Builder> {

	private LineProcessor lineProcessor;

	LineRuleStartState(LineProcessor lineProcessor) {
		this.lineProcessor = lineProcessor;
	}

	@Override
	public void extractRuleData(JsonStyleLayerData layer, Consumer<LineRuleData.Builder> processingRuleCallback) {

		String id = layer.getId();

		FilterData filterData = layer.getFilterData();

		String type = layer.getType();
		String sourceLayer = layer.getSourceLayer();

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();

		if (filterData != null) {
			processFilterData(layer, processingRuleCallback, id, filterData, type, sourceLayer, registry);
		}
	}

	private void processFilterData(JsonStyleLayerData layer, Consumer<LineRuleData.Builder> processingRuleCallback,
			String id, FilterData filterData, String type, String sourceLayer, CollectedDataRegistry registry) {

		LineRuleData.Builder rd = LineRuleData.builder();

		rd.id(id)
		.sourceLayer(sourceLayer)
		.layerType(type)
		.layer(layer);

		Double minZoom = layer.getMinZoom();

		if (minZoom > 0) {
			rd.zoomMin(String.valueOf(minZoom.intValue()));
		}

		final LineRuleData.Builder finalRd = rd;

		// TODO: replace with visitor when generating ...
		//		Optional<LayerClasses> layerClasses = LayerClasses.getLayerClassesForLayer(sourceLayer);

		//		if (layerClasses.isPresent()) {
		//
		//			LayerClasses classes = layerClasses.get();
		//
		//			// TODO: replace with visitor when generating ...
		//			Optional<LayerProcessor<? extends RuleData>> processor = classes.getLayerSpecificProcessor();
		//
		//			processor.ifPresent(p -> finalRd.processor(p));
		//		}

		String filter = filterData.getFilter();
		List<Object> nodes = filterData.getNodes();

		RuleState filterState = null;

		// TODO: cover more cases (also 'has', '!has', '>=', '<=',  etc.)

		if (filter != null) {

			if (filter.equals("==") && nodes.get(0).equals("class")) {

				filterState = new SimpleClassFilterState(rd, nodes);

			} else if (filter.equals("==") && nodes.get(0).equals("subclass")) {

				filterState = new SimpleSubClassFilterState(rd, nodes);

			} else if (filter.equals("all")) {

				filterState = new AllFilterState(rd, nodes);

			} else if (filter.equals("in")) {

				filterState = new InFilterState(rd, nodes);

			} else if (filter.equals("!in")) {

				// TODO: fix! Get classes from sourceLayerState instead ...
				//				filterState = new NotInFilterState(rd, nodes, layerClasses.orElse(null));

			}else if(filter.equals("==")) {

				filterState = new AllDirectFilterState(rd, nodes);
			}

		} else {

			// TODO: check that we have nodes and parse those ...
			filterState = new AllFilterState(rd, nodes);
		}

		// TODO: cover more cases so we can remove this check
		if(filterState != null) {
			filterState.extractRuleData(layer, processingRuleCallback);
		}

		if(processingRuleCallback != null) {
			processingRuleCallback.accept(rd);
		}

		LineRuleData lineRuleData = rd.build();

		lineProcessor.lineDataBuilt(lineRuleData);

		registry.addToSourceLayer(sourceLayer, lineRuleData);
	}
}
