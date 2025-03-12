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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import se.autocorrect.styleconverter.internal.data.ElementDataVisitor;
import se.autocorrect.styleconverter.internal.data.TagWithValues;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.util.ColorFormatter;

public class SymbolRuleData extends RuleData {

	private String symbolStr;
	private String whenMatched;
	private List<String> notinClassNodes;
	private boolean refTextStyles;
	private boolean noClassParent;
	private boolean oneway;

	private SymbolRuleData(String id, String layerType, String joinedClasses, String zoomMin, String sourcelayer,
			List<String> classNodes, JsonStyleLayerData layer, String symbolStr, String whenMatched,
			List<String> notinClassNodes, boolean refTextStyles, boolean noClassParent, TagWithValues tagWithValues,
			boolean filterNegate) {

		super(id, layer, joinedClasses, zoomMin, sourcelayer, classNodes, tagWithValues, filterNegate);

		this.layer = layer;
		this.symbolStr = symbolStr;
		this.whenMatched = whenMatched;
		this.notinClassNodes = notinClassNodes;
		this.refTextStyles = refTextStyles;
		this.noClassParent = noClassParent;
	}

	public static Builder builder() {
		return new Builder();
	}

	public Builder toBuilder() {

		return new Builder(id, layerType, joinedClasses, zoomMin, sourcelayer, classNodes, layer, symbolStr,
				whenMatched, refTextStyles, notinClassNodes, noClassParent, oneway, tagWithValues);
	}

	public boolean isRefTextStyles() {
		return refTextStyles;
	}

	public String getSymbolStr() {
		return symbolStr;
	}

	public boolean isNoClassParent() {
		return noClassParent;
	}

	private void setOneway(boolean oneway) {
		this.oneway = oneway;
	}

	public boolean isOneway() {
		return oneway;
	}

	@Override
	public void accept(ElementDataVisitor visitor) {
		visitor.visit(this);
	}

	public static class Builder extends RuleData.Builder {

		Builder(String id, String layerType, String joinedClasses, String zoomMin, String sourcelayer,
				List<String> classNodes, JsonStyleLayerData layer, String symbolStr, String whenMatched,
				boolean refTextStyles, List<String> notinClassNodes, boolean noClassParent, boolean oneway,
				TagWithValues tagWithValues) {

			super();

			// Super type
			this.id = id;
			this.layerType = layerType;
			this.joinedClasses = joinedClasses;
			this.zoomMin = zoomMin;
			this.classNodes = classNodes;
			this.tagWithValues = tagWithValues;

			// This type
			this.layer = layer;
			this.symbolStr = symbolStr;
			this.whenMatched = whenMatched;
			this.refTextStyles = refTextStyles;
			this.notinClassNodes = notinClassNodes;
			this.noClassParent = noClassParent;
			this.oneway = oneway;
		}

		private String symbolStr;
		private String whenMatched;
		private boolean refTextStyles;
		private List<String> notinClassNodes;
		private boolean noClassParent;
		private boolean oneway;

		private Builder() {
			super();
		}

		public Builder symbol(String symbolStr) {
			this.symbolStr = symbolStr;
			return this;
		}

		public Builder referenceTextStyles(boolean refTextStyles) {
			this.refTextStyles = refTextStyles;
			return this;
		}

		public Builder whenMatched(String whenMatched) {
			this.whenMatched = whenMatched;
			return this;
		}

		public Builder notInclassNodes(List<String> notinClassNodes) {
			this.notinClassNodes = notinClassNodes;
			return this;
		}

		public Builder oneway(boolean oneway) {
			this.oneway = oneway;
			return this;
		}

		@Override
		public SymbolRuleData build() {

			SymbolRuleData symbolRuleData = new SymbolRuleData(id, layerType, joinedClasses, zoomMin, sourceLayer,
					classNodes, layer, symbolStr, whenMatched, notinClassNodes, refTextStyles, noClassParent,
					tagWithValues, filterNegate);

			symbolRuleData.setOneway(oneway);

			return symbolRuleData;
		}
	}

