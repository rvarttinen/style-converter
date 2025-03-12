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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static se.autocorrect.styleconverter.util.Collectors.toSingleElement;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;


class CollectorsTest {

    @Test
    void testToSingleElementOk() {

        String expected = "test";

        String actual = Arrays.stream(new String[]{expected}).collect(toSingleElement());

        assertEquals(expected, actual);
    }

    @Test
    void testToSingleElementNotOkMultiple() {

        final Stream<String> stream = Arrays.stream(new String[]{"test1", "test2"});

        IllegalArgumentException actualException = assertThrows(IllegalArgumentException.class,

                () -> stream.collect(toSingleElement())
        );

        assertEquals("More than one element encountered: 2", actualException.getMessage());
    }

    @Test
    void testToSingleElementNotOkZeroSize() {

        assertNull(Arrays.stream(new String[]{}).collect(toSingleElement()));
    }
}
