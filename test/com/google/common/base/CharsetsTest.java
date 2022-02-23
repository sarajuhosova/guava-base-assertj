/*
 * Copyright (C) 2007 The Guava Authors
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
import com.google.common.annotations.GwtIncompatible;
import junit.framework.TestCase;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link Charsets}.
 *
 * @author Mike Bostock
 */
@GwtCompatible(emulated = true)
public class CharsetsTest extends TestCase {

    @GwtIncompatible // Non-UTF-8 Charset
    public void testUsAscii() {
        assertThat(Charsets.US_ASCII).isEqualTo(StandardCharsets.US_ASCII);
    }

    @GwtIncompatible // Non-UTF-8 Charset
    public void testIso88591() {
        assertThat(Charsets.ISO_8859_1).isEqualTo(StandardCharsets.ISO_8859_1);
    }

    public void testUtf8() {
        assertThat(Charsets.UTF_8).isEqualTo(StandardCharsets.UTF_8);
    }

    @GwtIncompatible // Non-UTF-8 Charset
    public void testUtf16be() {
        assertThat(Charsets.UTF_16BE).isEqualTo(StandardCharsets.UTF_16BE);
    }

    @GwtIncompatible // Non-UTF-8 Charset
    public void testUtf16le() {
        assertThat(Charsets.UTF_16LE).isEqualTo(StandardCharsets.UTF_16LE);
    }

    @GwtIncompatible // Non-UTF-8 Charset
    public void testUtf16() {
        assertThat(Charsets.UTF_16).isEqualTo(StandardCharsets.UTF_16);
    }

    @GwtIncompatible // Non-UTF-8 Charset
    public void testWhyUsAsciiIsDangerous() {
        byte[] b1 = "朝日新聞".getBytes(Charsets.US_ASCII);
        byte[] b2 = "聞朝日新".getBytes(Charsets.US_ASCII);
        byte[] b3 = "????".getBytes(Charsets.US_ASCII);
        byte[] b4 = "ニュース".getBytes(Charsets.US_ASCII);
        byte[] b5 = "スューー".getBytes(Charsets.US_ASCII);
        // Assert they are all equal (using the transitive property)
        assertThat(Arrays.equals(b1, b2)).isTrue();
        assertThat(Arrays.equals(b2, b3)).isTrue();
        assertThat(Arrays.equals(b3, b4)).isTrue();
        assertThat(Arrays.equals(b4, b5)).isTrue();
    }
}
