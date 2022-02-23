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
import com.google.common.collect.Lists;
import junit.framework.TestCase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests com.google.common.base.Suppliers.
 *
 * @author Laurence Gonsalves
 * @author Harry Heymann
 */
@GwtCompatible(emulated = true)
public class SuppliersTest extends TestCase {

    static class CountingSupplier implements Supplier<Integer> {
        int calls = 0;

        @Override
        public Integer get() {
            calls++;
            return calls * 10;
        }

        @Override
        public String toString() {
            return "CountingSupplier";
        }
    }

    static class ThrowingSupplier implements Supplier<Integer> {
        @Override
        public Integer get() {
            throw new NullPointerException();
        }
    }

    static class SerializableCountingSupplier extends CountingSupplier implements Serializable {
        private static final long serialVersionUID = 0L;
    }

    static class SerializableThrowingSupplier extends ThrowingSupplier implements Serializable {
        private static final long serialVersionUID = 0L;
    }

    static void checkMemoize(CountingSupplier countingSupplier, Supplier<Integer> memoizedSupplier) {
        // the underlying supplier hasn't executed yet
        assertThat(countingSupplier.calls).isEqualTo(0);

        assertThat((int) memoizedSupplier.get()).isEqualTo(10);

        // now it has
        assertThat(countingSupplier.calls).isEqualTo(1);

        assertThat((int) memoizedSupplier.get()).isEqualTo(10);

        // it still should only have executed once due to memoization
        assertThat(countingSupplier.calls).isEqualTo(1);
    }

    public void testMemoize() {
        memoizeTest(new CountingSupplier());
        memoizeTest(new SerializableCountingSupplier());
    }

    private void memoizeTest(CountingSupplier countingSupplier) {
        Supplier<Integer> memoizedSupplier = Suppliers.memoize(countingSupplier);
        checkMemoize(countingSupplier, memoizedSupplier);
    }

    public void testMemoize_redudantly() {
        memoize_redudantlyTest(new CountingSupplier());
        memoize_redudantlyTest(new SerializableCountingSupplier());
    }

    private void memoize_redudantlyTest(CountingSupplier countingSupplier) {
        Supplier<Integer> memoizedSupplier = Suppliers.memoize(countingSupplier);
        assertThat(Suppliers.memoize(memoizedSupplier)).isSameAs(memoizedSupplier);
    }

    public void testMemoizeExceptionThrown() {
        memoizeExceptionThrownTest(new ThrowingSupplier());
        memoizeExceptionThrownTest(new SerializableThrowingSupplier());
    }

    private void memoizeExceptionThrownTest(ThrowingSupplier throwingSupplier) {
        Supplier<Integer> memoizedSupplier = Suppliers.memoize(throwingSupplier);
        // call get() twice to make sure that memoization doesn't interfere
        // with throwing the exception
        for (int i = 0; i < 2; i++) {
            try {
                memoizedSupplier.get();
                fail("failed to throw NullPointerException");
            } catch (NullPointerException e) {
                // this is what should happen
            }
        }
    }

    public void testCompose() {
        Supplier<Integer> fiveSupplier =
                new Supplier<Integer>() {
                    @Override
                    public Integer get() {
                        return 5;
                    }
                };

        Function<Number, Integer> intValueFunction =
                new Function<Number, Integer>() {
                    @Override
                    public Integer apply(Number x) {
                        return x.intValue();
                    }
                };

        Supplier<Integer> squareSupplier = Suppliers.compose(intValueFunction, fiveSupplier);

        assertThat(squareSupplier.get()).isEqualTo(Integer.valueOf(5));
    }

    public void testComposeWithLists() {
        Supplier<ArrayList<Integer>> listSupplier =
                new Supplier<ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> get() {
                        return Lists.newArrayList(0);
                    }
                };

        Function<List<Integer>, List<Integer>> addElementFunction =
                new Function<List<Integer>, List<Integer>>() {
                    @Override
                    public List<Integer> apply(List<Integer> list) {
                        ArrayList<Integer> result = Lists.newArrayList(list);
                        result.add(1);
                        return result;
                    }
                };

        Supplier<List<Integer>> addSupplier = Suppliers.compose(addElementFunction, listSupplier);

