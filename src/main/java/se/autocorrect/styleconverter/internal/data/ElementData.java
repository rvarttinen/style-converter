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
package se.autocorrect.styleconverter.internal.data;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A DTO like object holding data extarcted from a layer and used when
 * generating the resulting style data.
 */
public interface ElementData {

	/**
	 * Generate the relevant node(s) based on element data extracted.
	 * 
	 * @param document the current document being generated
	 * @return the element
	 */
	Element generate(Document document);

	/**
	 * Retrieve the id of this element.
	 * 
	 * @return the id
	 */
	String getId();

	/**
	 * Accept a visitor for processing this element.
	 * 
	 * @param visitor the visitor
	 */
	void accept(ElementDataVisitor visitor);

	/**
	 * Builder of {@code ElementData} objects for later generation of styling data.
	 * 
	 * @param <T> the type of the {@code ElementData} to build
	 */
	abstract class Builder<T extends ElementData> {

		protected String textSize;
		protected String name;

		/**
		 * Build the data carrying elemnt based onvalues set.
		 * 
		 * @return the build data element
		 */
		public abstract T build();

		/**
		 * Sets the text size to use. Usually obtained from the 'layout' section in the
		 * source data.
		 * 
		 * @param textSize set the text size
		 * @return this builder
		 */
		public Builder<T> textSize(String textSize) {
			this.textSize = textSize;
			return this;
		}

		/**
		 * Set the 'name' key value of this layer to match it with the corresponding tag
		 * in a database element.
		 * 
		 * @param name the name key value to match
		 * @return this builder
		 */
		public Builder<T> name(String name) {
			this.name = name;
			return this;
		}
	}
}
