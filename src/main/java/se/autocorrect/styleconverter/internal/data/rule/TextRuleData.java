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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.internal.data.ElementDataVisitor;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;

public class TextRuleData extends RuleData {

	private Map<Double, Integer> textSizes;
	private String textColor;
	private String fonts;

	private TextRuleData(String id, JsonStyleLayerData layer, String joinedClasses, String zoomMin, String sourcelayer,
			List<String> classNodes, Map<Double, Integer> textSizes, String textColor, String fonts) {

		super(id, layer, joinedClasses, zoomMin, sourcelayer, classNodes, null, false);

		this.textSizes = textSizes;
		this.textColor = textColor;
		this.fonts = fonts;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public void accept(ElementDataVisitor visitor) {
		visitor.visit(this);
	}

	public static class Builder extends RuleData.Builder {

		private Map<Double, Integer> textSizes;
		private String textColor;
		private String fonts;

		public Builder textSizes(Map<Double, Integer> textSizes) {
			this.textSizes = textSizes;
			return this;
		}

		public Builder textColor(String textColor) {
			this.textColor = textColor;
			return this;
		}

		public Builder fonts(String fonts) {
			this.fonts = fonts;
			return this;
		}

		@Override
		public TextRuleData build() {
			return new TextRuleData(id, layer, joinedClasses, zoomMin, sourceLayer, classNodes, textSizes, textColor,
					fonts);
		}
	}

	@Override
	public Element generate(Document document) {

		Element subClassElement;

		if (!textSizes.isEmpty()) {

			subClassElement = document.createElement("m");

			if (joinedClasses != null) {
				subClassElement.setAttribute("v", joinedClasses);
			} else {
				subClassElement.setAttribute("v", id);
			}

			textSizes.forEach((zoom, size) -> {

				Element zoomLevelElement = document.createElement("m");
				zoomLevelElement.setAttribute("zoom-max", String.valueOf(zoom.intValue()));

				Element captionElement = document.createElement("caption");

				captionElement.setAttribute("size", String.valueOf(size));
				captionElement.setAttribute("k", "name");
				captionElement.setAttribute("priority", "name");
				captionElement.setAttribute("stroke", textColor);

				zoomLevelElement.appendChild(captionElement);
				subClassElement.appendChild(zoomLevelElement);
			});

		} else {
			subClassElement = super.generate(document);
		}

		return subClassElement;
	}
}
