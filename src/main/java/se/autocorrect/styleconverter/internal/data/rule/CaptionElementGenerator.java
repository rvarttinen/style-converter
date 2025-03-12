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

import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.LayoutData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.PaintData;
import se.autocorrect.styleconverter.util.ColorFormatter;
import se.autocorrect.styleconverter.util.StringUtils;

public class CaptionElementGenerator {

	private Document document;
	private JsonStyleLayerData layer;
	private String symbolStr;
	private String id;

	public CaptionElementGenerator(Document document, JsonStyleLayerData layer, String symbolStr) {

		this.document = document;
		this.layer = layer;
		this.symbolStr = symbolStr;
	}

	public CaptionElementGenerator(Document document, JsonStyleLayerData layer, String symbolStr, String id) {
		this(document, layer, symbolStr);
		this.id = id;
	}

	public List<Element> createCaptionElements() {

		List<Element> captionElements = new ArrayList<>();

		Object textSize = layer.getLayout().getTextSize();
		Object fillColor = layer.getPaint().getTextColor();

		if (symbolStr != null) {

			Element symbolElement = document.createElement("symbol");

			String useRef = id != null ? id : layer.getId();

			symbolElement.setAttribute("use", useRef);

			captionElements.add(symbolElement);
		}

		if (textSize instanceof Map textSizeMap) {

			// Create one caption element per zoom level
			createMultipleCaptionElementsForTextSizeZoomlevels(captionElements, textSizeMap);

		} else if (textSize instanceof Double textSizeD) {

			// Create single caption element ...
			createSingleCaptionElement(captionElements, textSizeD);
		}

		return captionElements;
	}

	private void createMultipleCaptionElementsForTextSizeZoomlevels(List<Element> elements, Map textSizeMap) {

		Object stopArr = textSizeMap.get("stops");

		if (stopArr instanceof List<?> stopArrayList) {

			List<List<?>> stopArrayListOfList = (List<List<?>>) stopArrayList;

			int idx = 0;
			for (List<?> listOfStop : stopArrayListOfList) {

				Element zoomRuleElement = document.createElement("m");

				if (idx == 0) {

					// First pos zoom max?
					Double zoomMin = (Double) listOfStop.get(0);
					zoomRuleElement.setAttribute("zoom-max", String.valueOf(zoomMin.intValue()));

				} else {

					// Second pos zoom min?
					Double zoomMax = (Double) listOfStop.get(0);
					zoomRuleElement.setAttribute("zoom-min", String.valueOf(zoomMax.intValue()));
				}

				idx++;

				Element captionElement = createCaptionElement();
				zoomRuleElement.appendChild(captionElement);

				Double size = (Double) listOfStop.get(1);
				captionElement.setAttribute("size", String.valueOf(size.intValue()));

				Object fillColor = layer.getPaint().getFillColor();

				if (fillColor instanceof String fillColorStr) {
					// Single string indicating color
					captionElement.setAttribute("fill", fillColorStr);
					captionElement.setAttribute("stroke", fillColorStr);
				}

				elements.add(zoomRuleElement);
			}
		}
	}

	private void createSingleCaptionElement(List<Element> captionElements, Double textSize) {

		Element captionElement = createCaptionElement();
		captionElement.setAttribute("size", String.valueOf(textSize.intValue()));

		captionElements.add(captionElement);
	}

	private Element createCaptionElement() {

		// TODO: when encountering different data for different zoom levels -> caption
		// element for each ...

		Element captionElement = document.createElement("caption");
		LayoutData layout = layer.getLayout();

		setNameKeyAttribute(captionElement, layout);

		captionElement.setAttribute("priority", "1"); // TODO: leave in?

		String[] textFont = layout.getTextFont();
		captionElement.setAttribute("font", String.join(",", textFont));

		Object textAnchor = layout.getTextAnchor();

		if (textAnchor instanceof String textAnchorStr) {

			captionElement.setAttribute("dy", textAnchorStr.equals("top") ? "-20" : "20");

		} else if (textAnchor instanceof Map textAnchorMap) {

			// TODO: fix this: "text-anchor": {"base": 1, "stops": [[0, "left"], [8,
			// "center"]]},
			// TODO: but, we 'only' have top and bottom. there seems to be no way to do
			// left, center nor right
		}

		PaintData paint = layer.getPaint();

		if (paint != null) {

			Object textColor = paint.getTextColor();

			if (textColor instanceof String textColorStr) {

				textColorStr = ColorFormatter.checkAndFormatColorString(textColorStr);
//				captionElement.setAttribute("stroke", textColorStr);
				captionElement.setAttribute("fill", textColorStr);
			}
		}

		String fontStyle = "normal";

		if (textFont.length == 1) {

			String fontStr = textFont[0].toLowerCase();

			if (fontStr.contains("italic")) {
				fontStyle = "italic";
			} else if (fontStr.contains("bold")) {
				fontStyle = "bold";
			} else if (fontStr.contains("bold") && fontStr.contains("italic")) {
				fontStyle = "bold_italic";
			}
		}

		captionElement.setAttribute("font-style", fontStyle); // FontStyle: BOLD, BOLD_ITALIC, ITALIC, NORMAL

		return captionElement;
	}

	private void setNameKeyAttribute(Element captionElement, LayoutData layout) {

		String textField = layout.getTextField();

		if (textField != null) {

			textField = textField.replaceAll("[{}]", "").replace("\n", "").replace("\r", "");

			if (textField.contains(":latin")) {
				textField = textField.replace(":latin", " ");
			}

			if (textField.contains(":nonlatin")) {
				textField = textField.replace(":nonlatin", " ");
			}

			textField = StringUtils.removeDuplicates(textField);
			textField = textField.trim();

			captionElement.setAttribute("k", textField);

		} else {

			captionElement.setAttribute("k", "name");
		}
	}
}
