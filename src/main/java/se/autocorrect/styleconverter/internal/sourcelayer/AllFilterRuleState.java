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

import se.autocorrect.styleconverter.internal.data.TagWithValues;
import se.autocorrect.styleconverter.internal.data.rule.RuleState;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData.Builder;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.FilterData;

class AllFilterRuleState implements se.autocorrect.styleconverter.internal.data.rule.RuleState<Builder> {

	private Builder srd;
	private List<Object> nodes;

	AllFilterRuleState(Builder srd, List<Object> nodes) {
		this.srd = srd;
		this.nodes = nodes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void extractRuleData(JsonStyleLayerData layer, Consumer<SymbolRuleData.Builder> processingRuleCallback) {

		RuleState<?> nextState = null;

		for (Object node : nodes) {

			if (FilterData.class.isInstance(node)) {

				FilterData fd = FilterData.class.cast(node);

				String filter = fd.getFilter();
				List<Object> filterNodes = fd.getNodes();

				nextState = null;

				if (filter != null) {

					switch (filter) {

					case "==":

						if (filterNodes.get(0).equals("class")) {

							nextState = new SimpleRuleClassFilterState(srd, filterNodes);

						} else if (filterNodes.get(1).equals("class")) {

							nextState = new SimpleRuleClassFilterState(srd, filterNodes, 1);

						} else if (filterNodes.get(0).equals("$type")) {

							nextState = new TypeRuleFilterState(srd, filterNodes);

						}else if(filterNodes.get(0).equals("oneway")) {

							nextState = new OnewayRuleFilterState(srd, filterNodes);

						} else {

							nextState = new TagValuesRuleFilterState(srd, filterNodes, true);
						}

						break;
					case "in":
					case "has":

						if(filterNodes.get(0).equals("class")) {
							nextState = new SimpleRuleClassFilterState(srd, filterNodes);
						}

						break;
					case "any":
					case "class":
					case "!=":

						nextState = new TagValuesRuleFilterState(srd, filterNodes, false);
						break;
					case "!in":

						if (filterNodes.get(0).equals("class")) {
							nextState = new SimpleRuleNotClassFilterState(srd, filterNodes);
						}
						break;
					case ">=": // E.g Rank range
						nextState = new TagGTValuesRuleFilterState(srd, filterNodes);
						break;
					case "<=": // E.g Rank range
						nextState = new TagLTValuesRuleFilterState(srd, filterNodes);
						break;
					case "all":
					case "!has":
					default:
						// TODO: implement missing cases.
					}

					if(nextState != null) {
						nextState.extractRuleData(layer, Consumer.class.cast(processingRuleCallback));
					}

				} else {

					// Next level of nodes
					out: for (Object nnode : filterNodes) {

						if (String.class.isInstance(nnode)) {

							String nfilter = String.class.cast(nnode);

							if (!nfilter.equals("any")) {
								break out;
							}

							TagWithValues twv = new TagWithValues(nfilter, true, "1"); // 1 - present ...
							srd.tagWithVales(twv);

						} else {

							FilterData nfilterData = FilterData.class.cast(nnode);

							String subFilter = nfilterData.getFilter();

							if (subFilter.equals("==")) {

								List<Object> subFilterNodes = nfilterData.getNodes();

								int size = subFilterNodes.size();

								String[] values = subFilterNodes.subList(1, size).stream()
										.map(String.class::cast)
										.toArray(String[]::new);

								TagWithValues twv = new TagWithValues((String) subFilterNodes.get(0), true, values);
								srd.tagWithVales(twv);
							}
						}
					}
				}

			} else if(String.class.isInstance(node)){

				String nodeStr = String.class.cast(node);

				if(nodeStr.equals("class")) {

					// "Rest" of the nodes are class indications ...
					nextState = new SimpleRuleClassFilterState(srd, nodes);
					nextState.extractRuleData(layer, Consumer.class.cast(processingRuleCallback));
				}
			}
		}
	}
}
