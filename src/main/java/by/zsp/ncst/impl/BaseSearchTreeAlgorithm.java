package by.zsp.ncst.impl;

import by.zsp.ncst.exception.AlgorithmException;
import by.zsp.ncst.graph.Graph;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BaseSearchTreeAlgorithm {

    private static boolean TRACE = false;

    @SuppressWarnings("OptionalAssignedToNull")
    protected static <V> @NotNull Optional<Graph<V>> find(
            final @NotNull TaskSupplier<V> task)
            throws AlgorithmException {

        final AtomicInteger nodesCreated = new AtomicInteger(0);
        final AtomicInteger nodesProcessed = new AtomicInteger(0);
        final ForkJoinPool pool = new ForkJoinPool();
        Optional<Graph<V>> ncst = null;

        try {
            final ForkJoinTask<Optional<Graph<V>>> executionTask =
                    pool.submit(task.supply(nodesCreated, nodesProcessed));

            do {
                try {
                    ncst = executionTask.get(1, TimeUnit.SECONDS);
                } catch (final TimeoutException e) {
                    final int processed = nodesProcessed.get();
                    final int created = nodesCreated.get();

                    System.out.println(String.format(
                            "%d created / %d processed / %d left", created, processed, created - processed));
                } catch (final Exception e) {
                    throw new AlgorithmException(e);
                }
            } while (ncst == null);
        } finally {
            pool.shutdown();
        }

        return ncst;
    }

    protected static void logNode(
            final @NotNull String message, final boolean isFinal, final int depth, final @NotNull String index) {

        final String color = isFinal ? "\u001B[33m" : "\u001B[34m";
        final String reset = "\u001B[0m";

        if (TRACE) {
            System.out.println(String.format("%s%d:%s / %s%s", color, depth, index, message, reset));
        }
    }

    public static void enableTracing() {
        TRACE = true;
    }

    public static void disableTracing() {
        TRACE = false;
    }

    @FunctionalInterface
    protected interface TaskSupplier<V> {

        @NotNull RecursiveTask<Optional<Graph<V>>> supply(
                @NotNull AtomicInteger nodesCreated,
                @NotNull AtomicInteger nodesProcessed);
    }

    protected static abstract class BaseSearchTreeNode<V> extends RecursiveTask<Optional<Graph<V>>> {

        protected final @NotNull AtomicInteger nodesCreated;
        protected final @NotNull AtomicInteger nodesProcessed;
        protected final int depth;
        protected final @NotNull String index;
        protected final @NotNull Graph<V> graph;

        protected BaseSearchTreeNode(
                final @NotNull AtomicInteger nodesCreated,
                final @NotNull AtomicInteger nodesProcessed,
                final int depth,
                final @NotNull String index,
                final @NotNull Graph<V> graph) {

            this.nodesCreated = nodesCreated;
            this.nodesProcessed = nodesProcessed;
            this.depth = depth;
            this.index = index;
            this.graph = graph;

            nodesCreated.incrementAndGet();
        }

        @Override
        protected @NotNull Optional<Graph<V>> compute() {
            final Optional<Graph<V>> result = _compute();
            nodesProcessed.incrementAndGet();
            return result;
        }

        protected abstract @NotNull Optional<Graph<V>> _compute();
    }
}
