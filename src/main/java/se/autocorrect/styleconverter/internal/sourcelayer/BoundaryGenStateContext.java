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


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.internal.data.rule.LineRuleData;
import se.autocorrect.styleconverter.internal.sourcelayer.BoundaryGenState.AdminLevelRangeState;
import se.autocorrect.styleconverter.internal.sourcelayer.BoundaryGenState.AdminLevelState;
import se.autocorrect.styleconverter.internal.sourcelayer.BoundaryGenState.DisputedState;
import se.autocorrect.styleconverter.internal.sourcelayer.BoundaryGenState.MaritimeState;
import se.autocorrect.styleconverter.util.ColorFormatter;

class BoundaryGenStateContext {

	private static BoundaryGenStateContext instance = new BoundaryGenStateContext();

	private final InsertionVisitor insertionVisitor;

	private final Map<String, Set<AdminLevelState>> adminStates;
	private final Map<String, Set<MaritimeState>> maritimeStates;

	private final Map<String, DisputedState> disputedStates;
	private final List<AdminLevelRangeState> rangeStates;

	private Set<String> processedDisputed;

	private BoundaryGenStateContext() {

		this.insertionVisitor = new InsertionVisitor();

		this.adminStates = new LinkedHashMap<>();
		this.maritimeStates = new LinkedHashMap<>();
		this.disputedStates = new LinkedHashMap<>();

		this.processedDisputed = new HashSet<>();
		this.rangeStates = new ArrayList<>();
	}

	static BoundaryGenStateContext getInstance() {
		return instance;
	}

	void addState(BoundaryGenState state) {
		state.accept(insertionVisitor);
	}

	void generateElements(Document document, Element ruleAdminLevelParentElement, Map<String, LineRuleData> ruleDatas,
			Element ruleParentElement) {

		processAdminLevels(document, ruleAdminLevelParentElement, ruleDatas);
		processRangeStates(document, ruleAdminLevelParentElement, ruleDatas);
		processRemainingDisputed(document, ruleDatas, ruleParentElement);
	}

	void reset() {

		this.adminStates.clear();
		this.maritimeStates.clear();
		this.disputedStates.clear();

		this.processedDisputed.clear();
		this.rangeStates.clear();
	}

	private void processAdminLevels(Document document, Element ruleAdminLevelParentElement,
			Map<String, LineRuleData> ruleDatas) {

		adminStates.forEach((adminLevel, adminStates) -> {

			Element adminLevelElement = document.createElement("m");
			adminLevelElement.setAttribute("v", adminLevel);
			ruleAdminLevelParentElement.appendChild(adminLevelElement);

			adminStates.forEach(adminState -> {

				String id = adminState.getId();
				Set<MaritimeState> mStates = maritimeStates.get(id);

				if (mStates != null) {

					mStates.forEach(maritimeState -> {

						Element maritimeElement = maritimeState.generateElement(document);
						adminLevelElement.appendChild(maritimeElement);

						DisputedState disputedState = disputedStates.get(id);
						Element disputedElement = null;

						if (disputedState != null) {
							disputedElement = disputedState.generateElement(document);
							processedDisputed.add(id);
						}

						generateMaritimeWithZoomElements(document, ruleDatas, id, maritimeElement, disputedElement);
					});

				}else {

					generateZoomElementsWithoutMaritiemParent(document, ruleDatas, adminLevelElement, id);
				}
			});
		});
	}

	private void generateZoomElementsWithoutMaritiemParent(Document document, Map<String, LineRuleData> ruleDatas,
			Element adminLevelElement, String id) {

		LineRuleData lineRuleData = ruleDatas.get(id);

		if(lineRuleData != null) {

			Map<Double, Double> lineWidths = lineRuleData.getLineWidths();
			String color = ColorFormatter.checkAndFormatColorString(lineRuleData.getStrokeColor());

			lineWidths.forEach((zoom, width) -> {

				Element zoomElement = document.createElement("m");
				zoomElement.setAttribute("zoom-min", String.valueOf(zoom.intValue()));

				Element lineElement = createLineElement(document, lineRuleData, color, width);
				zoomElement.appendChild(lineElement);
				adminLevelElement.appendChild(zoomElement);
			});
		}
	}

