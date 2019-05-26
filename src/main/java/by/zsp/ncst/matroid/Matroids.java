package by.zsp.ncst.matroid;

import by.zsp.ncst.exception.MatroidException;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Edge;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Matroids {

    public static <V> @NotNull Matroid<Edge<V>> cycleMatroidOf(final @NotNull Graph<V> graph) {
        return new CycleMatroid<>(graph);
    }

    public static <V> @NotNull Matroid<Edge<V>> intersectionMatroidOf(final @NotNull Graph<V> graph)
            throws MatroidException {

        return new IntersectionMatroid<>(graph, true);
    }

    public static <V> @NotNull Matroid<Edge<V>> cycleMatroidWithFixedEdgesOf(
            final @NotNull Graph<V> graph,
            final @NotNull Set<Edge<V>> fixedEdges) {

        return new MatroidWithFixedElements<>(cycleMatroidOf(graph), fixedEdges);
    }

    public static <V> @NotNull Matroid<Edge<V>> intersectionMatroidWithFixedEdgesOf(
            final @NotNull Graph<V> graph,
            final @NotNull Set<Edge<V>> fixedEdges) {

        return new MatroidWithFixedElements<>(intersectionMatroidOf(graph), fixedEdges);
    }
}
