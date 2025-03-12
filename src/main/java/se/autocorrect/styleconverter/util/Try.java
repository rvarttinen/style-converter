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


import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * The {@code Try} enables writing safe code without focusing on try-catch
 * blocks. {@code Try} is a monadic type.
 * <p>
 * Hopefully we will see a similar mechanism in a future version of the JDK.
 * </p>
 *
 * @param <T> the type held if processing is successful
 */
public abstract class Try<T> implements Monad<T> {

	private Try() {
	}

	/**
	 * Creates a Try of a {@code CheckedSupplier}. This method has the capability to
	 * report the exception resulting from {@code supplier.get()}.
	 *
	 * @param supplier A supplier
	 * @param <T>      the type held if processing is successful
	 * @return A Success holding the result of {@code supplier.get()} if no
	 *         exception occurs, otherwise a Failure holding the throwable if an
	 *         exception occurs calling {@code supplier.get()}.
	 */
	public static <T> Try<T> of(CheckedSupplier<? extends T> supplier) {

		try {
			return new Success<>(supplier.get());
		} catch (Throwable t) {
			return new Failure<>(t);
		}
	}

	/**
	 * Convenience method for casting a {@code Monad} object to a {@code Try}.
	 *
	 * @param <T>   the type held in the {@code Monad}
	 * @param monad the monad to cast
	 * @return an instance of this type, {@code null} if {@code null} provided
	 * @throws ClassCastException if the instance provided is not of type
	 *                            {@code Try}
	 */
	@SuppressWarnings("unchecked")
	public static <T> Try<T> castToTry(Monad<T> monad) {
		return Try.class.cast(monad);
	}

	/**
	 * Checks if this is a success.
	 *
	 * @return {@code true} is this is a success, {@code false} otherwise
	 */
	public abstract boolean isSuccess();

	/**
	 * Checks if this is a failure.
	 *
	 * @return {@code true} is this is a failure, {@code false} otherwise
	 */
	public abstract boolean isFailure();

	/**
	 * Gets the result of this {@code Try} if this is a success or {@code null} if
	 * this is a failure.
	 *
	 * @return the result of this {@code Try}.
	 */
	public abstract T get();

	/**
	 * If this {@code Try} is a success returns the held value, otherwise returns
	 * {@code other}.
	 *
	 * @param other the other value to return if {@code this} is not a success
	 * @return the value, if success, otherwise {@code other}
	 * @apiNote This method is inspired by the monadic class {@code Optional} in the
	 *          Java platform.
	 */
	public T orElse(T other) {
		return this.isSuccess() ? this.get() : other;
	}

	/**
	 * Gets the cause if this is a failure or throws
	 * {@code UnsupportedOperationException} if this is a success.
	 *
	 * @return The cause if this is a failure
	 * @throws UnsupportedOperationException if this is a success
	 */
	public abstract Throwable getCause();

	/**
	 * If this {@code Try} is a success and thus holds a value, returns a sequential
	 * {@code Stream} containing only that value. Otherwise, an empty stream is
	 * returned.
	 *
	 * @apiNote This method can be used to transform a {@code Stream} of
	 *          {@code Try}:s into a {@code Stream} of actual held elements:
	 * 
	 *          <pre>{@code
	 *     Stream<Try<T>> tries = ..
	 *     Stream<T> s = tries.flatMap(Try::stream)
	 * }
	 * </pre>
	 *
	 * @return the value as a {@code Stream}
	 */
	public Stream<T> stream() {

		if (isFailure()) {
			return Stream.empty();
		} else {
			return Stream.of(get());
		}
	}

	/**
	 * Convert this {@code Try} to an {@code Optional} holding the value if success,
	 * {@code Optional#empty()} if failure.
	 * 
	 * @return an {@code Optional} harboring the value if success,
	 *         {@code Optional#empty()} otherwise
	 */
	public Optional<T> toOptional() {
		return Optional.ofNullable(get());
	}

	/**
	 * If this {@code Try} is a failure then perform the given action.
	 *
	 * @param action the action to be performed, if this {@code Try} is a failure
	 * @throws NullPointerException if this {@code Try} is a failure and the given
	 *                              action is {@code null}
	 */
	public void ifFailure(Consumer<? super Throwable> action) {
		if (isFailure()) {
			action.accept(getCause());
		}
	}

