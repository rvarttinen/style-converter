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
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import se.autocorrect.styleconverter.internal.data.TagWithValues;
import se.autocorrect.styleconverter.internal.data.rule.FillRuleData;
import se.autocorrect.styleconverter.internal.data.rule.LineRuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleCondition;
import se.autocorrect.styleconverter.internal.data.rule.RuleData.Builder;
import se.autocorrect.styleconverter.internal.data.rule.RuleState;
import se.autocorrect.styleconverter.internal.sprite.cache.SpriteCache;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.FilterData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.PaintData;

public class AllFilterState implements RuleState {

	private Builder rd;
	private List<Object> nodes;

	public AllFilterState(Builder rd, List<Object> nodes) {
		this.rd = rd;
		this.nodes = nodes;
	}

	@Override
	public void extractRuleData(JsonStyleLayerData layer, Consumer processingRuleCallback) {

		if (nodes.isEmpty()) {
			// We have a rule that says "all" but no nodes - > check any fill or other symbol
			checkFillSymbol(layer);

		} else {

			for (Object object : nodes) {

				// TODO: for boundary we now get a filter and need to process that se
				// rows 81 -- 118
				// TODO: move that portion .?..

				// TODO: can we use the visitor pattern here instead ..?.

				FilterData fd = FilterData.class.cast(object);

				String filter = fd.getFilter();
				List<Object> filterNodes = fd.getNodes();

				RuleState<?> filterState = null;

				if (filter != null) {

					if ((filter.equals("==") || filter.equals("in")) && filterNodes.get(0).equals("class")) {

						filterState = new SimpleClassFilterState(rd, filterNodes);

					} else if (filter.equals("all")) {

						// Next level ...
						// TODO: for now we only allow this and next level ..
						// TODO: future enhancement: allow arbitrary number of levels .?.
						for (Object node : filterNodes) {

							FilterData filterData = FilterData.class.cast(node);

							if (filterData.getFilter() != null) {

								if (filterData.getFilter().equals("==")) {

									Object firstElement = filterData.getNodes().get(0);

									if (firstElement.equals("class")) {

										filterState = new SimpleClassFilterState(rd, filterData.getNodes());

									} else if (firstElement.equals("brunnel")) {

										List<String> list = filterData.getNodes().stream().skip(1)
												.map(String.class::cast).toList();
										rd.tagWithVales(
												new TagWithValues("brunnel", true, list.toArray(String[]::new)));
									}
								}

							} else {
								// TODO: check how to handle when filter is not defined , but we have nodes...
								// System.out.println("\t++ Layer with null filter in 2:nd level, but has nodes:
								// " + layer.getId());
							}
						}

					} else if (filter.equals("!=") || filter.equals("!in")) {

						if (!filterNodes.get(0).equals("$type") && !filterNodes.get(0).equals("ramp")) {

							extractTagValues(filterNodes, false, rd);
							checkIfBoundaryCondition(filter, filterNodes);
						}

					} else if (filter.equals("==")) {

						// TODO: special for now, near future: allow multiple tags/values
						if (!filterNodes.get(0).equals("$type") && !filterNodes.get(0).equals("ramp")) {

							extractTagValues(filterNodes, true, rd);
							checkIfBoundaryCondition(filter, filterNodes);

							// TODO: special for 'boundary' for now ...
						}else if(filterNodes.get(0).equals("maritime") || filterNodes.get(0).equals("disputed")) {

							// Add conditins if pos
							checkIfBoundaryCondition(filter, filterNodes);
						}

					} else {

						checkIfBoundaryCondition(filter, filterNodes);
					}

					if (filterState != null) { // TODO: Remain until we covered all variations (a few more)
						filterState.extractRuleData(layer, null);
					}

				} else {

					// TODO: move this part to a substate of its own?
					checkIfBoundaryCondition(filter, filterNodes);
				}
			}
		}
	}

	private void checkFillSymbol(JsonStyleLayerData layer) {

		PaintData paintData = layer.getPaint();

		if(paintData != null) {

			String fillPattern = paintData.getFillPattern();

			if(fillPattern != null) {

				// TODO: if no sprite - see if there is a default symbol in framework

				SpriteCache cache = SpriteCache.getInstance();

				Optional<String> spriteOp = cache.getSpriteFileBySpriteName(fillPattern);

				spriteOp.ifPresent(sprite -> {

					// FillRuleData::Builder
					if(FillRuleData.Builder.class.isInstance(rd)) {

						FillRuleData.Builder fillrd = FillRuleData.Builder.class.cast(rd);

						fillrd.symbol(sprite);
						fillrd.refDirectly(true);
					}
				});
			}
		}
	}

	private void extractTagValues(List<Object> nodes, boolean inclusive, Builder rdBuilder) {

		String tagName = null;
		String [] values = new String [nodes.size() - 1];

		for (int i = 0; i < nodes.size(); i++) {

			String value = String.class.cast(nodes.get(i));

			if(i == 0) {
				// Tag to ref., e.g. "capital"
				tagName = value;

			}else {
				// All potential values of this tag
				values[i - 1] = value;
			}
		}

		rdBuilder.tagWithVales(new TagWithValues(tagName, inclusive, values));
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
						((LineRuleData.Builder) rd).addCondition(cond);
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
