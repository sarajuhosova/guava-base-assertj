/*
 * Copyright (C) 2010 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link Strings}.
 *
 * @author Kevin Bourrillion
 */
@GwtCompatible(emulated = true)
public class StringsTest extends TestCase {
    public void testNullToEmpty() {
        assertEquals("", Strings.nullToEmpty(null));
        assertEquals("", Strings.nullToEmpty(""));
        assertThat(Strings.nullToEmpty("a")).isEqualTo("a");
    }

    public void testEmptyToNull() {
        assertThat(Strings.emptyToNull(null)).isNull();
        assertThat(Strings.emptyToNull("")).isNull();
        assertThat(Strings.emptyToNull("a")).isEqualTo("a");
    }

    public void testIsNullOrEmpty() {
        assertThat(Strings.isNullOrEmpty(null)).isTrue();
        assertThat(Strings.isNullOrEmpty("")).isTrue();
        assertThat(Strings.isNullOrEmpty("a")).isFalse();
    }

    // TODO: could remove if we got NPT working in GWT somehow
    public void testPadStart_null() {
        try {
            Strings.padStart(null, 5, '0');
            fail();
        } catch (NullPointerException expected) {
        }
    }

    // TODO: could remove if we got NPT working in GWT somehow
    public void testPadEnd_null() {
        try {
            Strings.padEnd(null, 5, '0');
            fail();
        } catch (NullPointerException expected) {
        }
    }

    // TODO: could remove if we got NPT working in GWT somehow
    public void testRepeat_null() {
        try {
            Strings.repeat(null, 5);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testCommonPrefix() {
        assertEquals("", Strings.commonPrefix("", ""));
        assertEquals("", Strings.commonPrefix("abc", ""));
        assertThat(Strings.commonPrefix("abc", "aaaaa")).isEqualTo("a");
        assertThat(Strings.commonPrefix("aa", "aaaaa")).isEqualTo("aa");

        // Identical valid surrogate pairs.
        assertEquals(
                "abc\uD8AB\uDCAB", Strings.commonPrefix("abc\uD8AB\uDCABdef", "abc\uD8AB\uDCABxyz"));
        // Differing valid surrogate pairs.
        assertThat(Strings.commonPrefix("abc\uD8AB\uDCABdef", "abc\uD8AB\uDCACxyz")).isEqualTo("abc");
        // One invalid pair.
        assertThat(Strings.commonPrefix("abc\uD8AB\uDCABdef", "abc\uD8AB\uD8ABxyz")).isEqualTo("abc");
        // Two identical invalid pairs.
        assertEquals(
                "abc\uD8AB\uD8AC", Strings.commonPrefix("abc\uD8AB\uD8ACdef", "abc\uD8AB\uD8ACxyz"));
        // Two differing invalid pairs.
        assertThat(Strings.commonPrefix("abc\uD8AB\uD8ABdef", "abc\uD8AB\uD8ACxyz")).isEqualTo("abc\uD8AB");
        // Two orphan high surrogates.
        assertThat(Strings.commonPrefix("\uD8AB", "\uD8AB")).isEqualTo("\uD8AB");
    }

    public void testCommonSuffix() {
        assertEquals("", Strings.commonSuffix("", ""));
        assertEquals("", Strings.commonSuffix("abc", ""));
        assertThat(Strings.commonSuffix("abc", "ccccc")).isEqualTo("c");
        assertThat(Strings.commonSuffix("aa", "aaaaa")).isEqualTo("aa");

        // Identical valid surrogate pairs.
        assertEquals(
                "\uD8AB\uDCABdef", Strings.commonSuffix("abc\uD8AB\uDCABdef", "xyz\uD8AB\uDCABdef"));
        // Differing valid surrogate pairs.
        assertThat(Strings.commonSuffix("abc\uD8AB\uDCABdef", "abc\uD8AC\uDCABdef")).isEqualTo("def");
        // One invalid pair.
        assertThat(Strings.commonSuffix("abc\uD8AB\uDCABdef", "xyz\uDCAB\uDCABdef")).isEqualTo("def");
        // Two identical invalid pairs.
        assertEquals(
                "\uD8AB\uD8ABdef", Strings.commonSuffix("abc\uD8AB\uD8ABdef", "xyz\uD8AB\uD8ABdef"));
        // Two differing invalid pairs.
        assertThat(Strings.commonSuffix("abc\uDCAB\uDCABdef", "abc\uDCAC\uDCABdef")).isEqualTo("\uDCABdef");
        // Two orphan low surrogates.
        assertThat(Strings.commonSuffix("\uDCAB", "\uDCAB")).isEqualTo("\uDCAB");
    }

    public void testValidSurrogatePairAt() {
        assertThat(Strings.validSurrogatePairAt("\uD8AB\uDCAB", 0)).isTrue();
        assertThat(Strings.validSurrogatePairAt("abc\uD8AB\uDCAB", 3)).isTrue();
        assertThat(Strings.validSurrogatePairAt("abc\uD8AB\uDCABxyz", 3)).isTrue();
        assertThat(Strings.validSurrogatePairAt("\uD8AB\uD8AB", 0)).isFalse();
        assertThat(Strings.validSurrogatePairAt("\uDCAB\uDCAB", 0)).isFalse();
        assertThat(Strings.validSurrogatePairAt("\uD8AB\uDCAB", -1)).isFalse();
        assertThat(Strings.validSurrogatePairAt("\uD8AB\uDCAB", 1)).isFalse();
        assertThat(Strings.validSurrogatePairAt("\uD8AB\uDCAB", -2)).isFalse();
        assertThat(Strings.validSurrogatePairAt("\uD8AB\uDCAB", 2)).isFalse();
        assertThat(Strings.validSurrogatePairAt("x\uDCAB", 0)).isFalse();
        assertThat(Strings.validSurrogatePairAt("\uD8ABx", 0)).isFalse();
    }
}
