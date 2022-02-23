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
import com.google.common.testing.GcFinalization;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.NoSuchElementException;
import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@code AbstractIterator}.
 *
 * @author Kevin Bourrillion
 */
@GwtCompatible(emulated = true)
public class AbstractIteratorTest extends TestCase {

  public void testDefaultBehaviorOfNextAndHasNext() {

    // This sample AbstractIterator returns 0 on the first call, 1 on the
    // second, then signals that it's reached the end of the data
    Iterator<Integer> iter =
        new AbstractIterator<Integer>() {
          private int rep;

          @Override
          public Integer computeNext() {
            switch (rep++) {
              case 0:
                return 0;
              case 1:
                return 1;
              case 2:
                return endOfData();
              default:
                fail("Should not have been invoked again");
                return null;
            }
          }
        };

    assertThat(iter.hasNext()).isTrue();
    assertThat((int) iter.next()).isEqualTo(0);

    // verify idempotence of hasNext()
    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.hasNext()).isTrue();
    assertThat(iter.hasNext()).isTrue();
    assertThat((int) iter.next()).isEqualTo(1);

    assertThat(iter.hasNext()).isFalse();

    // Make sure computeNext() doesn't get invoked again
    assertThat(iter.hasNext()).isFalse();

    try {
      iter.next();
      fail("no exception thrown");
    } catch (NoSuchElementException expected) {
    }
  }

  public void testSneakyThrow() throws Exception {
    Iterator<Integer> iter =
        new AbstractIterator<Integer>() {
          boolean haveBeenCalled;

          @Override
          public Integer computeNext() {
            if (haveBeenCalled) {
              fail("Should not have been called again");
            } else {
              haveBeenCalled = true;
              sneakyThrow(new SomeCheckedException());
            }
            return null; // never reached
          }
        };

    // The first time, the sneakily-thrown exception comes out
    try {
      iter.hasNext();
      fail("No exception thrown");
    } catch (Exception e) {
      if (!(e instanceof SomeCheckedException)) {
        throw e;
      }
    }

    // But the second time, AbstractIterator itself throws an ISE
    try {
      iter.hasNext();
      fail("No exception thrown");
    } catch (IllegalStateException expected) {
    }
  }

  public void testException() {
    final SomeUncheckedException exception = new SomeUncheckedException();
    Iterator<Integer> iter =
        new AbstractIterator<Integer>() {
          @Override
          public Integer computeNext() {
            throw exception;
          }
        };

    // It should pass through untouched
    try {
      iter.hasNext();
      fail("No exception thrown");
    } catch (SomeUncheckedException e) {
      assertThat(e).isSameAs(exception);
    }
  }

  public void testExceptionAfterEndOfData() {
    Iterator<Integer> iter =
        new AbstractIterator<Integer>() {
          @Override
          public Integer computeNext() {
            endOfData();
            throw new SomeUncheckedException();
          }
        };
    try {
      iter.hasNext();
      fail("No exception thrown");
    } catch (SomeUncheckedException expected) {
    }
  }

  public void testCantRemove() {
    Iterator<Integer> iter =
        new AbstractIterator<Integer>() {
          boolean haveBeenCalled;

          @Override
          public Integer computeNext() {
            if (haveBeenCalled) {
              endOfData();
            }
            haveBeenCalled = true;
            return 0;
          }
        };

    assertThat((int) iter.next()).isEqualTo(0);

    try {
      iter.remove();
      fail("No exception thrown");
    } catch (UnsupportedOperationException expected) {
    }
  }


  @GwtIncompatible // weak references
  public void testFreesNextReference() {
    Iterator<Object> itr =
        new AbstractIterator<Object>() {
          @Override
          public Object computeNext() {
            return new Object();
          }
        };
    WeakReference<Object> ref = new WeakReference<>(itr.next());
    GcFinalization.awaitClear(ref);
  }

  public void testReentrantHasNext() {
    Iterator<Integer> iter =
        new AbstractIterator<Integer>() {
          @Override
          protected Integer computeNext() {
            boolean unused = hasNext();
            return null;
          }
        };
    try {
      iter.hasNext();
      fail();
    } catch (IllegalStateException expected) {
    }
  }

  // Technically we should test other reentrant scenarios (4 combinations of
  // hasNext/next), but we'll cop out for now, knowing that
  // next() both start by invoking hasNext() anyway.

  /** Throws an undeclared checked exception. */
  private static void sneakyThrow(Throwable t) {
    class SneakyThrower<T extends Throwable> {
      @SuppressWarnings("unchecked") // intentionally unsafe for test
      void throwIt(Throwable t) throws T {
        throw (T) t;
      }
    }
    new SneakyThrower<Error>().throwIt(t);
  }

  private static class SomeCheckedException extends Exception {}

  private static class SomeUncheckedException extends RuntimeException {}
}
