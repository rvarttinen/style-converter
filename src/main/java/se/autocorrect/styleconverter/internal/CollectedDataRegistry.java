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


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import se.autocorrect.styleconverter.internal.data.AreaData;
import se.autocorrect.styleconverter.internal.data.ElementData;
import se.autocorrect.styleconverter.internal.data.ElementDataVisitorAdapter;
import se.autocorrect.styleconverter.internal.data.LineData;
import se.autocorrect.styleconverter.internal.data.SymbolData;
import se.autocorrect.styleconverter.internal.data.TextData;
import se.autocorrect.styleconverter.internal.data.rule.RuleData;
import se.autocorrect.styleconverter.internal.data.rule.SymbolRuleData;
import se.autocorrect.styleconverter.internal.layer.Resettable;
import se.autocorrect.styleconverter.internal.layer.RuleGeneratorContext;
import se.autocorrect.styleconverter.internal.layer.SourceLayers;
import se.autocorrect.styleconverter.util.Pair;

public final class CollectedDataRegistry {

	private static CollectedDataRegistry instance = new CollectedDataRegistry();

	private final Map<String, List<ElementData>> sourceLayers = new LinkedHashMap<>();
	private final Map<String, List<Pair<RuleData, ElementData>>> addedExtraClasses = new LinkedHashMap<>();
	private final Map<String, List<String>> classesAccordingToInput = new LinkedHashMap<>();

	private final Map<String, List<SymbolData>> symbolDatas = new LinkedHashMap<>();
	private final Map<String, List<LineData>> lineDatas = new LinkedHashMap<>();
	private final Map<String, List<AreaData>> fillDatas = new LinkedHashMap<>();
	private final Map<String, List<TextData>> textDatas = new LinkedHashMap<>();

	private final Map<String, Map<? extends RuleData, List<SymbolRuleData>>> toBeReplacedEnhancedElements = new HashMap<>();

	private final List<String> processedIds = new ArrayList<>();
	private final Map<String, Consumer<RuleGeneratorContext>> ruleConsumers = new HashMap<>();

	private final Set<Resettable> resettables = new HashSet<>();

	private CollectedDataRegistry() {
	}

	public static CollectedDataRegistry getInstance() {
		return instance;
	}

	public List<RuleData> getRuleDataForSourceLayer(String sourceLayer) {

		List<ElementData> elementDataForSourceLayer = getElementDataForSourceLayer(sourceLayer);

		return elementDataForSourceLayer.stream().filter(ed -> ed instanceof RuleData).map(RuleData.class::cast)
				.toList();
	}

	public List<ElementData> getElementDataForSourceLayer(String sourceLayer) {
		return sourceLayers.get(sourceLayer);
	}

	public void addToSourceLayer(String sourceLayer, ElementData data) {

		if (sourceLayers.containsKey(sourceLayer)) {

			sourceLayers.get(sourceLayer).add(data);

		} else {

			List<ElementData> elements = new ArrayList<>();
			elements.add(data);

			sourceLayers.put(sourceLayer, elements);
		}

		data.accept(new InsertionVisitor(sourceLayer));
	}

	public void addRuleConsumerForSourceLayer(SourceLayers sourceLayer,
			Consumer<RuleGeneratorContext> ruleSpecificConsumer) {

		ruleConsumers.computeIfAbsent(sourceLayer.getSourceLayer(), key -> ruleSpecificConsumer);
	}

	public Optional<Consumer<RuleGeneratorContext>> getRuleConsumerForSourceLayer(SourceLayers sourceLayer) {
		return Optional.ofNullable(ruleConsumers.get(sourceLayer.getSourceLayer()));
	}

	public Optional<Consumer<RuleGeneratorContext>> getRuleConsumerForSourceLayer(String sourceLayer) {
		return Optional.ofNullable(ruleConsumers.get(sourceLayer));
	}

	public Map<String, List<SymbolData>> getSymbolData() {
		return Collections.unmodifiableMap(symbolDatas);
	}

	public Map<String, List<LineData>> getLineData() {
		return Collections.unmodifiableMap(lineDatas);
	}

	public Map<String, List<AreaData>> getAreaData() {
		return Collections.unmodifiableMap(fillDatas);
	}

	public Map<String, List<TextData>> getTextData() {
		return Collections.unmodifiableMap(textDatas);
	}

	public Map<String, List<ElementData>> getSourceLayers() {
		return Collections.unmodifiableMap(sourceLayers);
	}

	public void addReplacementsForSourceLayer(String sourceLayer,
			Map<SymbolRuleData, List<SymbolRuleData>> toBeReplacedWithRuleData) {

		if (toBeReplacedWithRuleData.size() > 0) {

			toBeReplacedEnhancedElements.computeIfPresent(sourceLayer, (key, existingMap) -> {

				Stream<Entry<? extends RuleData, List<SymbolRuleData>>> combined = Stream
						.concat(existingMap.entrySet().stream(), toBeReplacedWithRuleData.entrySet().stream());

				Map<? extends RuleData, List<SymbolRuleData>> collect = combined
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

				return collect;
			});

			toBeReplacedEnhancedElements.computeIfAbsent(sourceLayer, v -> {
				return toBeReplacedWithRuleData;
			});
		}
	}

