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


import java.util.stream.IntStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.internal.data.rule.RuleCondition;

interface BoundaryGenState {

	Element generateElement(Document document);
	
	void accept(Visitor visitor);
	
	interface Visitor {
		
		void visit(AdminLevelState state);
		void visit(AdminLevelRangeState state);
		void visit(MaritimeState state);	
		void visit(DisputedState state);
	}
	
	static class AdminLevelState implements BoundaryGenState {

		private String id;
		private RuleCondition condition;

		AdminLevelState(String id, RuleCondition condition) {
			this.id = id;
			this.condition = condition;
		}
		
		String getId() {
			return id;
		}
		
		String getAdminLevel() {
			return condition.getValue();
		}

		@Override
		public Element generateElement(Document document) {
			
			Element adminLvlElement = document.createElement("m");
			adminLvlElement.setAttribute("k", getAdminLevel());
			
			return adminLvlElement;
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}
	static class AdminLevelRangeState implements BoundaryGenState {
		
		private String id;
		private RuleCondition lowerCondition;
		private RuleCondition upperCondition;

		AdminLevelRangeState(String id) {
			this.id = id;
		}
		
		String getId() {
			return id;
		}

		void setLowerCondition(RuleCondition lowerCondition) {
			this.lowerCondition = lowerCondition;
		}

		void setUpperCondition(RuleCondition upperCondition) {
			this.upperCondition = upperCondition;
		}
		
		RuleCondition getLowerCondition() {
			return lowerCondition;
		}

		RuleCondition getUpperCondition() {
			return upperCondition;
		}

		boolean isComplete() {
			return lowerCondition != null & upperCondition != null;
		}

		@Override
		public Element generateElement(Document document) {
			
			int lower = Integer.valueOf(lowerCondition.getValue());
			int upper = Integer.valueOf(upperCondition.getValue());
			
			StringBuilder admLevels = new StringBuilder();
			
			IntStream.rangeClosed(lower, upper).forEach(val -> {
				
				admLevels.append(val);
				
				if(val != upper) {
					admLevels.append('|');
				}
			});
			
			Element element = document.createElement("m");
			element.setAttribute("v", admLevels.toString());
			
			return element;
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}
	
	static class MaritimeState implements BoundaryGenState {
		
		private String id;
		private RuleCondition condition;

		MaritimeState(String id, RuleCondition condition) {
			this.id = id;
			this.condition = condition;
		}

		String getId() {
			return id;
		}
		
		String getValue() {
			return condition.getValue();
		}
		
		@Override
		public Element generateElement(Document document) {
			
			String cond = condition.getCondition();
			String condVal = "";
			
			if(cond.equals("==")||cond.equals("in")) {
				condVal = condition.getValue();
			}else {
				condVal = "-|" + condition.getValue();
			}
			
			Element maritimeElement = document.createElement("m");
			maritimeElement.setAttribute("k", "maritime");
			maritimeElement.setAttribute("v", condVal);
			
			return maritimeElement;
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}
	
	static class DisputedState implements BoundaryGenState {

		private String id;
		private RuleCondition condition;

		DisputedState(String id, RuleCondition condition) {
			this.id = id;
			this.condition = condition;
		}
		
		String getId() {
			return id;
		}
		
		@Override
		public Element generateElement(Document document) {
			
			String cond = condition.getCondition();
			String condVal = "";
			
			if(cond.equals("==")||cond.equals("in")) {
				condVal = condition.getValue();
			}else {
				condVal = "-|" + condition.getValue();
			}
			
			Element disputedElement = document.createElement("m");
			
			disputedElement.setAttribute("k", "disputed");
			disputedElement.setAttribute("v", condVal);
			
			return disputedElement;
		}
		
		@Override
		public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}
}
