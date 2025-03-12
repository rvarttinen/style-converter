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


import static se.autocorrect.styleconverter.internal.layer.LayerType.FILL;
import static se.autocorrect.styleconverter.internal.layer.SourceLayers.WATER;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.data.TagWithValues;
import se.autocorrect.styleconverter.internal.data.rule.FillRuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.layer.LayerStateBase;
import se.autocorrect.styleconverter.internal.layer.RuleGeneratorContext;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;

public class WaterSourceLayerState extends LayerStateBase {

	public WaterSourceLayerState(ConversionContext context) {
		super(context);
	}

	@Override
	public void processLayer(JsonStyleLayerData layer) {

		String type = layer.getType();

		if(type.equals(FILL.getType())) {

			FillProcessor fillProcessor = new FillProcessor(this);
			fillProcessor.processFillLayer(layer, c -> {});

			registry.addRuleConsumerForSourceLayer(WATER, getRuleSpecificConsumer());

		}else{

			logUnexpectedLayerType(layer, type);
		}
	}

	@Override
	public Collection<String> getClassesForLayer() {
		return Arrays.asList(classesStr.split(", \\r\\n"));
	}

	@Override
	public ConversionContext getContext() {
		return context;
	}

	private static String classesStr =
			"dock, \r\n"
					+ "river, \r\n"
					+ "pond, \r\n"
					+ "lake, \r\n"
					+ "ocean, \r\n"
					+ "swimming_pool";

	@Override
	public Consumer<RuleGeneratorContext> getRuleSpecificConsumer() {

		return ruleGeneratorContext -> {

			Document document = ruleGeneratorContext.getDocument();
			Element ruleParentElement = ruleGeneratorContext.getRuleParentElement();
			List<RuleData> ruleDatas = ruleGeneratorContext.getRuleDatas();

			Element classRuleParent = document.createElement("m");
			classRuleParent.setAttribute("k", "class");

			ruleDatas.forEach(ruleData -> generateRuleElement(document, ruleParentElement, classRuleParent, FillRuleData.class.cast(ruleData)));
		};
	}

	private void generateRuleElement(Document document, Element ruleParentElement, Element classRuleParent, FillRuleData ruleData) {

		String joinedClasses = ruleData.getJoinedClasses();

		String id = ruleData.getId();
		String symbol = ruleData.getSymbol();
		TagWithValues tagWithValues = ruleData.getTagWithValues();

		if(joinedClasses != null) {

			// TODO: if classe(s) indicated use classRuleParent ...
			// TODO:; append classRuleParent to ruleParent

		}else {

			Element tagValElement = null;

			if(tagWithValues != null) {

				tagValElement = createTagValueElement(document, tagWithValues);
				ruleParentElement.appendChild(tagValElement);

				Element useElement = document.createElement("area");
				useElement.setAttribute("mesh", "true");
				useElement.setAttribute("use", id);

				tagValElement.appendChild(useElement);
			}

			if(symbol != null) {

				Element symbolElement = document.createElement("area");
				symbolElement.setAttribute("id", id);
				symbolElement.setAttribute("src", symbol);

				if(tagValElement != null) {
					tagValElement.appendChild(symbolElement);
				}else {
					ruleParentElement.appendChild(symbolElement);
				}
			}

			//TODO: ref to area element ... + mesh="true"
		}
	}

	private Element createTagValueElement(Document document, TagWithValues tagWithValues) {

		Element tagValElement = document.createElement("m");

		boolean inclusive = tagWithValues.inclusive();
		String tag = tagWithValues.tag();

		tagValElement.setAttribute("k", tag);

		String tagValue = String.join("|", tagWithValues.values());
		tagValue = inclusive ? tagValue : "-|" + tagValue;

		tagValElement.setAttribute("v", tagValue);


		return tagValElement;
	}
}

