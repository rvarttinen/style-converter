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
package se.autocorrect.styleconverter.internal.layer;


import java.util.function.Consumer;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.sourcelayer.AerodromeLabelSourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.AerowaySourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.BoundarySourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.BuildingSourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.HouseNumberSourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.LandCoverSourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.LandUseSourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.ParkSourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.PlaceSourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.PoiSourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.TransportationNameSourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.TransportationSourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.WaterNameSourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.WaterSourceLayerState;
import se.autocorrect.styleconverter.internal.sourcelayer.WaterWaySourceLayerState;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;

public class SourceLayerStartState extends LayerStateBase /* implements LayerDataAssembler */ {

	public SourceLayerStartState(ConversionContext context) {
		super(context);
	}

	@Override
	public void processLayer(JsonStyleLayerData layer) {

		String sourceLayer = layer.getSourceLayer();

		if (sourceLayer != null) {

			switch (sourceLayer) {

			case "aerodrome_label":
				nextState = new AerodromeLabelSourceLayerState(context);
				break;
			case "aeroway":
				nextState = new AerowaySourceLayerState(context);
				break;
			case "boundary":
				nextState = new BoundarySourceLayerState(context);
				break;
			case "building":
				nextState = new BuildingSourceLayerState(context);
				break;
			case "housenumber":
				nextState = new HouseNumberSourceLayerState(context);
				break;
			case "landcover":
				nextState = new LandCoverSourceLayerState(context);
				break;
			case "landuse":
				nextState = new LandUseSourceLayerState(context);
				break;
			case "place":
				nextState = new PlaceSourceLayerState(context);
				break;
			case "poi":
				nextState = new PoiSourceLayerState(context);
				break;
			case "transportation":
				nextState = new TransportationSourceLayerState(context);
				break;
			case "transportation_name":
				nextState = new TransportationNameSourceLayerState(context);
				break;
			case "water":
				nextState = new WaterSourceLayerState(context);
				break;
			case "water_name":
				nextState = new WaterNameSourceLayerState(context);
				break;
			case "park":
				nextState = new ParkSourceLayerState(context);
				break;
			case "waterway":
				nextState = new WaterWaySourceLayerState(context);
				break;

			// TODO: this one also needs fixing ...
			case "mountain_peak":
			default:
				break;
			}

			// TODO: remove check once all cases are covered
			if (nextState != null) {
				nextState.processLayer(layer);
			}
		}
	}

	@Override
	public Consumer<RuleGeneratorContext> getRuleSpecificConsumer() {
		return c -> {
		}; // Do Nothing consumer
	}

	// @Override
	// public void assembleLayerData() {
	//
	// // Process all collected data, analyze and merge/expand relavant parts ...
	// CollectedDataRegistry registry = CollectedDataRegistry.getInstance();
	// registry.processReplacements();
	// registry.pruneData();
	// }
}