	/**
	 * If this {@code Try} is a success, then perform the given action with the
	 * value, otherwise does nothing.
	 *
	 * @param action the action to be performed, if this {@code Try} is a success
	 * @throws NullPointerException if this {@code Try} is a success and the given
	 *                              action is {@code null}
	 */
	public void ifSuccess(Consumer<? super T> action) {
		if (isSuccess()) {
			action.accept(get());
		}
	}

	/**
	 * If this {@code Try} is a success invoke the supplied function and return its
	 * value, otherwise returns {@code defaultValue} which may be {@code null}.
	 * 
	 * @param <R>          the type of the result of the supplied function
	 * @param function     the supplied function
	 * @param defaultValue the value to return if this is not a success or if the
	 *                     invocation fails for some reason
	 * @return the result on the function invocation if successful, otherwise the
	 *         {@code defaultValue}
	 * 
	 * @apiNote There is no corresponding method for failures, use one of the
	 *          recover methods in that case.
	 * @see Try#recover(Class, Function)
	 * @see Try#recover(Class, Object)
	 */
	public <R> R onSuccess(CheckedFunction<T, R> function, R defaultValue) {

		if (isSuccess()) {
			try {
				return function.apply(get());
			} catch (Throwable t) {
			}
		}

		return defaultValue;
	}

	/**
	 * Applies the action to the value held if this is a success, will not do
	 * anything if this is a failure.
	 * 
	 * @param action the action
	 * @return this
	 */
	public Try<T> peek(Consumer<T> action) {

		if (isSuccess()) {
			action.accept(get());
		}

		return this;
	}

	/**
	 * Represents a supplier of results. This type of supplier may throw an
	 * exception.
	 *
	 * @param <R> the type of results supplied by this supplier
	 */
	@FunctionalInterface
	public interface CheckedSupplier<R> {

		/**
		 * Gets a result, the processing might throw an exception.
		 *
		 * @return a result
		 * @throws Throwable if the processing for some reason throws an exception
		 */
		R get() throws Throwable;
	}

	/**
	 * Represents a function that accepts one argument and produces a result. This
	 * type of function may throw an exception.
	 * 
	 * @param <T> the type of the input to the function
	 * @param <R> the type of the result of the function
	 */
	@FunctionalInterface
	public interface CheckedFunction<T, R> {

		/**
		 * Applies this function to the given argument.
		 *
		 * @param t the function argument
		 * @return the function result
		 * @throws Throwable if the processing for some reason throws an exception
		 */
		R apply(T t) throws Throwable;
	}

	private static class Success<T> extends Try<T> {

		private final T value;

		private Success(T value) {
			this.value = value;
		}

		@Override
		public boolean isSuccess() {
			return true;
		}

		@Override
		public boolean isFailure() {
			return false;
		}

		@Override
		public T get() {
			return value;
		}

		@Override
		public Throwable getCause() {
			throw new NoSuchElementException();
		}

		@Override
		public <R> Monad<R> flatMap(Function<T, Monad<R>> function) {

			try {
				return function.apply(value);
			} catch (Throwable t) {
				return new Failure<>(t);
			}
		}

		@Override
		public boolean equals(Object other) {

			if (this == other) {
				return true;
			}

			if (other == null || getClass() != other.getClass()) {
				return false;
			}

			Success<?> that = (Success<?>) other;

			return Objects.equals(value, that.value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(value);
		}
	}

	private static class Failure<T> extends Try<T> {

		private final Throwable cause;

		private Failure(Throwable cause) {
			this.cause = cause;
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public boolean isFailure() {
			return true;
		}

		@Override
		public T get() {
			return null;
		}

		@Override
		public Throwable getCause() {
			return cause;
		}

		@Override
		public <R> Monad<R> flatMap(Function<T, Monad<R>> function) {
			return new Failure<>(cause);
		}

		@Override
		public boolean equals(Object other) {

			if (this == other) {
				return true;
			}

			if (other == null || getClass() != other.getClass()) {
				return false;
			}

			Failure<?> that = (Failure<?>) other;

			return Objects.equals(cause, that.cause);
		}

		@Override
		public int hashCode() {
			return Objects.hash(cause);
		}
	}
}
