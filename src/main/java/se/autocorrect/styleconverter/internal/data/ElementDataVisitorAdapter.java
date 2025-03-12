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


import se.autocorrect.styleconverter.internal.data.rule.FillRuleData;
import se.autocorrect.styleconverter.internal.data.rule.LineRuleData;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData;
import se.autocorrect.styleconverter.internal.data.rule.TextRuleData;

public class ElementDataVisitorAdapter implements ElementDataVisitor {

	@Override
	public void visit(AreaData areaData) {
	}

	@Override
	public void visit(LineData lineData) {
	}

	@Override
	public void visit(SymbolData symbolData) {
	}

	@Override
	public void visit(TextData textData) {
	}

	@Override
	public void visit(RuleData ruleData) {
	}

	@Override
	public void visit(FillRuleData areaData) {
	}

	@Override
	public void visit(LineRuleData lineData) {
	}

	@Override
	public void visit(SymbolRuleData symbolData) {
	}

	@Override
	public void visit(TextRuleData textData) {
	}
}
