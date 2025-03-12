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
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import se.autocorrect.styleconverter.internal.CollectedDataRegistry;
import se.autocorrect.styleconverter.internal.data.rule.RuleState;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData.Builder;
import se.autocorrect.styleconverter.internal.layer.LayerState;
import se.autocorrect.styleconverter.internal.sprite.cache.SpriteCache;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.FilterData;

class SymbolRuleStartState implements RuleState<SymbolRuleData.Builder> {

	private LayerState layerState;
	private RuleState<Builder> nextState;
	private SymbolProcessor processor;

	public SymbolRuleStartState(SymbolProcessor processor, LayerState layerState) {
		this.processor = processor;
		this.layerState = layerState;
	}

	@Override
	public void extractRuleData(JsonStyleLayerData layer, Consumer<SymbolRuleData.Builder> processingRuleCallback) {

		String id = layer.getId();
		String sourceLayer = layer.getSourceLayer();

		SymbolRuleData.Builder srd = SymbolRuleData.builder();

		srd.id(id)
		.sourceLayer(sourceLayer)
		.layer(layer);

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();

		//		checkAndSetIfLayerProcessorPresent(sourceLayer, srd);

		// We do not have a symbol match -> check and see if we have classes indicated
		// in a filter
		// Check if we have a icon image we can use
		Object iconImage = layer.getLayout().getIconImage();

		if (iconImage instanceof String iconImageStr) {

			// Find sprite first, use default otherwise

			SpriteCache spriteCache = SpriteCache.getInstance();

			Optional<String> sprite = spriteCache.getSpriteFileBySpriteName(iconImageStr);

			if(sprite.isPresent()) {

				srd.symbol(sprite.get());

			}else {

				//				Optional<String> symbolForIconImage = layerState.getDefaultSymbolForClass(iconImageStr);
				//				symbolForIconImage.ifPresentOrElse(symbol -> srd.symbol(symbol), () -> srd.symbol(iconImageStr));
				srd.symbol(iconImageStr); // TODO: As default for indciating - but, should be null actually .. .
			}

		} else if (iconImage instanceof Map iconImageMap) {

			// TODO: fix different images for different zoom levels ..?.
		}

		Double minZoom = layer.getMinZoom();

		if (minZoom > 0) {
			srd.zoomMin(String.valueOf(minZoom.intValue()));
		}

		parseFilterData(layer, srd, processingRuleCallback);

		SymbolRuleData symbolRuleData = srd.build();

		processor.symbolRuleDataBuilt(symbolRuleData);

		registry.addToSourceLayer(sourceLayer, symbolRuleData);
	}

	private void parseFilterData(JsonStyleLayerData layer, SymbolRuleData.Builder srd, Consumer<SymbolRuleData.Builder> processingRuleCallback) {

		FilterData filterData = layer.getFilterData();

		if(filterData != null) {

			String filter = filterData.getFilter();
			List<Object> nodes = filterData.getNodes();

			if(filter != null) {

				switch (filter) {
				case "all":
				case "in":

					if(nodes.size() > 1 && nodes.get(1).equals("class")) {
						nodes = nodes.stream().skip(1).toList();
					}

					nextState = new AllFilterRuleState(srd, nodes);
					break;

				case "==":
				case "has":

					if (nodes.size() > 1 && nodes.get(0).equals("$type")) {

						nextState = new TypeRuleFilterState(srd, null);
						break;
					}

					if(nodes.size() > 1 && nodes.get(1).equals("class")) {
						nodes = nodes.stream().skip(1).toList();
					}

					nextState = new AllFilterRuleState(srd, nodes);
					break;

				case "!in":

					nextState = new NotInFilterState(srd, nodes, layerState);
					break;
				default:
					// TODO: <- fix cases!
				}

				if(nextState != null) {
					nextState.extractRuleData(layer, processingRuleCallback);
				}

			}else {
				// TODO: <- fix case!
			}
		}
	}
}
