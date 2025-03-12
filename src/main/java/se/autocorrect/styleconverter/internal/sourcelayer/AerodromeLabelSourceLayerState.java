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
import static se.autocorrect.styleconverter.internal.layer.SourceLayers.AERODROME_LABEL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.CollectedDataRegistry;
import se.autocorrect.styleconverter.internal.data.ElementData;
import se.autocorrect.styleconverter.internal.data.ElementDataVisitor;
import se.autocorrect.styleconverter.internal.data.ElementDataVisitorAdapter;
import se.autocorrect.styleconverter.internal.data.TextData;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData;
import se.autocorrect.styleconverter.internal.layer.LayerStateBase;
import se.autocorrect.styleconverter.internal.layer.RuleGeneratorContext;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;

public class AerodromeLabelSourceLayerState extends LayerStateBase {

	public AerodromeLabelSourceLayerState(ConversionContext context) {
		super(context);
	}

	@Override
	public void processLayer(JsonStyleLayerData layer) {

		String type = layer.getType();

		if(type != null) {

			if(type.equals(SYMBOL.getType())) {

				SymbolProcessor symbolProcessor = new SymbolProcessor(this);
				symbolProcessor.processSymbolLayer(layer);

				registry.addRuleConsumerForSourceLayer(AERODROME_LABEL, getRuleSpecificConsumer());

			}else{

				logUnexpectedLayerType(layer, type);
			}

		}else {

			// TODO: look for "ref" to other layer .?..
		}
	}

	public Collection<String> getClassesForLayer() {
		return Arrays.asList(classesStr.split(", \\r\\n"));
	}

	@Override
	public Consumer<RuleGeneratorContext> getRuleSpecificConsumer() {

		return ruleGeneratorContext -> {

			Element ruleParentElement = ruleGeneratorContext.getRuleParentElement();

			if(ruleParentElement != null) {

				Document document = ruleGeneratorContext.getDocument();

				CollectedDataRegistry registry = CollectedDataRegistry.getInstance();
				List<ElementData> dataForSourceLayer = registry.getElementDataForSourceLayer("aerodrome_label");
				ElementDataVisitor visitor = new AeroDromeLabelVisitor(document, ruleParentElement);

				dataForSourceLayer.forEach(elementData -> elementData.accept(visitor));

				visitor.done();
			}
		};
	}

	static class AeroDromeLabelVisitor extends ElementDataVisitorAdapter {

		private final Document document;
		private final Element ruleParent;
		private final List<Element> elements = new ArrayList<>();

		private Element zoomElement;

		AeroDromeLabelVisitor(Document document, Element ruleParent) {
			this.document = document;
			this.ruleParent = ruleParent;
		}

		@Override
		public void visit(TextData textData) {

			Element captionElement = document.createElement("caption");

			captionElement.setAttribute("id", textData.getId());
			captionElement.setAttribute("dy", textData.getDy());
			captionElement.setAttribute("font", textData.getFonts());
			captionElement.setAttribute("fill", textData.getTextColor());
			captionElement.setAttribute("size", textData.getTextSize());
			captionElement.setAttribute("k", textData.getTextField());

			elements.add(captionElement);
		}

		@Override
		public void visit(SymbolRuleData symbolData) {

			String id = symbolData.getId();
			String zoomMin = symbolData.getZoomMin();

			if(zoomMin != null) {

				zoomElement = document.createElement("m");
				zoomElement.setAttribute("zoom-min", zoomMin);
			}

			Element useElement = document.createElement("symbol");
			useElement.setAttribute("use", id);

			elements.add(useElement);
		}

		@Override
		public void done() {

			if(zoomElement != null) {

				elements.forEach(zoomElement::appendChild);
				ruleParent.appendChild(zoomElement);

			}else {

				elements.forEach(ruleParent::appendChild);
			}
		}
	}

	private static String classesStr =
			"international, \r\n"
					+ "public, \r\n"
					+ "regional, \r\n"
					+ "military, \r\n"
					+ "private, \r\n"
					+ "other";
}
