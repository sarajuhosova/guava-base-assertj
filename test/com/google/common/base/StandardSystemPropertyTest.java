/*
 * Copyright (C) 2012 The Guava Authors
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

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StandardSystemProperty}.
 *
 * @author Kurt Alfred Kluever
 */
public class StandardSystemPropertyTest extends TestCase {

    public void testGetKeyMatchesString() {
        for (StandardSystemProperty property : StandardSystemProperty.values()) {
            String fieldName = property.name();
            String expected = Ascii.toLowerCase(fieldName).replaceAll("_", ".");
            assertThat(property.key()).isEqualTo(expected);
        }
    }

    public void testGetValue() {
        for (StandardSystemProperty property : StandardSystemProperty.values()) {
            assertThat(property.value()).isEqualTo(System.getProperty(property.key()));
        }
    }

    public void testToString() {
        for (StandardSystemProperty property : StandardSystemProperty.values()) {
            assertThat(property.toString()).isEqualTo(property.key() + "=" + property.value());
        }
    }
}