	public void processReplacements() {

		toBeReplacedEnhancedElements.forEach((sourceLayer, replacements) -> {

			replacements.forEach((toBeReplaced, with) -> {

				List<ElementData> elementsForSourceLayer = sourceLayers.get(sourceLayer);

				if (elementsForSourceLayer != null) {

					elementsForSourceLayer.remove(toBeReplaced);
					elementsForSourceLayer.addAll(with);
				}
			});
		});
	}

	public void pruneData() {

		// TODO: iterate over entries and for same source layer prune away duplicates of
		// i.e class ref:s
		// TODO: remove other elements referring these classes
		Set<Pair<RuleData, ElementData>> toBeRemoved = new HashSet<>();

		sourceLayers.forEach((sourceLayer, elements) -> {

			List<String> classes = classesAccordingToInput.get(sourceLayer);
			List<Pair<RuleData, ElementData>> added = addedExtraClasses.get(sourceLayer);

			if (classes != null && added != null) {

				// Those whose classes we need to remove from syntheticically added classes
				Set<String> intersection = added.stream().distinct().flatMap(p -> p.getLeft().getClassNodes().stream())
						.filter(classes::contains).collect(Collectors.toSet());

				toBeRemoved.addAll(added.stream().filter(p -> p.getLeft().getClassNodes().containsAll(intersection))
						.collect(Collectors.toSet()));
			}
		});

		// Remove added elements data according to what appears in intersection
		toBeRemoved.forEach(r -> {

			RuleData ruleToBeRemoved = r.getLeft();
			ElementData elementToBeRemoved = r.getRight();

			String sourceLayer = ruleToBeRemoved.getLayer().getSourceLayer();

			List<ElementData> elements = sourceLayers.get(sourceLayer);

			elements.remove(ruleToBeRemoved);
			elements.remove(elementToBeRemoved);
		});
	}

	public void addNaturalClassNode(String sourceLayer, String name) {

		classesAccordingToInput.computeIfPresent(sourceLayer, (sl, classes) -> {

			classes.add(name);
			return classes;
		});

		classesAccordingToInput.computeIfAbsent(sourceLayer, k -> {

			List<String> classes = new ArrayList<>();
			classes.add(name);

			return classes;
		});
	}

	public void addSyntheticClassNode(String sourceLayer, RuleData element, SymbolData sd) {

		addedExtraClasses.computeIfPresent(sourceLayer, (sl, classes) -> {

			classes.add(Pair.of(element, sd));
			return classes;
		});

		addedExtraClasses.computeIfAbsent(sourceLayer, k -> {

			List<Pair<RuleData, ElementData>> classes = new ArrayList<>();
			classes.add(Pair.of(element, sd));

			return classes;
		});
	}

	public void addResettable(Resettable resettable) {

		boolean noPreviousOfSameClass = !resettables.stream().anyMatch(r -> r.getClass().isInstance(resettable));

		if (noPreviousOfSameClass) {
			resettables.add(resettable);
		}
	}

	public Set<Resettable> getResetables() {
		return resettables;
	}

	public void addProcessed(String id) {
		processedIds.add(id);
	}

	public boolean isProcessed(String id) {
		return processedIds.contains(id);
	}

	public void reset() {

		sourceLayers.clear();
		addedExtraClasses.clear();
		classesAccordingToInput.clear();
		toBeReplacedEnhancedElements.clear();

		processedIds.clear();
		ruleConsumers.clear();

		textDatas.clear();
		symbolDatas.clear();
		lineDatas.clear();
		fillDatas.clear();

		resettables.clear();
	}

	private class InsertionVisitor extends ElementDataVisitorAdapter {

		private String sourceLayer;

		InsertionVisitor(String sourceLayer) {
			this.sourceLayer = sourceLayer;
		}

		@Override
		public void visit(AreaData areaData) {

			if (fillDatas.containsKey(sourceLayer)) {

				fillDatas.get(sourceLayer).add(areaData);

			} else {

				List<AreaData> elements = new ArrayList<>();
				elements.add(areaData);

				fillDatas.put(sourceLayer, elements);
			}
		}

		@Override
		public void visit(LineData lineData) {

			if (lineDatas.containsKey(sourceLayer)) {

				lineDatas.get(sourceLayer).add(lineData);

			} else {

				List<LineData> elements = new ArrayList<>();
				elements.add(lineData);

				lineDatas.put(sourceLayer, elements);
			}
		}

		@Override
		public void visit(SymbolData symbolData) {

			if (symbolDatas.containsKey(sourceLayer)) {

				symbolDatas.get(sourceLayer).add(symbolData);

			} else {

				List<SymbolData> elements = new ArrayList<>();
				elements.add(symbolData);

				symbolDatas.put(sourceLayer, elements);
			}
		}

		@Override
		public void visit(TextData textData) {

			if (textDatas.containsKey(sourceLayer)) {

				textDatas.get(sourceLayer).add(textData);

			} else {

				List<TextData> elements = new ArrayList<>();
				elements.add(textData);

				textDatas.put(sourceLayer, elements);
			}
		}
	}
}
