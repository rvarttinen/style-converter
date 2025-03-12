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


import static se.autocorrect.styleconverter.internal.layer.LayerType.LINE;
import static se.autocorrect.styleconverter.internal.layer.SourceLayers.BOUNDARY;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.data.rule.LineRuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleCondition;
import se.autocorrect.styleconverter.internal.layer.LayerStateBase;
import se.autocorrect.styleconverter.internal.layer.RuleGeneratorContext;
import se.autocorrect.styleconverter.internal.sourcelayer.BoundaryGenState.AdminLevelRangeState;
import se.autocorrect.styleconverter.internal.sourcelayer.BoundaryGenState.AdminLevelState;
import se.autocorrect.styleconverter.internal.sourcelayer.BoundaryGenState.DisputedState;
import se.autocorrect.styleconverter.internal.sourcelayer.BoundaryGenState.MaritimeState;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;

public class BoundarySourceLayerState extends LayerStateBase {

	private static Map<String, LineRuleData> ruleDatas = new LinkedHashMap<>();
	AdminLevelRangeState rangeState = null;

	public BoundarySourceLayerState(ConversionContext context) {
		super(context);
	}

	@Override
	public void processLayer(JsonStyleLayerData layer) {

		String type = layer.getType();

		if(type != null) {

			if(type.equals(LINE.getType())) {

				LineProcessor lineProcessor = new LineProcessor(this);
				lineProcessor.processLineLayer(layer, lineRuleData -> {

					String id = lineRuleData.getId();
					ruleDatas.put(id, lineRuleData);

					processConditions(id, lineRuleData);
				});

				registry.addRuleConsumerForSourceLayer(BOUNDARY, getRuleSpecificConsumer());

			}else{

				logUnexpectedLayerType(layer, type);
			}
		}
	}

	@Override
	public ConversionContext getContext() {
		return context;
	}

	@Override
	public Consumer<RuleGeneratorContext> getRuleSpecificConsumer() {

		return ruleGeneratorContext -> {

			BoundaryGenStateContext genContext = BoundaryGenStateContext.getInstance();

			Document document = ruleGeneratorContext.getDocument();

			Element ruleAdminLevelParentElement = document.createElement("m");
			ruleAdminLevelParentElement.setAttribute("k", "admin_level");

			Element ruleParentElement = ruleGeneratorContext.getRuleParentElement();
			ruleParentElement.appendChild(ruleAdminLevelParentElement);

			genContext.generateElements(document, ruleAdminLevelParentElement, ruleDatas, ruleParentElement);
			genContext.reset();
		};
	}

	private void processConditions(String id, LineRuleData lineRuleData) {

		BoundaryGenStateContext genContext = BoundaryGenStateContext.getInstance();

		List<RuleCondition> conditions = lineRuleData.getConditions();

		if (conditions != null) {

			conditions.forEach(cond -> {

				if (cond.isAdminLevel()) {

					if (cond.getCondition().equals("==") || cond.getCondition().equals("in")) {

						genContext.addState(new AdminLevelState(id, cond));

					} else if (cond.getCondition().equals(">=")) {

						processRangeStateLower(id, genContext, cond);

					} else if (cond.getCondition().equals("<=")) {

						processRangeStateUpper(id, genContext, cond);

					} else {

//						logger.error("Unimplmented case encountered: admin_level condition \"" + cond.getCondition() + "\" not handled for: " + id);
					}

				} else if (cond.isMaritime()) {

					genContext.addState(new MaritimeState(id, cond));

				} else if (cond.isDisputed()) {

					genContext.addState(new DisputedState(id, cond));
				}
			});
		}
	}

	private void processRangeStateUpper(String id, BoundaryGenStateContext genContext, RuleCondition cond) {

		if(rangeState == null) {
			rangeState = new AdminLevelRangeState(id);
		}

		rangeState.setUpperCondition(cond);

		checkRangeStateComplete(genContext);
	}

	private void processRangeStateLower(String id, BoundaryGenStateContext genContext, RuleCondition cond) {

		if(rangeState == null) {
			rangeState = new AdminLevelRangeState(id);
		}

		rangeState.setLowerCondition(cond);

		checkRangeStateComplete(genContext);
	}

	private void checkRangeStateComplete(BoundaryGenStateContext genContext) {

		if(rangeState.isComplete()) {
			genContext.addState(rangeState);
			rangeState = null;
		}
	}
}
