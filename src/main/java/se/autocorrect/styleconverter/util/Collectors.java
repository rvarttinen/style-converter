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
package se.autocorrect.styleconverter.util;


import static java.util.stream.Collectors.collectingAndThen;

import java.util.List;
import java.util.stream.Collector;

/**
 * Implementations of Collector that implement various useful reduction
 * operations, such as collecting a single element.
 */
public final class Collectors {

    private Collectors(){}

    /**
     * Returns a Collector that collects the single input element.
     *
     * @param <T> collected type from the reduction operation
     * @return the Collector which collects the single input element
     */
    public static <T> Collector<T, ?, T> toSingleElement() {

        return collectingAndThen(toList(), list -> {

                    if (list.size() > 1) {
                        throw new IllegalArgumentException("More than one element encountered: " + list.size());
                    } else if (list.isEmpty()) {
                        return null;
                    }
                    return list.get(0);
                }
        );
    }

    /**
	 * A pass-through implementation of the
	 * {@code java.util.stream.Collectors#toList()} method for convenience. So we do
	 * not need to import two different classes with the same actual name.
	 * 
	 * @param <T> the type of the input elements
	 * @return a {@code Collector} which collects all the input elements into a
	 *         {@code List}, in the order encountered
	 */
    public static <T> Collector<T, ?, List<T>> toList() {
    	return java.util.stream.Collectors.toList();
    }
}
