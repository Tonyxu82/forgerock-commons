/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2015 ForgeRock AS.
 */
package org.forgerock.util.promise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.forgerock.util.AsyncFunction;
import org.forgerock.util.Function;

/**
 * Utility methods for creating and composing {@link Promise}s.
 */
public final class Promises {
    // TODO: n-of, etc.

    private static abstract class CompletedPromise<V, E extends Exception> implements Promise<V, E> {
        @Override
        public final boolean cancel(final boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public final V get() throws ExecutionException {
            if (hasResult()) {
                return getResult();
            } else {
                throw new ExecutionException(getException());
            }
        }

        @Override
        public final V get(final long timeout, final TimeUnit unit) throws ExecutionException {
            return get();
        }

        @Override
        public final V getOrThrow() throws E {
            if (hasResult()) {
                return getResult();
            } else {
                throw getException();
            }
        }

        @Override
        public final V getOrThrow(final long timeout, final TimeUnit unit) throws E {
            return getOrThrow();
        }

        @Override
        public final V getOrThrowUninterruptibly() throws E {
            return getOrThrow();
        }

        @Override
        public final V getOrThrowUninterruptibly(final long timeout, final TimeUnit unit) throws E {
            return getOrThrow();
        }

        @Override
        public final boolean isCancelled() {
            return false;
        }

        @Override
        public final boolean isDone() {
            return true;
        }

        @Override
        public final Promise<V, E> thenOnException(final ExceptionHandler<? super E> onException) {
            if (!hasResult()) {
                onException.handleException(getException());
            }
            return this;
        }

        @Override
        public final Promise<V, E> thenOnResult(final ResultHandler<? super V> onResult) {
            if (hasResult()) {
                onResult.handleResult(getResult());
            }
            return this;
        }

        @Override
        public final Promise<V, E> thenOnResultOrException(final ResultHandler<? super V> onResult,
                                                       final ExceptionHandler<? super E> onException) {
            return thenOnResult(onResult).thenOnException(onException);
        }

        @Override
        public final Promise<V, E> thenOnResultOrException(final Runnable onResultOrException) {
            onResultOrException.run();
            return this;
        }

        @Override
        public final <VOUT> Promise<VOUT, E> then(final Function<? super V, VOUT, E> onResult) {
            return then(onResult, Promises.<VOUT, E>exceptionIdempotentFunction());
        }

        @Override
        public <EOUT extends Exception> Promise<V, EOUT> thenCatch(Function<? super E, V, EOUT> onException) {
            return then(Promises.<V, EOUT>resultIdempotentFunction(), onException);
        }

        @Override
        @SuppressWarnings("unchecked")
        public final <VOUT, EOUT extends Exception> Promise<VOUT, EOUT> then(
                final Function<? super V, VOUT, EOUT> onResult,
                final Function<? super E, VOUT, EOUT> onException) {
            try {
                if (hasResult()) {
                    return newResultPromise(onResult.apply(getResult()));
                } else {
                    return newResultPromise(onException.apply(getException()));
                }
            } catch (final Exception e) {
                return newExceptionPromise((EOUT) e);
            }
        }


        @Override
        public final Promise<V, E> thenAlways(final Runnable onResultOrException) {
            return thenOnResultOrException(onResultOrException);
        }

        @Override
        public Promise<V, E> thenFinally(Runnable onResultOrException) {
            return thenOnResultOrException(onResultOrException);
        }

        @Override
        public final <VOUT> Promise<VOUT, E> thenAsync(
                final AsyncFunction<? super V, VOUT, E> onResult) {
            return thenAsync(onResult, Promises.<VOUT, E>exceptionIdempotentAsyncFunction());
        }

        @Override
        public final <EOUT extends Exception> Promise<V, EOUT> thenCatchAsync(
                final AsyncFunction<? super E, V, EOUT> onException) {
            return thenAsync(Promises.<V, EOUT>resultIdempotentAsyncFunction(), onException);
        }

        @Override
        @SuppressWarnings("unchecked")
        public final <VOUT, EOUT extends Exception> Promise<VOUT, EOUT> thenAsync(
                final AsyncFunction<? super V, VOUT, EOUT> onResult,
                final AsyncFunction<? super E, VOUT, EOUT> onException) {
            try {
                if (hasResult()) {
                    return onResult.apply(getResult());
                } else {
                    return onException.apply(getException());
                }
            } catch (final Exception e) {
                return newExceptionPromise((EOUT) e);
            }
        }

        abstract E getException();

        abstract V getResult();

        abstract boolean hasResult();
    }

    private static final class ExceptionPromise<V, E extends Exception> extends CompletedPromise<V, E> {
        private final E exception;

        private ExceptionPromise(final E exception) {
            this.exception = exception;
        }

        @Override
        E getException() {
            return exception;
        }

        @Override
        V getResult() {
            throw new IllegalStateException();
        }

        @Override
        boolean hasResult() {
            return false;
        }
    }

    private static final class ResultPromise<V, E extends Exception> extends
            CompletedPromise<V, E> {
        private final V value;

        private ResultPromise(final V value) {
            this.value = value;
        }

        @Override
        E getException() {
            throw new IllegalStateException();
        }

        @Override
        V getResult() {
            return value;
        }

        @Override
        boolean hasResult() {
            return true;
        }
    }

    private static final AsyncFunction<Exception, Object, Exception> EXCEPTION_IDEM_ASYNC_FUNC =
            new AsyncFunction<Exception, Object, Exception>() {
                @Override
                public Promise<Object, Exception> apply(final Exception exception) throws Exception {
                    throw exception;
                }
            };

