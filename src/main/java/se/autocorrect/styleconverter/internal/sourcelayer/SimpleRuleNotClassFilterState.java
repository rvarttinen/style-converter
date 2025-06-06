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

import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleData.Builder;
import se.autocorrect.styleconverter.internal.data.rule.RuleState;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;

class SimpleRuleNotClassFilterState implements RuleState<RuleData.Builder>  {

	private Builder srd;
	private List<Object> filterNodes;
	private int skip;
	
	SimpleRuleNotClassFilterState(RuleData.Builder srd, List<Object> filterNodes){
		this(srd, filterNodes, 1);
	}

	SimpleRuleNotClassFilterState(RuleData.Builder srd, List<Object> filterNodes, int skip) {
		this.srd = srd;
		this.filterNodes = filterNodes;
		this.skip = skip;
	}

	@Override
	public void extractRuleData(JsonStyleLayerData layer, Consumer<Builder> processingRuleCallback) {
		
		List<String> classNodes = filterNodes.stream()
				.skip(skip)
				.map(String.class::cast)
				.toList();

		srd.classNodes(classNodes);
		
		String joinedClasses = String.join("|", classNodes);
		srd.matchClasses(joinedClasses);
		srd.negate();
		
		if(processingRuleCallback != null) {
			processingRuleCallback.accept(srd);
		}		
	}
}