        List<Integer> result = addSupplier.get();
        assertThat(result.get(0)).isEqualTo(Integer.valueOf(0));
        assertThat(result.get(1)).isEqualTo(Integer.valueOf(1));
    }

    @GwtIncompatible // Thread.sleep
    public void testMemoizeWithExpiration() throws InterruptedException {
        CountingSupplier countingSupplier = new CountingSupplier();

        Supplier<Integer> memoizedSupplier =
                Suppliers.memoizeWithExpiration(countingSupplier, 75, TimeUnit.MILLISECONDS);

        checkExpiration(countingSupplier, memoizedSupplier);
    }

    @GwtIncompatible // Thread.sleep
    private void checkExpiration(
            CountingSupplier countingSupplier, Supplier<Integer> memoizedSupplier)
            throws InterruptedException {
        // the underlying supplier hasn't executed yet
        assertThat(countingSupplier.calls).isEqualTo(0);

        assertThat((int) memoizedSupplier.get()).isEqualTo(10);
        // now it has
        assertThat(countingSupplier.calls).isEqualTo(1);

        assertThat((int) memoizedSupplier.get()).isEqualTo(10);
        // it still should only have executed once due to memoization
        assertThat(countingSupplier.calls).isEqualTo(1);

        Thread.sleep(150);

        assertThat((int) memoizedSupplier.get()).isEqualTo(20);
        // old value expired
        assertThat(countingSupplier.calls).isEqualTo(2);

        assertThat((int) memoizedSupplier.get()).isEqualTo(20);
        // it still should only have executed twice due to memoization
        assertThat(countingSupplier.calls).isEqualTo(2);
    }

    public void testOfInstanceSuppliesSameInstance() {
        Object toBeSupplied = new Object();
        Supplier<Object> objectSupplier = Suppliers.ofInstance(toBeSupplied);
        assertThat(objectSupplier.get()).isSameAs(toBeSupplied);
        assertThat(objectSupplier.get()).isSameAs(toBeSupplied); // idempotent
    }

    public void testOfInstanceSuppliesNull() {
        Supplier<Integer> nullSupplier = Suppliers.ofInstance(null);
        assertThat(nullSupplier.get()).isNull();
    }

    @GwtIncompatible // Thread

    public void testExpiringMemoizedSupplierThreadSafe() throws Throwable {
        Function<Supplier<Boolean>, Supplier<Boolean>> memoizer =
                new Function<Supplier<Boolean>, Supplier<Boolean>>() {
                    @Override
                    public Supplier<Boolean> apply(Supplier<Boolean> supplier) {
                        return Suppliers.memoizeWithExpiration(supplier, Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                    }
                };
        testSupplierThreadSafe(memoizer);
    }

    @GwtIncompatible // Thread

    public void testMemoizedSupplierThreadSafe() throws Throwable {
        Function<Supplier<Boolean>, Supplier<Boolean>> memoizer =
                new Function<Supplier<Boolean>, Supplier<Boolean>>() {
                    @Override
                    public Supplier<Boolean> apply(Supplier<Boolean> supplier) {
                        return Suppliers.memoize(supplier);
                    }
                };
        testSupplierThreadSafe(memoizer);
    }

    @GwtIncompatible // Thread
    public void testSupplierThreadSafe(Function<Supplier<Boolean>, Supplier<Boolean>> memoizer)
            throws Throwable {
        final AtomicInteger count = new AtomicInteger(0);
        final AtomicReference<Throwable> thrown = new AtomicReference<>(null);
        final int numThreads = 3;
        final Thread[] threads = new Thread[numThreads];
        final long timeout = TimeUnit.SECONDS.toNanos(60);

        final Supplier<Boolean> supplier =
                new Supplier<Boolean>() {
                    boolean isWaiting(Thread thread) {
                        switch (thread.getState()) {
                            case BLOCKED:
                            case WAITING:
                            case TIMED_WAITING:
                                return true;
                            default:
                                return false;
                        }
                    }

                    int waitingThreads() {
                        int waitingThreads = 0;
                        for (Thread thread : threads) {
                            if (isWaiting(thread)) {
                                waitingThreads++;
                            }
                        }
                        return waitingThreads;
                    }

                    @Override
                    public Boolean get() {
                        // Check that this method is called exactly once, by the first
                        // thread to synchronize.
                        long t0 = System.nanoTime();
                        while (waitingThreads() != numThreads - 1) {
                            if (System.nanoTime() - t0 > timeout) {
                                thrown.set(
                                        new TimeoutException(
                                                "timed out waiting for other threads to block"
                                                        + " synchronizing on supplier"));
                                break;
                            }
                            Thread.yield();
                        }
                        count.getAndIncrement();
                        return Boolean.TRUE;
                    }
                };

        final Supplier<Boolean> memoizedSupplier = memoizer.apply(supplier);

        for (int i = 0; i < numThreads; i++) {
            threads[i] =
                    new Thread() {
                        @Override
                        public void run() {
                            assertThat(memoizedSupplier.get()).isSameAs(Boolean.TRUE);
                        }
                    };
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        if (thrown.get() != null) {
            throw thrown.get();
        }
        assertThat(count.get()).isEqualTo(1);
    }

    @GwtIncompatible // Thread

    public void testSynchronizedSupplierThreadSafe() throws InterruptedException {
        final Supplier<Integer> nonThreadSafe =
                new Supplier<Integer>() {
                    int counter = 0;

                    @Override
                    public Integer get() {
                        int nextValue = counter + 1;
                        Thread.yield();
                        counter = nextValue;
                        return counter;
                    }
                };

        final int numThreads = 10;
        final int iterations = 1000;
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] =
                    new Thread() {
                        @Override
                        public void run() {
                            for (int j = 0; j < iterations; j++) {
                                Suppliers.synchronizedSupplier(nonThreadSafe).get();
                            }
                        }
                    };
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        assertThat((int) nonThreadSafe.get()).isEqualTo(numThreads * iterations + 1);
    }

    public void testSupplierFunction() {
        Supplier<Integer> supplier = Suppliers.ofInstance(14);
        Function<Supplier<Integer>, Integer> supplierFunction = Suppliers.supplierFunction();

        assertThat((int) supplierFunction.apply(supplier)).isEqualTo(14);
    }
}
