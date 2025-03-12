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


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;


class TryTest {

    @Test
    void testSuccessOf() {

        Object expected = new Object();

        Try<Object> actual = Try.of(() -> expected);

        assertAll(
                () -> assertNotNull(actual),
                () -> assertTrue(actual.isSuccess()),
                () -> assertEquals(expected, actual.get()),
                () -> assertThrows(NoSuchElementException.class, actual::getCause)
        );
    }

    @Test
    void testFailureOf() {

        Try<Object> actual = Try.of(this::exceptionThrowingMethod);

        assertAll(
                () -> assertNotNull(actual),
                () -> assertFalse(actual.isSuccess()),
                () -> assertNull(actual.get()),

                () -> {
                    Throwable cause = actual.getCause();
                    assertAll(
                            () -> assertEquals("test", cause.getMessage()),
                            () -> assertInstanceOf(IllegalArgumentException.class, cause)
                    );
                }
        );
    }

    @Test
    void testCheckedFailureOf() {

        Try<Object> actual = Try.of(this::checkedExceptionThrowingMethod);

        assertAll(
                () -> assertNotNull(actual),
                () -> assertFalse(actual.isSuccess()),
                () -> assertNull(actual.get()),

                () -> {
                    Throwable cause = actual.getCause();
                    assertAll(
                            () -> assertEquals("checked test", cause.getMessage()),
                            () -> assertInstanceOf(Exception.class, cause)
                    );
                }
        );
    }

    @Test
    void testShouldFlatMapOnSuccess() {

        Try<String> t = Try.of(() -> "5");
        Monad<Integer> actual = t.flatMap(s -> Try.of(() -> Integer.parseInt(s)));

        assertEquals(5, ((Try<Integer>) actual).get().intValue());
    }

    @Test
    void testShouldFlatMapOnFailure() {

        Try<Object> t = Try.of(this::exceptionThrowingMethod);

        Monad<String> actual = t.flatMap(s -> Try.of(() -> "5"));

        assertAll(
                () -> assertFalse(((Try<String>) actual).isSuccess()),
                () -> {
                    Throwable cause = ((Try<String>) actual).getCause();
                    assertAll(
                            () -> assertEquals("test", cause.getMessage()),
                            () -> assertInstanceOf(IllegalArgumentException.class, cause)
                    );
                }
        );
    }

    @Test
    void testShouldFlatMapOnCheckedFailure() {

        Try<Object> t = Try.of(this::checkedExceptionThrowingMethod);

        Monad<String> actual = t.flatMap(s -> Try.of(() -> "5"));

        assertAll(
                () -> assertFalse(((Try<String>) actual).isSuccess()),
                () -> {
                    Throwable cause = ((Try<String>) actual).getCause();
                    assertAll(
                            () -> assertEquals("checked test", cause.getMessage()),
                            () -> assertInstanceOf(Exception.class, cause)
                    );
                }
        );
    }

    @Test
    void testShouldFlatMapOnFailureFromSuccess() {

        Try<String> t = Try.of(() -> "5");

        Monad<Object> actual = t.flatMap(s -> Try.of(this::exceptionThrowingMethod));

        assertAll(
                () -> assertFalse(((Try<Object>) actual).isSuccess()),
                () -> {
                    Throwable cause = ((Try<Object>) actual).getCause();
                    assertAll(
                            () -> assertEquals("test", cause.getMessage()),
                            () -> assertInstanceOf(IllegalArgumentException.class, cause)
                    );
                }
        );
    }

    @Test
    void testShouldFlatMapOnCheckedFailureFromSuccess() {

        Try<String> t = Try.of(() -> "5");

        Monad<Object> actual = t.flatMap(s -> Try.of(this::checkedExceptionThrowingMethod));

        assertAll(
                () -> assertFalse(((Try<Object>) actual).isSuccess()),
                () -> {
                    Throwable cause = ((Try<Object>) actual).getCause();
                    assertAll(
                            () -> assertEquals("checked test", cause.getMessage()),
                            () -> assertInstanceOf(Exception.class, cause)
                    );
                }
        );
    }

    @Test
    void testCastToTry() {

        assertAll(
                () -> assertNotNull(Try.castToTry(Try.of(() -> 1))),
                () -> assertNull(Try.castToTry(null)),
                () -> assertThrows(ClassCastException.class, () -> Try.castToTry(GenericMonad.of(1)))
        );
    }

    @Test
    void testOrElseOk() {

        final int expected = 1;
        final String stringValue = String.valueOf(expected);

        int actual = Try.of(() -> Integer.parseInt(stringValue)).orElse(0);

        assertEquals(1, actual);
    }

    @Test
    void testOrElseNotOk() {

        final int someDefaultValue = 42;

        final int expected = someDefaultValue;
        final String stringValue = "some non integer string";

        int actual = Try.of(() -> Integer.parseInt(stringValue)).orElse(someDefaultValue);

        assertEquals(expected, actual);
    }

    @Test
    void testOrElseNull() {

        final int someDefaultValue = 42;

        final int expected = someDefaultValue;

        int actual = Try.of(() -> Integer.parseInt(null)).orElse(someDefaultValue);

        assertEquals(expected, actual);
    }

    @Test
    void testStream() {

        Stream<Integer> expected = Stream.of(1, 2);

        Stream<Try<Integer>> tryStream = Stream.of(Try.of(() -> 1), Try.of(() -> 2));

        Stream<Integer> actual = tryStream.flatMap(Try::stream);

        assertStreamEquals(expected, actual);
    }
    
    @Test
    void testIfFailure() {
    	
    	Try<Object> actual = Try.of(this::checkedExceptionThrowingMethod);
    	Throwable actualException = actual.getCause();
    	
    	Logger logger = mock(Logger.class);
    	doNothing().when(logger).log(eq(Level.SEVERE), eq("Error"), eq(actualException));
    	
    	actual.ifFailure(exception -> logger.log(Level.SEVERE, "Error", exception));
    
    	verify(logger).log(eq(Level.SEVERE), eq("Error"), eq(actualException));
    }
    
    @Test
    void testIfSuccess() {
    	
    	Object expected = new Object();

        Try<Object> success = Try.of(() -> expected);
        
        success.ifSuccess(actual -> assertEquals(expected, actual));
    }
    
    @Test
    void testPeek() {
    	
    	Try<Integer> expected = Try.of(()-> 1);
    	
    	List<Integer> list = new ArrayList<>();
    	
    	Try<Integer> actual = expected.peek(list::add);
    	
    	assertAll(
    			() -> assertEquals(actual, expected),
    			() -> assertFalse(list.isEmpty())
    			);
    }
    
    static void assertStreamEquals(Stream<?> expected, Stream<?> actual){

        Iterator<?> itActual = actual.iterator();
        assertTrue(expected.allMatch(o -> itActual.hasNext() && Objects.equals(o, itActual.next())) && !itActual.hasNext(),
                () -> "Streams differ");
    }
    
    private Object exceptionThrowingMethod() {
        throw new IllegalArgumentException("test");
    }

    private Object checkedExceptionThrowingMethod() throws Exception {
        throw new Exception("checked test");
    }
}
