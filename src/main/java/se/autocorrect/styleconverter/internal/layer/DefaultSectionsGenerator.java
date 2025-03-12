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
package se.autocorrect.styleconverter.internal.layer;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.data.rule.RuleSectionsGenerator;
import se.autocorrect.styleconverter.internal.xml.SectionsGenerator;
import se.autocorrect.styleconverter.internal.xml.StyleSectionsGenerator;

class DefaultSectionsGenerator implements SectionsGenerator {

	private ConversionContext context;

	DefaultSectionsGenerator(ConversionContext context) {
		this.context = context;
	}

	@Override
	public void prepareXmlHeaders(String backgroundColor) {

		Document document = context.getDocument();

		Element rootElement = document.createElement("rendertheme");

		rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.setAttribute("xsi:schemaLocation",
				"http://opensciencemap.org/rendertheme https://raw.githubusercontent.com/mapsforge/vtm/master/resources/rendertheme.xsd");
		rootElement.setAttribute("map-background", backgroundColor);
		rootElement.setAttribute("version", "1");

		document.appendChild(rootElement);
	}

	@Override
	public void createStyleSections() {

		StyleSectionsGenerator gen = new StyleSectionsGenerator(context);

		gen.generateTextStylesSection();
		gen.generateAreaStylesSection();
		gen.generateLineStylesSection();
		gen.generateSymbolStylesSection();
	}

	@Override
	public void createRuleSection() {

		RuleSectionsGenerator gen = new RuleSectionsGenerator(context);
		gen.generateRuleSectionsForSourceLayers();
	}
}