    private static final Function<Exception, Object, Exception> EXCEPTION_IDEM_FUNC =
            new Function<Exception, Object, Exception>() {
                @Override
                public Object apply(final Exception exception) throws Exception {
                    throw exception;
                }
            };

    private static final AsyncFunction<Exception, Object, Exception> RESULT_IDEM_ASYNC_FUNC =
            new AsyncFunction<Exception, Object, Exception>() {
                @Override
                public Promise<Object, Exception> apply(final Exception exception) throws Exception {
                    throw exception;
                }
            };

    private static final Function<Object, Object, Exception> RESULT_IDEM_FUNC =
            new Function<Object, Object, Exception>() {
                @Override
                public Object apply(final Object value) throws Exception {
                    return value;
                }
            };

    /**
     * Returns a {@link Promise} representing an asynchronous task which has
     * already failed with the provided exception. Attempts to get the result will
     * immediately fail, and any listeners registered against the returned
     * promise will be immediately invoked in the same thread as the caller.
     *
     * @param <V>
     *            The type of the task's result, or {@link Void} if the task
     *            does not return anything (i.e. it only has side-effects).
     * @param <E>
     *            The type of the exception thrown by the task if it fails, or
     *            {@link NeverThrowsException} if the task cannot fail.
     * @param exception
     *            The exception indicating why the asynchronous task has failed.
     * @return A {@link Promise} representing an asynchronous task which has
     *         already failed with the provided exception.
     */
    public static final <V, E extends Exception> Promise<V, E> newExceptionPromise(final E exception) {
        return new ExceptionPromise<V, E>(exception);
    }

    /**
     * Returns a {@link Promise} representing an asynchronous task which has
     * already succeeded with the provided result. Attempts to get the result
     * will immediately return the result, and any listeners registered against
     * the returned promise will be immediately invoked in the same thread as
     * the caller.
     *
     * @param <V>
     *            The type of the task's result, or {@link Void} if the task
     *            does not return anything (i.e. it only has side-effects).
     * @param <E>
     *            The type of the exception thrown by the task if it fails, or
     *            {@link NeverThrowsException} if the task cannot fail.
     * @param result
     *            The result of the asynchronous task.
     * @return A {@link Promise} representing an asynchronous task which has
     *         already succeeded with the provided result.
     */
    public static final <V, E extends Exception> Promise<V, E> newResultPromise(final V result) {
        return new ResultPromise<V, E>(result);
    }

    /**
     * Returns a {@link Promise} which will be completed once all of the
     * provided promises have succeeded, or as soon as one of them fails.
     *
     * @param <V>
     *            The type of the tasks' result, or {@link Void} if the tasks do
     *            not return anything (i.e. they only has side-effects).
     * @param <E>
     *            The type of the exception thrown by the tasks if they fail, or
     *            {@link NeverThrowsException} if the tasks cannot fail.
     * @param promises
     *            The list of tasks to be combined.
     * @return A {@link Promise} which will be completed once all of the
     *         provided promises have succeeded, or as soon as one of them
     *         fails.
     */
    public static final <V, E extends Exception> Promise<List<V>, E> when(
            final List<Promise<V, E>> promises) {
        final int size = promises.size();
        final AtomicInteger remaining = new AtomicInteger(size);
        final List<V> results = new ArrayList<V>(size);
        final PromiseImpl<List<V>, E> composite = PromiseImpl.create();
        for (final Promise<V, E> promise : promises) {
            promise.thenOnResult(new ResultHandler<V>() {
                @Override
                public void handleResult(final V value) {
                    synchronized (results) {
                        results.add(value);
                    }
                    if (remaining.decrementAndGet() == 0) {
                        composite.handleResult(results);
                    }
                }
            }).thenOnException(new ExceptionHandler<E>() {
                @Override
                public void handleException(final E exception) {
                    composite.handleException(exception);
                }
            });
        }
        if (promises.isEmpty()) {
            composite.handleResult(results);
        }
        return composite;
    }

    /**
     * Returns a {@link Promise} which will be completed once all of the
     * provided promises have succeeded, or as soon as one of them fails.
     *
     * @param <V>
     *            The type of the tasks' result, or {@link Void} if the tasks do
     *            not return anything (i.e. they only has side-effects).
     * @param <E>
     *            The type of the exception thrown by the tasks if they fail, or
     *            {@link NeverThrowsException} if the tasks cannot fail.
     * @param promises
     *            The list of tasks to be combined.
     * @return A {@link Promise} which will be completed once all of the
     *         provided promises have succeeded, or as soon as one of them
     *         has thrown an exception.
     */
    public static final <V, E extends Exception> Promise<List<V>, E> when(
            final Promise<V, E>... promises) {
        return when(Arrays.asList(promises));
    }

    @SuppressWarnings("unchecked")
    static <VOUT, E extends Exception> AsyncFunction<E, VOUT, E> exceptionIdempotentAsyncFunction() {
        return (AsyncFunction<E, VOUT, E>) EXCEPTION_IDEM_ASYNC_FUNC;
    }

    @SuppressWarnings("unchecked")
    static <VOUT, E extends Exception> Function<E, VOUT, E> exceptionIdempotentFunction() {
        return (Function<E, VOUT, E>) EXCEPTION_IDEM_FUNC;
    }

    @SuppressWarnings("unchecked")
    static <V, E extends Exception> AsyncFunction<V, V, E> resultIdempotentAsyncFunction() {
        return (AsyncFunction<V, V, E>) RESULT_IDEM_ASYNC_FUNC;
    }

    @SuppressWarnings("unchecked")
    static <V, E extends Exception> Function<V, V, E> resultIdempotentFunction() {
        return (Function<V, V, E>) RESULT_IDEM_FUNC;
    }

    private Promises() {
        // Prevent instantiation.
    }
}
