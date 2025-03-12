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


import java.util.function.Function;

/**
 * A monad can be used to abstract away boilerplate code needed for common
 * operations (such as dealing with undefined values or fallible functions).
 * Thus, monads can be used to turn complicated sequences of functions into
 * succinct pipelines that abstract away control flow, and side-effects.
 * <p>
 * Both the concept of a monad and the term is originally derived from category
 * theory, where a monad is defined as a functor with additional structure.
 * Research beginning in the late 1980s and early 1990s established that monads
 * could bring seemingly disparate computer-science problems under a unified,
 * functional model. Category theory also provides a few formal requirements,
 * known as the monad laws, which should be satisfied by any monad and can be
 * used to verify monadic code
 * </p>
 *
 * @param <T> type held by this monad
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Monad_(functional_programming)"></a>
 */
@FunctionalInterface
public interface Monad<T> {

	/**
	 * Transforms the current held value of type {@code T} to a new value of type
	 * {@code R} wrapped in a new monad.
	 *
	 * @param function function to apply
	 * @param <R>      the new type
	 * @return new composed monad
	 */
	<R> Monad<R> flatMap(Function<T, Monad<R>> function);
}
