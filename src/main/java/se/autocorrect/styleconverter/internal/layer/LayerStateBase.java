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


import java.util.Collection;
import java.util.Collections;

import se.autocorrect.styleconverter.ConversionContext;
import se.autocorrect.styleconverter.internal.CollectedDataRegistry;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;


public abstract class LayerStateBase implements LayerState {

	protected LayerState nextState;
	protected ConversionContext context;
//	protected ILog logger;

	protected static CollectedDataRegistry registry = CollectedDataRegistry.getInstance();

	protected LayerStateBase(ConversionContext context) {

		this.context = context;
//		this.logger = context.getLogger();
	}

	@Override
	public ConversionContext getContext() {
		return context;
	}

	@Override
	public Collection<String> getClassesForLayer() {
		return Collections.emptyList();
	}

	protected void logUnexpectedLayerType(JsonStyleLayerData layer, String type) {
//		logger.warn(String.format("Unexpected layer type encountered: %s, for %s", type, layer.getId()));
	}
}
