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
import static se.autocorrect.styleconverter.internal.layer.SourceLayers.HOUSENUMBER;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.data.rule.CaptionElementGenerator;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.layer.LayerStateBase;
import se.autocorrect.styleconverter.internal.layer.RuleGeneratorContext;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;

public class HouseNumberSourceLayerState extends LayerStateBase {

	public HouseNumberSourceLayerState(ConversionContext context) {
		super(context);
	}

	@Override
	public void processLayer(JsonStyleLayerData layer) {

		String type = layer.getType();

		if(type != null) {

			if(type.equals(SYMBOL.getType())) {

				SymbolProcessor symbolProcessor = new SymbolProcessor(this, true);
				symbolProcessor.processSymbolLayer(layer);

				registry.addRuleConsumerForSourceLayer(HOUSENUMBER, getRuleSpecificConsumer());

			}else{

				logUnexpectedLayerType(layer, type);
			}
		}
	}

	public Collection<String> getClassesForLayer() {
		return Collections.emptyList();
	}

	@Override
	public ConversionContext getContext() {
		return context;
	}

	@Override
	public Consumer<RuleGeneratorContext> getRuleSpecificConsumer() {

		return ruleGeneratorContext -> {

			Document document = ruleGeneratorContext.getDocument();
			Element ruleParentElement = ruleGeneratorContext.getRuleParentElement();
			List<RuleData> ruleDatas = ruleGeneratorContext.getRuleDatas();

			ruleDatas.forEach(ruleData -> {

				String zoomMin = ruleData.getZoomMin();

				if (zoomMin != null) {

					Element zoomLevelElement = document.createElement("m");
					zoomLevelElement.setAttribute("zoom-min", zoomMin);

					ruleParentElement.appendChild(zoomLevelElement);

					CaptionElementGenerator gen = new CaptionElementGenerator(document, ruleData.getLayer(), null);
					List<Element> captionElements = gen.createCaptionElements();

					for (Element captionElement : captionElements) {
						captionElement.removeAttribute("priority");
						zoomLevelElement.appendChild(captionElement);
					}
				}
			});
		};
	}
}