	@Override
	public Element generate(Document document) {

		Element subClassElement = null;

		if (classNodes != null && classNodes.size() > 0) {

			for (String classStr : classNodes) {

				subClassElement = document.createElement("m");

				if (filterNegate) {

					subClassElement.setAttribute("k", "class");
					subClassElement.setAttribute("v", "-|" + classStr);

					final Element finalSubClassElement = subClassElement;

					List<Element> elements = refTextStyles ? generateUseElements(document)
							: generateCaptionElements(document);
					elements.forEach(element -> finalSubClassElement.appendChild(element));

				} else {

					subClassElement.setAttribute("v", classStr);

					Element tagValElement = null; // appendTagValues(document, subClassElement); // TODO: Doesn't work
													// in rule engine

					List<Element> elements = refTextStyles ? generateUseElements(document)
							: generateCaptionElements(document);

					for (Element element : elements) {

						if (tagValElement != null) {
							tagValElement.appendChild(element);
						} else {
							subClassElement.appendChild(element);
						}
					}
				}

				ruleClassParentElement.appendChild(subClassElement);
			}

		} else if (refTextStyles) {

			// TODO: if there are several references to other text-styles
			// TODO: map the classes to relevant text-styles ...

			String layerId = layer.getId();

			Object textColor = layer.getPaint().getTextColor();
			Object textSize = layer.getLayout().getTextSize();

			if (textSize instanceof Map textSizeMap) {

				Object stopArr = textSizeMap.get("stops");

				if (stopArr instanceof List stopArrList) {

					for (Object list : stopArrList) {

						List<?> zoomList = List.class.cast(list);

						Double zoomVal = Double.class.cast(zoomList.get(0));
						Double sizeVal = Double.class.cast(zoomList.get(1));

						Element zoomLevelElement = document.createElement("m");
						zoomLevelElement.setAttribute("zoom-max", String.valueOf(zoomVal.intValue()));

						Element textElement = document.createElement("text");

						textElement.setAttribute("use", id);
						textElement.setAttribute("size", String.valueOf(sizeVal.intValue()));
						textElement.setAttribute("priority", "1");

						if (textColor instanceof String textColorStr) {

							textColorStr = ColorFormatter.checkAndFormatColorString(textColorStr);
							textElement.setAttribute("stroke", textColorStr);
						}

						zoomLevelElement.appendChild(textElement);
						ruleClassParentElement.appendChild(zoomLevelElement);
					}
				}

			} else {

				Element useElement = document.createElement("text");
				useElement.setAttribute("use", layerId);

				subClassElement = document.createElement("m");

				if (joinedClasses != null && joinedClasses.length() > 0) {

					subClassElement.setAttribute("v", joinedClasses);

				} else {

					// TODO: "move" use element "up" in the hierarchy?
					// TODO: ... or, assume, id is the same as db class .?..
					// subClassElement.setAttribute("v", "");
				}

				subClassElement.appendChild(useElement);
			}

		} else {

			// Only one ref and no filter data ...
			String layerId = layer.getId();

			Node parentNode = ruleClassParentElement.getParentNode();

			if (parentNode != null) {

				Element useElement = document.createElement("text");
				useElement.setAttribute("use", layerId);

				parentNode.removeChild(ruleClassParentElement);
				parentNode.appendChild(useElement);
			}
		}

		return subClassElement;
	}

	private List<Element> generateUseElements(Document document) {

		List<Element> useElements = new ArrayList<>();

		Object textSize = layer.getLayout().getTextSize();

		if (textSize instanceof Map textSizeMap) {

			Object stopArr = textSizeMap.get("stops");

			if (stopArr instanceof List stopArrList) {

				for (Object list : stopArrList) {

					List<?> zoomList = List.class.cast(list);

					Double zoomVal = Double.class.cast(zoomList.get(0));
					Double sizeVal = Double.class.cast(zoomList.get(1));

					Element zoomLevelElement = document.createElement("m");
					zoomLevelElement.setAttribute("zoom-min", String.valueOf(zoomVal.intValue()));

					Element textElement = document.createElement("text");

					textElement.setAttribute("use", id);
					textElement.setAttribute("size", String.valueOf(sizeVal.intValue()));

					zoomLevelElement.appendChild(textElement);
					useElements.add(zoomLevelElement);
				}
			}
		}

		return useElements;
	}

	private List<Element> generateCaptionElements(Document document) {

		CaptionElementGenerator gen = new CaptionElementGenerator(document, layer, symbolStr, id);
		List<Element> captionElements = gen.createCaptionElements();

		return captionElements;
	}
}