	private void processRemainingDisputed(Document document, Map<String, LineRuleData> ruleDatas,
			Element ruleParentElement) {

		if (processedDisputed.size() < disputedStates.size()) {

			// Process any reamining disputed ...
			Map<String, DisputedState> disputedtoProcess = disputedStates.entrySet().stream()
					.filter(entry -> !processedDisputed.contains(entry.getKey()))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

			disputedtoProcess.forEach((id, disputedState) -> {

				Set<MaritimeState> mStates = maritimeStates.get(id);

				if (mStates != null) {

					if (!mStates.isEmpty()) {

						mStates.forEach(maritimeState -> {

							Element maritimeElement = maritimeState.generateElement(document);
							ruleParentElement.appendChild(maritimeElement);

							Element disputedElement = disputedState.generateElement(document);
							maritimeElement.appendChild(disputedElement);

							generateMaritimeWithZoomElements(document, ruleDatas, id, maritimeElement, disputedElement);
						});

					} else {

						// TODO: create disputed rule element directly under rule base ...
						// TODO: implement when need arises.
//						Activator.getLogger().error(
//								"Unimplmented case encountered: disputed element should be appended directly to rule parent element for: "
//										+ id);
					}
				}
			});
		}
	}

	private void processRangeStates(Document document, Element ruleAdminLevelParentElement,
			Map<String, LineRuleData> ruleDatas) {

		rangeStates.forEach(rangeState -> {

			String id = rangeState.getId();
			Set<MaritimeState> mStates = maritimeStates.get(id);

			if (mStates != null) {

				Element rangeElement = rangeState.generateElement(document);
				ruleAdminLevelParentElement.appendChild(rangeElement);

				mStates.forEach(maritimeState -> {

					Element maritimeElement = maritimeState.generateElement(document);
					rangeElement.appendChild(maritimeElement);

					DisputedState disputedState = disputedStates.get(id);
					Element disputedElement = null;

					if (disputedState != null) {
						disputedElement = disputedState.generateElement(document);
						processedDisputed.add(id);
					}

					generateMaritimeWithZoomElements(document, ruleDatas, id, maritimeElement, disputedElement);
				});
			}
		});
	}

	private void generateMaritimeWithZoomElements(Document document, Map<String, LineRuleData> ruleDatas, String id,
			Element maritimeElement, final Element finalDisputedElement) {

		LineRuleData lineRuleData = ruleDatas.get(id);

		if (lineRuleData != null) {

			Map<Double, Double> lineWidths = lineRuleData.getLineWidths();

			String zoomMin = lineRuleData.getZoomMin();

			if (zoomMin != null) {
				maritimeElement.setAttribute("minzoom", zoomMin);
			}

			String color = ColorFormatter.checkAndFormatColorString(lineRuleData.getStrokeColor());

			lineWidths.forEach((zoom, width) -> {

				Element zoomElement = document.createElement("m");
				zoomElement.setAttribute("zoom-min", String.valueOf(zoom.intValue()));

				Element lineElement = createLineElement(document, lineRuleData, color, width);

				zoomElement.appendChild(lineElement);

				if (finalDisputedElement != null) {

					finalDisputedElement.appendChild(zoomElement);
					maritimeElement.appendChild(finalDisputedElement);

				} else {

					maritimeElement.appendChild(zoomElement);
				}
			});
		}
	}

	private Element createLineElement(Document document, LineRuleData lineRuleData, String color, Double width) {

		Element lineElement = document.createElement("line");

		lineElement.setAttribute("fix", "true");
		lineElement.setAttribute("outline", lineRuleData.getId());
		lineElement.setAttribute("stroke", color);
		lineElement.setAttribute("width", String.valueOf(width));

		String lineCap = lineRuleData.getLayer().getLayout().getLineCap();

		if (lineCap != null) {
			lineElement.setAttribute("cap", lineCap);
		}

		return lineElement;
	}

	private class InsertionVisitor implements BoundaryGenState.Visitor {

		@Override
		public void visit(AdminLevelState state) {

			// Qualify on admin level ....

			String adminLevel = state.getAdminLevel();

			Set<AdminLevelState> states = adminStates.get(adminLevel);

			if (states == null) {

				states = new LinkedHashSet<>();
				states.add(state);

				adminStates.put(adminLevel, states);

			} else {

				states.add(state);
			}
		}

		@Override
		public void visit(MaritimeState state) {

			// Qualify on id ...

			String id = state.getId();

			Set<MaritimeState> states = maritimeStates.get(id);

			if (states == null) {

				states = new LinkedHashSet<>();
				states.add(state);

				maritimeStates.put(id, states);

			} else {

				states.add(state);
			}
		}

		@Override
		public void visit(DisputedState state) {

			String id = state.getId();
			disputedStates.put(id, state);
		}

		@Override
		public void visit(AdminLevelRangeState state) {
			rangeStates.add(state);
		}
	}
}
