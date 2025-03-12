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
package se.autocorrect.styleconverter.internal.data.rule;


import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.autocorrect.styleconverter.internal.data.ElementData;
import se.autocorrect.styleconverter.internal.data.ElementDataVisitor;
import se.autocorrect.styleconverter.internal.data.TagWithValues;
import se.autocorrect.styleconverter.json.JsonStyleLayerData;

// TODO: generation from rule data to be performed "outside" of this class complex and moved to rules package.

public class RuleData implements ElementData {

	protected String id;
	protected String layerType;
	protected String joinedClasses;
	protected String zoomMin;
	protected String sourcelayer;
	protected List<String> classNodes;

	protected JsonStyleLayerData layer;

	protected Element ruleClassParentElement;
	protected TagWithValues tagWithValues;
	protected boolean filterNegate;

	protected RuleData(String id, JsonStyleLayerData layer, String joinedClasses, String zoomMin, String sourcelayer,
			List<String> classNodes, TagWithValues tagWithValues, boolean filternegate) {

		this.id = id;
		this.layer = layer;
		this.layerType = layer.getType();
		this.joinedClasses = joinedClasses;
		this.zoomMin = zoomMin;
		this.sourcelayer = sourcelayer;
		this.classNodes = classNodes;
		this.tagWithValues = tagWithValues;
		this.filterNegate = filternegate;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getJoinedClasses() {
		return joinedClasses;
	}

	public void setCurrentClassParent(Element ruleClassParentElement) {
		this.ruleClassParentElement = ruleClassParentElement;
	}

	public Element getRuleClassParentElement() {
		return ruleClassParentElement;
	}

	public JsonStyleLayerData getLayer() {
		return layer;
	}

	public String getLayerType() {
		return layerType;
	}

	public String getZoomMin() {
		return zoomMin;
	}

	public String getSourcelayer() {
		return sourcelayer;
	}

	public List<String> getClassNodes() {
		return classNodes;
	}

	public TagWithValues getTagWithValues() {
		return tagWithValues;
	}

	public boolean isFilterNegate() {
		return filterNegate;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ElementData.Builder<RuleData> {

		protected String id;
		protected JsonStyleLayerData layer;
		protected String layerType;
		protected String joinedClasses;
		protected String zoomMin;
		protected String sourceLayer;
		protected List<String> classNodes;
		protected TagWithValues tagWithValues;

		protected boolean filterNegate = false;

		protected Builder() {
		}

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder classNodes(List<String> classNodes) {
			this.classNodes = classNodes;
			return this;
		}

		public Builder matchClasses(String joinedClasses) {
			this.joinedClasses = joinedClasses;
			return this;
		}

		public Builder zoomMin(String zoomMin) {
			this.zoomMin = zoomMin;
			return this;
		}

		public Builder sourceLayer(String sourceLayer) {
			this.sourceLayer = sourceLayer;
			return this;
		}

		public Builder layerType(String layerType) {
			this.layerType = layerType;
			return this;
		}

		public Builder layer(JsonStyleLayerData layer) {
			this.layer = layer;
			return this;
		}

		public Builder tagWithVales(TagWithValues tagWithValues) {
			this.tagWithValues = tagWithValues;
			return this;
		}

		public Builder negate() {
			this.filterNegate = true;
			return this;
		}

		@Override
		public RuleData build() {
			return new RuleData(id, layer, joinedClasses, zoomMin, sourceLayer, classNodes, tagWithValues,
					filterNegate);
		}
	}

	@Override
	public Element generate(Document document) {

		Element subClassElement = null;

		if (!layerType.equals("symbol")) {

			if (joinedClasses != null) {

				subClassElement = document.createElement("m");
				subClassElement.setAttribute("v", joinedClasses);

				Element useElement = null;

				if (layerType.equals("line")) {

					useElement = document.createElement("line");
					useElement.setAttribute("outline", id);

				} else {

					useElement = document.createElement("area");
				}

				useElement.setAttribute("use", id);
				subClassElement.appendChild(useElement);

			} // else{

			// Optional<LayerClasses> layerClassesOp =
			// LayerClasses.getLayerClassesForLayer(sourcelayer);
			//
			// // TODO: come up with better way to handle "water"
			// if (layerClassesOp.isPresent() && sourcelayer.equals(id)) {
			//
			// Optional<Element> subClsElement = layerClassesOp.map(lc -> {
			//
			// Collection<String> classesForLayer = lc.getClassesForLayer();
			//
			// Element subClassElem = document.createElement("m");
			// subClassElem.setAttribute("v", String.join("|", classesForLayer));
			//
			// Element useElem = null;
			//
			// if (layerType.equals("line")) {
			//
			// useElem = document.createElement("line");
			// useElem.setAttribute("outline", id);
			//
			// } else {
			//
			// useElem = document.createElement("area");
			// }
			//
			// useElem.setAttribute("use", id);
			// subClassElem.appendChild(useElem);
			//
			// return subClassElem;
			// });
			//
			// subClassElement = subClsElement.isPresent() ? subClsElement.get() : null;
			// }
			// }
		}

		return subClassElement;
	}

	@Override
	public void accept(ElementDataVisitor visitor) {
		visitor.visit(this);
	}
}
