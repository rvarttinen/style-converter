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

import org.apache.commons.lang3.StringUtils;

import se.autocorrect.styleconverter.internal.data.rule.LineRuleData;
import se.autocorrect.styleconverter.internal.data.rule.LineRuleData.Builder;
import se.autocorrect.styleconverter.internal.data.rule.RuleCondition;
import se.autocorrect.styleconverter.internal.data.rule.RuleState;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;

public class AllDirectFilterState implements RuleState {

	private Builder rd;
	private List<Object> nodes;

	public AllDirectFilterState(Builder rd, List<Object> nodes) {
		this.rd = rd;
		this.nodes = nodes;
	}

	@Override
	public void extractRuleData(JsonStyleLayerData layer, Consumer processingRuleCallback) {

		// We came here as the root node is "=="
		// Process the nodes and create conditions based on them ..

		checkIfBoundaryCondition("==", nodes);
	}

	private void checkIfBoundaryCondition(String filter, List<Object> filterNodes) {

		// TODO: this should reside in a a for the layer (boundary) specific class, see refactoring notes
		if (LineRuleData.Builder.class.isInstance(rd)) {

			final ConditionValues conditionValues = new ConditionValues();
			conditionValues.condition = filter;

			filterNodes.forEach(node -> {

				if (String.class.isInstance(node)) {

					String nodeVal = String.class.cast(node);

					if (StringUtils.containsAny(nodeVal, "<>=!")) {

						conditionValues.condition = nodeVal;

					} else if (nodeVal.equals("admin_level")) {

						conditionValues.adminLevel = true;

					} else if (nodeVal.equals("maritime")) {

						conditionValues.maritime = true;

					}else if(nodeVal.equals("disputed")) {

						conditionValues.disputed = true;

					} else {

						conditionValues.value = nodeVal;

						RuleCondition cond = new RuleCondition(conditionValues.condition,
								conditionValues.adminLevel, conditionValues.maritime, conditionValues.disputed,
								conditionValues.value);
						rd.addCondition(cond);
					}
				}
			});
		}
	}

	private static class ConditionValues {

		String condition = null;
		String value = null;
		boolean adminLevel = false;
		boolean maritime = false;
		boolean disputed = false;
	}
}
