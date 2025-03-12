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

/**
 * A {@code ElementDataVisitor} is used when processing extarcted data, mainly
 * for procuding {@code Element}s for a resulting XML-Document holding the thme
 * in question.
 */
public interface ElementDataVisitor {

	// Rendering data

	/**
	 * Visit an {@code AreaData} object.
	 * 
	 * @param areaData the data object
	 */
	void visit(AreaData areaData);

	/**
	 * Visit a {@code LineData} object.
	 * 
	 * @param lineData the data object
	 */
	void visit(LineData lineData);

	/**
	 * Visit a {@code SymbolData} object.
	 * 
	 * @param symbolData the data object
	 */
	void visit(SymbolData symbolData);

	/**
	 * Visit a {@code TextData} object.
	 * 
	 * @param textData the data object
	 */
	void visit(TextData textData);

	// Rule data

	/**
	 * Visit a generic {@code RuleData} object.
	 * 
	 * @param ruleData the rulke data object
	 */
	void visit(RuleData ruleData);

	/**
	 * Visit a {@code FillRuleData} object.
	 * 
	 * @param areaData
	 */
	void visit(FillRuleData areaData);

	/**
	 * Visit a {@code LineRuleData} object.
	 * 
	 * @param lineData
	 */
	void visit(LineRuleData lineData);

	/**
	 * Visit a {@code SymbolRuleData} object.
	 * 
	 * @param symbolData
	 */
	void visit(SymbolRuleData symbolData);

	/**
	 * Visit a {@code TextRuleData} object.
	 * 
	 * @param textData
	 */
	void visit(TextRuleData textData);

	/**
	 * Once a visitor is done processing extracted data this method may be invoked
	 * for post processing. E.g. a visitor may collect elements produced but may
	 * defer appending child nodes until all nodes are known and their relationships
	 * has been determined.
	 */
	default void done() {
	}
}
