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
package se.autocorrect.styleconverter.internal.data.rule;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.CollectedDataRegistry;
import se.autocorrect.styleconverter.internal.data.ElementData;
import se.autocorrect.styleconverter.internal.layer.RuleGeneratorContext;

public class RuleSectionsGenerator {

	private Document document;
	private Element rootElement;
	private ConversionContext context;

	public RuleSectionsGenerator(ConversionContext context) {
		this.context = context;
		this.document = context.getDocument();
		this.rootElement = context.getRootElement();
	}

	public void generateRuleSectionsForSourceLayers() {

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();

		Map<String, List<ElementData>> sourceLayersElementData = registry.getSourceLayers();

		rootElement.appendChild(document.createTextNode(" "));
		rootElement.appendChild(document.createTextNode(" "));
		rootElement.appendChild(document.createComment("###### ASSIGNMENT ######"));
		rootElement.appendChild(document.createTextNode(" "));

		sourceLayersElementData.entrySet().forEach(entry -> {

			String sourceLayer = entry.getKey();
			List<ElementData> rds = entry.getValue();

			List<RuleData> rulesData = rds.stream().filter(RuleData.class::isInstance).map(RuleData.class::cast)
					.toList();

			Element ruleParentElement = document.createElement("m");
			ruleParentElement.setAttribute("k", "layer");
			ruleParentElement.setAttribute("v", sourceLayer);

			Optional<Consumer<RuleGeneratorContext>> consumerOp = registry.getRuleConsumerForSourceLayer(sourceLayer);

			consumerOp.ifPresent(consumer -> {

				RuleGeneratorContext context = new RuleGeneratorContext() {

					@Override
					public Element getRuleParentElement() {
						return ruleParentElement;
					}

					@Override
					public Document getDocument() {
						return document;
					}

					@Override
					public List<RuleData> getRuleDatas() {
						return rulesData;
					}
				};

				consumer.accept(context);
			});

			rootElement.appendChild(ruleParentElement);
		});
	}
}
