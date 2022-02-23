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

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link Defaults}.
 *
 * @author Jige Yu
 */
public class DefaultsTest extends TestCase {
  public void testGetDefaultValue() {
      assertThat(Defaults.defaultValue(boolean.class).booleanValue()).isFalse();
      assertThat(Defaults.defaultValue(char.class).charValue()).isEqualTo('\0');

      assertThat(Defaults.defaultValue(byte.class).byteValue()).isEqualTo((byte) 0);
      assertThat(Defaults.defaultValue(short.class).shortValue()).isEqualTo((short) 0);
      assertThat(Defaults.defaultValue(int.class).intValue()).isEqualTo(0);
      assertThat(Defaults.defaultValue(long.class).longValue()).isEqualTo(0L);

      assertThat(Defaults.defaultValue(float.class).floatValue()).isEqualTo(0.0f);
      assertThat(Defaults.defaultValue(double.class).doubleValue()).isEqualTo(0.0d);

      assertThat(Defaults.defaultValue(void.class)).isNull();
      assertThat(Defaults.defaultValue(String.class)).isNull();
  }
}
