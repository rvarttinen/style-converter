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
package se.autocorrect.styleconverter.internal.xml;


import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.CollectedDataRegistry;
import se.autocorrect.styleconverter.internal.data.AreaData;
import se.autocorrect.styleconverter.internal.data.LineData;
import se.autocorrect.styleconverter.internal.data.SymbolData;
import se.autocorrect.styleconverter.internal.data.TextData;

public class StyleSectionsGenerator {

	private Document document;
	private Element rootElement;

	public StyleSectionsGenerator(ConversionContext context) {
		this.document = context.getDocument();
		this.rootElement = context.getRootElement();
	}

	public void generateTextStylesSection() {

		rootElement.appendChild(document.createTextNode(" "));
		rootElement.appendChild(document.createTextNode(" "));
		rootElement.appendChild(document.createComment("###### TEXT styles ######"));
		rootElement.appendChild(document.createTextNode(" "));

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();

		Map<String, List<TextData>> textDatas = registry.getTextData();

		textDatas.entrySet().forEach(entry -> {

			List<TextData> textData = entry.getValue();

			textData.forEach(td -> {

				String textId = td.getId();
				Element element = td.generate(document);

				createElementBlock(document, rootElement, textId, element);
			});

		});
	}

	public void generateAreaStylesSection() {

		rootElement.appendChild(document.createTextNode(" "));
		rootElement.appendChild(document.createTextNode(" "));
		rootElement.appendChild(document.createComment("###### AREA styles ######"));
		rootElement.appendChild(document.createTextNode(" "));

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();

		Map<String, List<AreaData>> areaDatas = registry.getAreaData();

		areaDatas.entrySet().forEach(entry -> {

			List<AreaData> areaData = entry.getValue();

			areaData.forEach(ad -> {

				String areaId = ad.getId();
				Element element = ad.generate(document);

				createElementBlock(document, rootElement, areaId, element);
			});
		});
	}

	public void generateLineStylesSection() {

		rootElement.appendChild(document.createTextNode(" "));
		rootElement.appendChild(document.createTextNode(" "));
		rootElement.appendChild(document.createComment("###### LINE styles ######"));
		rootElement.appendChild(document.createTextNode(" "));

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();

		Map<String, List<LineData>> lineDatas = registry.getLineData();

		lineDatas.entrySet().forEach(entry -> {

			List<LineData> lineData = entry.getValue();

			lineData.forEach(ld -> {

				String lineId = ld.getId();
				Element element = ld.generate(document);

				createElementBlock(document, rootElement, lineId, element);
			});
		});
	}

	public void generateSymbolStylesSection() {

		rootElement.appendChild(document.createTextNode(" "));
		rootElement.appendChild(document.createTextNode(" "));
		rootElement.appendChild(document.createComment("###### SYMBOL styles ######"));
		rootElement.appendChild(document.createTextNode(" "));

		CollectedDataRegistry registry = CollectedDataRegistry.getInstance();

		Map<String, List<SymbolData>> symbolDatas = registry.getSymbolData();

		symbolDatas.entrySet().forEach(entry -> {

			List<SymbolData> data = entry.getValue();

			data.forEach(sd -> {

				String symbolId = sd.getId();
				Element element = sd.generate(document);

				if (element != null) {
					createElementBlock(document, rootElement, symbolId, element);
				}
			});
		});
	}

	private void createElementBlock(Document document, Element rootElement, String id, Element element) {

		rootElement.appendChild(document.createTextNode(" "));
		rootElement.appendChild(document.createComment(id));
		rootElement.appendChild(element);
	}
}
