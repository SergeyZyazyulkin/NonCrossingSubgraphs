package by.zsp.ncst.matroid;

import by.zsp.ncst.exception.MatroidException;
import by.zsp.ncst.graph.Edge;
import by.zsp.ncst.graph.Graph;
import by.zsp.ncst.graph.Vertex;
import by.zsp.ncst.util.annotation.Mutable;
import by.zsp.ncst.util.annotation.NotEmpty;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class CycleMatroid<V> extends GraphMatroid<V> {

    CycleMatroid(@NotNull Graph<V> graph) throws MatroidException {
        super(graph);

        if (graph.isDirected()) {
            throw new MatroidException("Directed graphs aren't supported");
        }
    }

    @Override
    protected Optional<@NotEmpty Set<Edge<V>>> findCircuitChecked(final @NotNull @NotEmpty Set<Edge<V>> subset) {
        final ImmutableSet<Vertex<V>> vertices = subset.stream()
                .flatMap(Edge::asStream)
                .collect(ImmutableSet.toImmutableSet());

        return findCircuit(vertices, subset);
    }

    private Optional<@NotEmpty Set<Edge<V>>> findCircuit(
            final @NotNull Set<Vertex<V>> vertices,
            final @NotNull Set<Edge<V>> edges) {

        final Map<Vertex<V>, Vertex<V>> ancestors = new HashMap<>();
        Optional<Vertex<V>> repeated = Optional.empty();

        for (final Vertex<V> vertex : vertices) {
            if (!ancestors.containsKey(vertex)) {
                ancestors.put(vertex, null);
                repeated = dfs(vertex, edges, ancestors);

                if (repeated.isPresent()) {
                    break;
                }
            }
        }

        if (repeated.isPresent()) {
            final Set<Edge<V>> circuit = new HashSet<>();
            final Vertex<V> first = repeated.get();
            Vertex<V> current = first;

            do {
                assert ancestors.containsKey(current);
                final @NotNull Vertex<V> next = ancestors.get(current);
                circuit.add(graph.edgeOf(next, current));
                current = next;
            } while (!Objects.equals(current, first));

            return Optional.of(circuit);
        } else {
            return Optional.empty();
        }
    }

    // returns first repeated vertex
    // ancestors must form a cycle if a repeated vertex is found
    private Optional<Vertex<V>> dfs(
            final @NotNull Vertex<V> current,
            final @NotNull Set<Edge<V>> edges,
            final @NotNull @Mutable Map<Vertex<V>, Vertex<V>> ancestors) {

        assert ancestors.containsKey(current);

        for (final Vertex<V> neighbour : graph.getNeighbours(current)) {
            if (edges.contains(graph.edgeOf(current, neighbour))) {
                if (ancestors.containsKey(neighbour)) {
                    if (!Objects.equals(neighbour, ancestors.get(current))) {
                        ancestors.put(neighbour, current);
                        return Optional.of(neighbour);
                    }
                } else {
                    ancestors.put(neighbour, current);
                    final Optional<Vertex<V>> repeated = dfs(neighbour, edges, ancestors);

                    if (repeated.isPresent()) {
                        return repeated;
                    }
                }
            }
        }

        return Optional.empty();
    }
}

