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


import java.io.ByteArrayOutputStream;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.oscim.theme.ThemeFile;
import org.w3c.dom.Document;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.ConversionException;
import se.autocorrect.styleconverter.internal.layer.LayerDataProcessor;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.util.ColorFormatter;

public class XmlGenerator {

	private ConversionContext context;
	private Transformer transformer;
	private DocumentBuilder docBuilder;

	public XmlGenerator(ConversionContext context) {
		this.context = context;
	}

	public ThemeFile generateXml() throws ConversionException {

		Optional<JsonStyleLayerData> opBackGround = context.getLayerById("background");
		JsonStyleLayerData background = opBackGround
				.orElseThrow(() -> new IllegalArgumentException("No background provided"));

		String backgroundColor = background.getPaint().getBackgroundColor();
		String backgroundColorFormatted = ColorFormatter.checkAndFormatColorString(backgroundColor);

		ThemeFile theme = null;

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newDefaultInstance();

			docBuilder = docFactory.newDocumentBuilder();
			Document document = docBuilder.newDocument();
			context.update(document);

			LayerDataProcessor processor = new LayerDataProcessor(context);
			processor.processVisibleLayers(sectionsGenerator -> {

				// Create the various sections
				sectionsGenerator.prepareXmlHeaders(backgroundColorFormatted);
				sectionsGenerator.createStyleSections();
				sectionsGenerator.createRuleSection();
			});

			processor.reset();

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource source = new DOMSource(document);

			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

				StreamResult result = new StreamResult(baos);

				transformer.transform(source, result);

				String xmlStr = new String(baos.toByteArray());
				theme = new ThemeFileImpl(xmlStr);
			}

		} catch (Exception e) {
			throw new ConversionException(e);
		}

		return theme;
	}

	public void dispose() {

		if (transformer != null) {
			transformer.reset();
		}

		if (docBuilder != null) {
			docBuilder.reset();
		}

		this.context = null;
	}
}
