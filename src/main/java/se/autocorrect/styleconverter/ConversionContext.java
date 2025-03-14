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
package se.autocorrect.styleconverter;


import java.util.Collection;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.json.JsonStyleLayerData;

/**
 * A context holding relevant data to be used throughout a conversion process.
 */
public interface ConversionContext {

	/**
	 * Retrieve the layers being processes.
	 *
	 * @return the layers
	 */
	Collection<JsonStyleLayerData> getLayers();

	/**
	 * Retrieve the layers in the data whose visibility is set to {@code true}. 
	 *
	 * @return the visible layers
	 */
	Collection<JsonStyleLayerData> getVisibleLayers();

	/**
	 * Retrieve a layer by its id. 
	 *
	 * @param id the id
	 * @return an {@code Optional} holding the layer if present, {@code Optional#empty()} otherwise 
	 */
	Optional<JsonStyleLayerData> getLayerById(String id);

	/**
	 * Set the in-memory {@code Document} instance. This docuemnt will render the
	 * XML output. However, it is also used when creating new elements.
	 *
	 * @param document the document
	 */
	void update(Document document);

	/**
	 * Retrieve the current {@code Document} instance.
	 *
	 * @return the document
	 */
	Document getDocument();

	/**
	 * Retrieve the root {@code Element} of the {@code Document}.
	 *
	 * @return the root element
	 */
	Element getRootElement();
}
