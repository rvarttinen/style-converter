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
package se.autocorrect.styleconverter.internal;


import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.json.JsonStyle;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.json.JsonStyleLayerData.LayoutData;

class DefaultJsonConversionContext implements ConversionContext {

	private JsonStyle jsonStyle;

	private Document document;
	private String name;

	DefaultJsonConversionContext(JsonStyle jsonStyle, String name) {
		this.jsonStyle = jsonStyle;
		this.name = name;
	}

	@Override
	public Collection<JsonStyleLayerData> getLayers() {

		return Arrays.stream(jsonStyle.getLayers()).toList();
	}

	@Override
	public Collection<JsonStyleLayerData> getVisibleLayers() {

		return Arrays.stream(jsonStyle.getLayers()).filter(this::isVisible).toList();
	}

	@Override
	public Optional<JsonStyleLayerData> getLayerById(String id) {

		return Arrays.stream(jsonStyle.getLayers()).filter(l -> l.getId().equals(id)).findFirst();
	}

	@Override
	public void update(Document document) {
		this.document = document;
	}

	@Override
	public Document getDocument() {
		return document;
	}

	@Override
	public Element getRootElement() {
		return (Element) document.getFirstChild();
	}

	public String getName() {
		return name;
	}

	private boolean isVisible(JsonStyleLayerData layer) {

		String visibility = "none";
		LayoutData layout = layer.getLayout();

		if (layout != null) {
			visibility = layout.getVisibility();
		}

		return layout != null && visibility != null ? visibility.equals("visible") : true;
	}
}
