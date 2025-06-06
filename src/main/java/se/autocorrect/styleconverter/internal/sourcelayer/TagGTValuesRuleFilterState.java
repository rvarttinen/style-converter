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
package se.autocorrect.styleconverter.internal.sourcelayer;


import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import se.autocorrect.styleconverter.internal.data.TagWithValues;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleData.Builder;
import se.autocorrect.styleconverter.internal.data.rule.RuleState;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;
import se.autocorrect.styleconverter.util.Try;

class TagGTValuesRuleFilterState implements RuleState<RuleData.Builder> {

	private Builder rd;
	private List<Object> nodes;

	TagGTValuesRuleFilterState(RuleData.Builder rd, List<Object> nodes) {

		this.rd = rd;
		this.nodes = nodes;
	}

	@Override
	public void extractRuleData(JsonStyleLayerData layer, Consumer<Builder> processingRuleCallback) {

		String tagName = null;
		String [] values = new String [nodes.size() - 1];

		for (int i = 0; i < nodes.size(); i++) {

			String value = String.class.cast(nodes.get(i));

			if(i == 0) {
				// Tag to ref., e.g. "capital"
				tagName = value;

			}else {
				// All potential values of this tag
				values[i - 1] = value;
			}
		}

		if(values.length == 1) {

			final String [] finalVals = values;

			Try<Integer> val = Try.of(() -> Integer.valueOf(finalVals[0]));

			if(val.isSuccess()) {

				values = IntStream.rangeClosed(val.get(), 6).boxed().map(i -> String.valueOf(i.intValue())).toArray(String[]::new);
			}
		}

		rd.tagWithVales(new TagWithValues(tagName, true, values));
	}
}
